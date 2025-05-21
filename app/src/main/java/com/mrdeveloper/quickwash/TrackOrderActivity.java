package com.mrdeveloper.quickwash;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.List;

public class TrackOrderActivity extends AppCompatActivity {

    LinearLayout timelineContainer;
    String[] statuses = {"Pending", "Picked Up", "Processing", "Completed", "Delivered"};
    String currentStatus = "Processing"; // এই স্ট্যাটাস PHP API থেকে আনবেন

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

        timelineContainer = findViewById(R.id.timelineContainer);

        currentStatus = getIntent().getStringExtra("status");

        for (String status : statuses) {
            View view = getLayoutInflater().inflate(R.layout.timeline_item, null);
            TextView tv = view.findViewById(R.id.tvStatus);
            View circle = view.findViewById(R.id.circle);

            tv.setText(status);

            // স্ট্যাটাস রঙ পরিবর্তন
            if (isStatusReached(status, currentStatus)) {
                circle.setBackgroundResource(R.drawable.circle_checked);
            } else {
                circle.setBackgroundResource(R.drawable.circle_unchecked);
            }

            timelineContainer.addView(view);
        }
    }

    private boolean isStatusReached(String status, String currentStatus) {
        List<String> list = Arrays.asList(statuses);
        return list.indexOf(status) <= list.indexOf(currentStatus);
    }

}