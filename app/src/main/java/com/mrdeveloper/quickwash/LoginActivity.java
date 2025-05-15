package com.mrdeveloper.quickwash;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    AppCompatButton btnSignUp, btnSignIn, signUpButton;
    CheckBox checkbox;
    LinearLayout signUpLayout, signInLayout;
    TextInputLayout nameInputLayout, emailInputLayout, password1InputLayout, password2InputLayout, loginEmailInputLayout, loginPasswordInputLayout;
    TextInputEditText edName, edEmail, edPassword1, edPassword2, loginEdEmail, loginEdPassword1;
    TextView title;

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
        title = findViewById(R.id.title);
        signUpLayout = findViewById(R.id.signUpLayout);
        signInLayout = findViewById(R.id.signInLayout);

        nameInputLayout = findViewById(R.id.nameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        password1InputLayout = findViewById(R.id.password1InputLayout);
        password2InputLayout = findViewById(R.id.password2InputLayout);
        loginEmailInputLayout = findViewById(R.id.loginEmailInputLayout);
        loginPasswordInputLayout = findViewById(R.id.loginPasswordInputLayout);





        // SignIn বাটন সিলেক্ট করুন (ডিফল্ট)
        btnSignUp.setSelected(true);

        // SignUp বাটনে ক্লিক লিসেনার
        btnSignUp.setOnClickListener(v -> {
            btnSignIn.setSelected(false);
            btnSignUp.setSelected(true); // (যদি প্রয়োজন হয়)
            btnSignUp.setTextColor(Color.WHITE);
            btnSignIn.setTextColor(Color.BLACK);
            signUpLayout.setVisibility(View.VISIBLE);
            signInLayout.setVisibility(View.GONE);
            title.setText("SignUp To Continue");
            //Toast.makeText(this, "Sign Up Clicked", Toast.LENGTH_SHORT).show();
        });

        // SignIn বাটনে ক্লিক লিসেনার
        btnSignIn.setOnClickListener(v -> {
            btnSignIn.setSelected(true);
            btnSignUp.setSelected(false);
            btnSignUp.setTextColor(Color.BLACK);
            btnSignIn.setTextColor(Color.WHITE);
            signUpLayout.setVisibility(View.GONE);
            signInLayout.setVisibility(View.VISIBLE);
            title.setText("SignIn To Continue");
            //Toast.makeText(this, "Sign In Clicked", Toast.LENGTH_SHORT).show();
        });


        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Exit")
                        .setMessage("Are You Sure Exit?")
                        .setIcon(R.drawable.only_logo)
                        .setPositiveButton("Yes", (dialogInterface, i) -> finish())
                        .setNegativeButton("No", null)
                        .show();
            }
        });


    } // ========================== On Create End =====================
}