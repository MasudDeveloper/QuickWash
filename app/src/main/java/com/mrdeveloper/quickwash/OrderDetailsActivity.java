package com.mrdeveloper.quickwash;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import com.mrdeveloper.quickwash.Model.OrderRequest;
import com.mrdeveloper.quickwash.Model.ServiceItem;

public class OrderDetailsActivity extends AppCompatActivity {

    TextView tvOrderId, tvCustomerName, tvPhone, tvAddress, tvOrderDate;
    LinearLayout serviceContainer;
    Button btnViewInvoice;
    OrderRequest order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find views by ID
        tvOrderId = findViewById(R.id.tvOrderId);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        serviceContainer = findViewById(R.id.serviceContainer);
        btnViewInvoice = findViewById(R.id.btnViewInvoice);

        // Get order from intent
        order = (OrderRequest) getIntent().getSerializableExtra("order");

        // Set order details
        tvOrderId.setText("Order ID: " + order.getId());
        tvCustomerName.setText("Name: " + order.getName());
        tvPhone.setText("Phone: " + order.getPhone());
        tvAddress.setText("Address: " + order.getAddress());
        tvOrderDate.setText("Date: " + order.getCreated_at());

        // Clear existing views first
        serviceContainer.removeAllViews();

        // Add divider
        View divider = new View(this);
        divider.setBackgroundColor(ContextCompat.getColor(this, R.color.divider_grey));
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.divider_height)
        );
        dividerParams.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.padding_medium));
        divider.setLayoutParams(dividerParams);
        serviceContainer.addView(divider);

        // Dynamically add service items with enhanced design
        for (ServiceItem item : order.getService_list()) {
            // Create card for each service item
            MaterialCardView card = new MaterialCardView(this);
            card.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            card.setCardElevation(getResources().getDimension(R.dimen.card_elevation_small));
            card.setRadius(getResources().getDimension(R.dimen.card_corner_radius));
            card.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.card_stroke_width));
            card.setStrokeColor(ContextCompat.getColor(this, R.color.card_stroke));
            card.setUseCompatPadding(true);
            card.setContentPadding(
                    getResources().getDimensionPixelSize(R.dimen.padding_medium),
                    getResources().getDimensionPixelSize(R.dimen.padding_medium),
                    getResources().getDimensionPixelSize(R.dimen.padding_medium),
                    getResources().getDimensionPixelSize(R.dimen.padding_medium)
            );

            // Create inner layout
            LinearLayout cardLayout = new LinearLayout(this);
            cardLayout.setOrientation(LinearLayout.VERTICAL);
            cardLayout.setPadding(0, 0, 0, 0);

            // Service name with icon
            LinearLayout nameLayout = new LinearLayout(this);
            nameLayout.setOrientation(LinearLayout.HORIZONTAL);
            nameLayout.setGravity(Gravity.CENTER_VERTICAL);

            ImageView icon = new ImageView(this);
            icon.setImageResource(getServiceIcon(item.getCategory_name()));
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.icon_size),
                    getResources().getDimensionPixelSize(R.dimen.icon_size)
            );
            iconParams.setMargins(0, 0,
                    getResources().getDimensionPixelSize(R.dimen.padding_small), 0);
            icon.setLayoutParams(iconParams);
            nameLayout.addView(icon);

            TextView serviceName = new TextView(this);
            serviceName.setText(item.getService_name());
            serviceName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            serviceName.setTextColor(ContextCompat.getColor(this, R.color.primary_text));
            serviceName.setTypeface(null, Typeface.BOLD);
            nameLayout.addView(serviceName);

            cardLayout.addView(nameLayout);

            // Service details
            TextView category = new TextView(this);
            category.setText("Category: " + item.getCategory_name());
            category.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            category.setTextColor(ContextCompat.getColor(this, R.color.secondary_text));
            category.setPadding(0,
                    getResources().getDimensionPixelSize(R.dimen.padding_small), 0, 0);
            cardLayout.addView(category);

            // Price and quantity row
            LinearLayout priceQtyLayout = new LinearLayout(this);
            priceQtyLayout.setOrientation(LinearLayout.HORIZONTAL);
            priceQtyLayout.setGravity(Gravity.CENTER_VERTICAL);
            priceQtyLayout.setPadding(0,
                    getResources().getDimensionPixelSize(R.dimen.padding_small), 0, 0);

            TextView quantity = new TextView(this);
            quantity.setText("Qty: " + item.getQuantity());
            quantity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            quantity.setTextColor(ContextCompat.getColor(this, R.color.secondary_text));
            LinearLayout.LayoutParams qtyParams = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1
            );
            quantity.setLayoutParams(qtyParams);
            priceQtyLayout.addView(quantity);

            TextView price = new TextView(this);
            price.setText("৳" + (item.getPrice_per_item() * item.getQuantity()));
            price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            price.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            price.setTypeface(null, Typeface.BOLD);
            price.setGravity(Gravity.END);
            LinearLayout.LayoutParams priceParams = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1
            );
            price.setLayoutParams(priceParams);
            priceQtyLayout.addView(price);

            cardLayout.addView(priceQtyLayout);

            // Add layout to card
            card.addView(cardLayout);

            // Add card to container
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0,
                    getResources().getDimensionPixelSize(R.dimen.padding_small));
            serviceContainer.addView(card, cardParams);
        }

        // Helper method to get appropriate icon


        // Handle invoice button click
        btnViewInvoice.setOnClickListener(v -> {
            Bitmap invoiceBitmap = generateInvoiceBitmap(order, String.valueOf(order.getId()));
            if (invoiceBitmap != null) {
                showInvoiceDialog(invoiceBitmap);
            } else {
                Toast.makeText(this, "Invoice not generated.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private int getServiceIcon(String categoryName) {
        switch (categoryName) {
            case "Wash Only":
                return R.drawable.img_wash_only;
            case "Wash & Fold":
                return R.drawable.img_wash_and_fold;
            case "Wash & Iron":
                return R.drawable.img_wash_and_iron;
            case "Dry Cleaning":
                return R.drawable.img_dry_clean;
            case "Iron Only":
                return R.drawable.img_iron_only;
            case "Shoe Cleaning":
                return R.drawable.img_shoe_cleaning;
            case "Curtain Cleaning":
                return R.drawable.img_curtain_cleaning;
            case "Blanket Cleaning":
                return R.drawable.img_blanket_cleaning;
            default:
                return R.drawable.img_wash_only;
        }
    }

    private void showInvoiceDialog(Bitmap bitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(imageView);
        builder.setView(scrollView);
        builder.setPositiveButton("Close", null);
        builder.show();
    }

    private Bitmap generateInvoiceBitmap(OrderRequest order, String orderId) {
        int width = 800;
        int height = 1200 + (order.getService_list().size() * 60); // height depending on items
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        paint.setFakeBoldText(true);

        int y = 80;

        canvas.drawText("Invoice", 320, y, paint);
        y += 60;

        paint.setTextSize(30);
        paint.setFakeBoldText(false);

        canvas.drawText("Order ID: " + order.getId(), 40, y, paint); y += 40;
        canvas.drawText("Name: " + order.getName(), 40, y, paint); y += 40;
        canvas.drawText("Phone: " + order.getPhone(), 40, y, paint); y += 40;
        canvas.drawText("Address: " + order.getAddress(), 40, y, paint); y += 40;
        canvas.drawText("Date: " + order.getCreated_at(), 40, y, paint); y += 60;

        paint.setFakeBoldText(true);
        canvas.drawText("Service Details:", 40, y, paint); y += 40;
        paint.setFakeBoldText(false);

        for (ServiceItem item : order.getService_list()) {
            String serviceText = item.getCategory_name() + ": " + item.getService_name() +
                    " | Qty: " + item.getQuantity() +
                    " | ৳" + item.getPrice_per_item();
            canvas.drawText(serviceText, 40, y, paint);
            y += 40;
        }

        return bitmap;
    }
}