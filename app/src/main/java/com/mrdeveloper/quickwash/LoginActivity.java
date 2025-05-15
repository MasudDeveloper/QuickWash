package com.mrdeveloper.quickwash;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    AppCompatButton btnSignUp, btnSignIn, signUpButton;
    CheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);
        signUpButton = findViewById(R.id.signUpButton);
        checkbox = findViewById(R.id.checkbox);

        // SignIn বাটন সিলেক্ট করুন (ডিফল্ট)
        btnSignIn.setSelected(true);

        // SignUp বাটনে ক্লিক লিসেনার
        btnSignUp.setOnClickListener(v -> {
            btnSignIn.setSelected(false);
            btnSignUp.setSelected(true); // (যদি প্রয়োজন হয়)
            Toast.makeText(this, "Sign Up Clicked", Toast.LENGTH_SHORT).show();
        });

        // SignIn বাটনে ক্লিক লিসেনার
        btnSignIn.setOnClickListener(v -> {
            btnSignIn.setSelected(true);
            btnSignUp.setSelected(false);
            Toast.makeText(this, "Sign In Clicked", Toast.LENGTH_SHORT).show();
        });

    }
}