package com.mrdeveloper.quickwash;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.mrdeveloper.quickwash.Interface.ApiInterface;
import com.mrdeveloper.quickwash.Interface.ApiResponse;
import com.mrdeveloper.quickwash.Interface.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    AppCompatButton btnSignUp, btnSignIn, signUpButton, signInButton;
    CheckBox checkbox;
    LinearLayout signUpLayout, signInLayout;
    TextInputLayout nameInputLayout, phoneInputLayout, password1InputLayout, password2InputLayout, loginPhoneInputLayout, loginPasswordInputLayout;
    TextInputEditText edName, edEmail, edPassword1, edPassword2, loginEdPhone, loginEdPassword;
    TextView title;
    ProgressDialog progressDialog;

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
        signInButton = findViewById(R.id.signInButton);
        checkbox = findViewById(R.id.checkbox);
        title = findViewById(R.id.title);
        signUpLayout = findViewById(R.id.signUpLayout);
        signInLayout = findViewById(R.id.signInLayout);

        nameInputLayout = findViewById(R.id.nameInputLayout);
        phoneInputLayout = findViewById(R.id.emailInputLayout);
        password1InputLayout = findViewById(R.id.password1InputLayout);
        password2InputLayout = findViewById(R.id.password2InputLayout);
        loginPhoneInputLayout = findViewById(R.id.loginPhoneInputLayout);
        loginPasswordInputLayout = findViewById(R.id.loginPasswordInputLayout);

        edName = findViewById(R.id.edName);
        edEmail = findViewById(R.id.edEmail);
        edPassword1 = findViewById(R.id.edPassword1);
        edPassword2 = findViewById(R.id.edPassword2);
        loginEdPhone = findViewById(R.id.loginEdPhone);
        loginEdPassword = findViewById(R.id.loginEdPassword);





        // SignIn বাটন সিলেক্ট করুন (ডিফল্ট)
        btnSignUp.setSelected(true);

        // SignUp বাটনে ক্লিক লিসেনার
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSignIn.setSelected(false);
                btnSignUp.setSelected(true);
                btnSignUp.setTextColor(Color.WHITE);
                btnSignIn.setTextColor(Color.BLACK);
                signUpLayout.setVisibility(View.VISIBLE);
                signInLayout.setVisibility(View.GONE);
                title.setText("SignUp To Continue");
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpClick();
            }
        });

        // SignIn বাটনে ক্লিক লিসেনার
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSignIn.setSelected(true);
                btnSignUp.setSelected(false);
                btnSignUp.setTextColor(Color.BLACK);
                btnSignIn.setTextColor(Color.WHITE);
                signUpLayout.setVisibility(View.GONE);
                signInLayout.setVisibility(View.VISIBLE);
                title.setText("SignIn To Continue");
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginPhone = loginEdPhone.getText().toString();
                String loginPassword = loginEdPassword.getText().toString();

                // Clear previous errors
                loginPhoneInputLayout.setError(null);
                loginPasswordInputLayout.setError(null);

                if (loginPhone.isEmpty()) {
                    loginPhoneInputLayout.setError("আগে মোবাইল নাম্বার লিখুন");
                } else if (loginPhone.length() < 11) {
                    loginPhoneInputLayout.setError("সঠিক মোবাইল নাম্বার লিখুন");
                } else if (loginPassword.isEmpty()) {
                    loginPasswordInputLayout.setError("আপনার পাসওয়ার্ড লিখুন");
                } else {
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Loading, please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    loginData(loginPhone, loginPassword);
                }
            }
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

    private void signUpData(String name, String phone, String password) {

        ApiInterface apiInterface = RetrofitClient.getApiService();

        apiInterface.signUp(name,phone,password).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();
                    if (message.contains("রেজিস্ট্রেশন সফল")){
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("phone",phone).apply();

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else if (message.contains("মোবাইল নম্বরটি ইতিমধ্যে ব্যবহৃত হয়েছে")) {
                        phoneInputLayout.setError(message);
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "একাউন্ট খুলতে সমস্যা হচ্ছে", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "নেটওয়ার্ক ত্রুটি: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void signUpClick() {
        String name = edName.getText().toString();
        String phone = edEmail.getText().toString();
        String password = edPassword1.getText().toString();
        String password2 = edPassword2.getText().toString();

        // Clear previous errors
        nameInputLayout.setError(null);
        phoneInputLayout.setError(null);
        password1InputLayout.setError(null);
        password2InputLayout.setError(null);

        if (name.isEmpty()) {
            nameInputLayout.setError("আগে নাম লিখুন");
        } else if (phone.isEmpty()) {
            phoneInputLayout.setError("আগে মোবাইল নাম্বার লিখুন");
        } else if (phone.length() < 11) {
            phoneInputLayout.setError("সঠিক মোবাইল নাম্বার লিখুন");
        } else if (password.isEmpty()) {
            password1InputLayout.setError("আপনার পাসওয়ার্ড লিখুন");
        } else if (password2.isEmpty()) {
            password2InputLayout.setError("আপনার পাসওয়ার্ড নিশ্চিত করুন");
        } else if (!password.equals(password2)) {
            password1InputLayout.setError("পাসওয়ার্ড মিলেনি");
            password2InputLayout.setError("পাসওয়ার্ড মিলেনি");
        } else if (password.length() < 6) {
            password1InputLayout.setError("দূর্বল পাসওয়ার্ড");
            password2InputLayout.setError("দূর্বল পাসওয়ার্ড");
        } else if (!checkbox.isChecked()) {
            checkbox.setError("Check the Terms and Condition");
        } else {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Loading, please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            signUpData(name, phone, password);
        }
    }

    private void loginData(String phone, String password) {

        ApiInterface apiInterface = RetrofitClient.getApiService();

        apiInterface.getLoginResponse(phone,password).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();
                    if (message.contains("লগইন সফল")){
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences("MyPrefs",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("phone",phone).apply();

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else if (message.contains("মোবাইল নাম্বারটি দিয়ে একাউন্ট খোলা হয়নি")) {
                        loginPhoneInputLayout.setError(message);
                    } else if (message.contains("ভুল পাসওয়ার্ড")) {
                        loginPasswordInputLayout.setError(message);
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "লগইন করতে সমস্যা হচ্ছে", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "নেটওয়ার্ক ত্রুটি: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}