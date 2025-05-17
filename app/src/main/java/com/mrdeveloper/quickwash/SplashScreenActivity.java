package com.mrdeveloper.quickwash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import de.hdodenhof.circleimageview.CircleImageView;

public class SplashScreenActivity extends AppCompatActivity {

    CircleImageView imageView;
    ImageView textView;

    Animation imageAnimation, textAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        imageAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        textAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);

        imageView.setAnimation(imageAnimation);
        textView.setAnimation(textAnimation);

        new Handler().postDelayed(() -> {
            SharedPreferences pref = getSharedPreferences("onboarding", MODE_PRIVATE);
            boolean isFirstTime = pref.getBoolean("firstTime", true);

            if (isFirstTime) {
                startActivity(new Intent(SplashScreenActivity.this, OnboardingActivity.class));
            } else {
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            }
            finish();
        }, 2000); // 2 second delay

    }
}