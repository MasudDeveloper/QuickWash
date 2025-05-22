package com.mrdeveloper.quickwash;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mrdeveloper.quickwash.Interface.ApiInterface;
import com.mrdeveloper.quickwash.Interface.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackOrderActivity extends AppCompatActivity {

    private LinearLayout timelineContainer;
    private String currentStatus;
    private int orderId;
    MaterialToolbar toolbar;
    MaterialButton btnContactSupport;

    // Status data model
    class OrderStatus {
        String status;
        String timestamp;
        String description;
        boolean isCompleted;

        OrderStatus(String status, String timestamp, String description, boolean isCompleted) {
            this.status = status;
            this.timestamp = timestamp;
            this.description = description;
            this.isCompleted = isCompleted;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_track_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        timelineContainer = findViewById(R.id.timelineContainer);
        btnContactSupport = findViewById(R.id.btnContactSupport);
        TextView tvOrderId = findViewById(R.id.tvOrderId);
        TextView tvCurrentStatus = findViewById(R.id.tvCurrentStatus);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get order data from intent
        orderId = getIntent().getIntExtra("order_id", 0);
        currentStatus = getIntent().getStringExtra("status");

        // Set order info
        tvOrderId.setText("Order #" + orderId);
        tvCurrentStatus.setText(currentStatus);

        fetchOrderStatus(orderId);

        btnContactSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:01777777777"));
                startActivity(intent);
            }
        });
    }

    private void buildTimeline(List<OrderStatus> statusList) {
        timelineContainer.removeAllViews();

        for (int i = 0; i < statusList.size(); i++) {
            OrderStatus status = statusList.get(i);
            View view = getLayoutInflater().inflate(R.layout.timeline_item, timelineContainer, false);

            // Get views
            TextView tvStatus = view.findViewById(R.id.tvStatus);
            TextView tvStatusTime = view.findViewById(R.id.tvStatusTime);
            TextView tvStatusDesc = view.findViewById(R.id.tvStatusDescription);
            ShapeableImageView circle = view.findViewById(R.id.circle);
            View connectorLine = view.findViewById(R.id.connectorLine);
            ImageView ivStatusIcon = view.findViewById(R.id.ivStatusIcon);

            // Set data
            tvStatus.setText(status.status);
            tvStatusTime.setText(status.timestamp);
            tvStatusDesc.setText(status.description);

            // Show/hide description based on completion
            if (status.isCompleted) {
                tvStatusDesc.setVisibility(View.VISIBLE);
            } else {
                tvStatusDesc.setVisibility(View.GONE);
            }

            // Set status colors and icons
            if (status.isCompleted) {
                // Completed status
                circle.setBackgroundResource(R.drawable.icon_tick);
                ivStatusIcon.setImageResource(getStatusIcon(status.status));
                tvStatus.setTextColor(ContextCompat.getColor(this, R.color.primary_text));

                // Hide connector line for last item
                if (i == statusList.size() - 1) {
                    connectorLine.setVisibility(View.GONE);
                } else {
                    connectorLine.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
                }
            } else {
                // Pending status
                circle.setBackgroundResource(R.drawable.circle_unchecked);
                ivStatusIcon.setImageResource(getStatusIcon(status.status));
                tvStatus.setTextColor(ContextCompat.getColor(this, R.color.secondary_text));
                connectorLine.setBackgroundColor(ContextCompat.getColor(this, R.color.divider_grey));
            }

            timelineContainer.addView(view);
        }
    }

    private int getStatusIcon(String status) {
        switch (status) {
            case "Pending": return R.drawable.icon_pending;
            case "Picked Up": return R.drawable.icon_picked_up;
            case "Processing": return R.drawable.img_wash_only;
            case "Quality Check": return R.drawable.img_wash_and_fold;
            case "Out for Delivery": return R.drawable.icon_premium_delivery;
            case "Delivered": return R.drawable.icon_delivery_done;
            default: return R.drawable.icon_pending;
        }
    }

    private void fetchOrderStatus(int orderId) {
        ApiInterface apiInterface = RetrofitClient.getApiService();
        Call<JsonObject> call = apiInterface.getOrderStatus(orderId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        JsonObject jsonResponse = response.body();

                        // 1. First check if the response has the basic structure
                        if (!jsonResponse.has("success") || !jsonResponse.get("success").getAsBoolean()) {
                            String errorMsg = jsonResponse.has("message") ?
                                    jsonResponse.get("message").getAsString() : "Unknown error";
                            throw new Exception("API Error: " + errorMsg);
                        }

                        // 2. Check if required fields exist
                        if (!jsonResponse.has("status_history") || !jsonResponse.has("current_status")) {
                            throw new JSONException("Missing required fields in response");
                        }

                        JsonArray statusArray = jsonResponse.getAsJsonArray("status_history");
                        List<OrderStatus> statusList = new ArrayList<>();

                        // 3. Parse each status item
                        for (JsonElement element : statusArray) {
                            JsonObject statusObj = element.getAsJsonObject();
                            statusList.add(new OrderStatus(
                                    statusObj.get("status").getAsString(),
                                    statusObj.has("timestamp") ? statusObj.get("timestamp").getAsString() : "",
                                    statusObj.has("description") ? statusObj.get("description").getAsString() : "",
                                    statusObj.get("is_completed").getAsBoolean()
                            ));
                        }

                        buildTimeline(statusList);

                        // Update current status
                        String currentStatus = jsonResponse.get("current_status").getAsString();
                        TextView tvCurrentStatus = findViewById(R.id.tvCurrentStatus);
                        tvCurrentStatus.setText(currentStatus);

                    } else {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "No error body";
                        throw new Exception("API call failed: " + response.code() + " - " + errorBody);
                    }
                } catch (Exception e) {
                    Log.e("TrackOrder", "Error processing response", e);
                    runOnUiThread(() -> {
                        Toast.makeText(TrackOrderActivity.this,
                                "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                    });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(TrackOrderActivity.this,
                            "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();

                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}