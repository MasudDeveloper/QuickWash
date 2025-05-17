package com.mrdeveloper.quickwash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.mrdeveloper.quickwash.Adapter.OnboardingAdapter;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private Button btnSkip, btnNext, btnGetStarted;
    private OnboardingAdapter onboardingAdapter;
    private List<View> slides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager2 = findViewById(R.id.onboardingViewPager);
        btnSkip = findViewById(R.id.btnSkip);
        btnNext = findViewById(R.id.btnNext);
        btnGetStarted = findViewById(R.id.btnGetStarted);

        LayoutInflater inflater = LayoutInflater.from(this);
        slides = new ArrayList<>();
        slides.add(inflater.inflate(R.layout.slide1, viewPager2, false));
        slides.add(inflater.inflate(R.layout.slide2, viewPager2, false));
        slides.add(inflater.inflate(R.layout.slide3, viewPager2, false));

        onboardingAdapter = new OnboardingAdapter(slides);
        viewPager2.setAdapter(onboardingAdapter);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == slides.size() - 1) {
                    btnNext.setVisibility(View.GONE);
                    btnSkip.setVisibility(View.GONE);
                    btnGetStarted.setVisibility(View.VISIBLE);
                } else {
                    btnNext.setVisibility(View.VISIBLE);
                    btnSkip.setVisibility(View.VISIBLE);
                    btnGetStarted.setVisibility(View.GONE);
                }
            }
        });

        LinearLayout dotLayout = findViewById(R.id.dotLayout);
        int totalDots = slides.size();
        ImageView[] dots = new ImageView[totalDots];

        for (int i = 0; i < totalDots; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_inactive));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            dotLayout.addView(dots[i], params);
        }

        // Highlight first dot initially
        dots[0].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_active));

        // Then in your onPageSelected:
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < totalDots; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                            i == position ? R.drawable.dot_active : R.drawable.dot_inactive));
                }

                if (position == totalDots - 1) {
                    btnNext.setVisibility(View.GONE);
                    btnSkip.setVisibility(View.GONE);
                    btnGetStarted.setVisibility(View.VISIBLE);
                } else {
                    btnNext.setVisibility(View.VISIBLE);
                    btnSkip.setVisibility(View.VISIBLE);
                    btnGetStarted.setVisibility(View.GONE);
                }
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextItem = viewPager2.getCurrentItem() + 1;
                if (nextItem < slides.size()) {
                    viewPager2.setCurrentItem(nextItem);
                }
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager2.setCurrentItem(slides.size() - 1);
            }
        });

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to MainActivity or SignInActivity
                startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
                finishOnboarding();
                finish();
            }
        });

    } // ================== On Create end ======================

    private void finishOnboarding() {
        SharedPreferences pref = getSharedPreferences("onboarding", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("firstTime", false);
        editor.apply();

        startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
        finish();
    }



}