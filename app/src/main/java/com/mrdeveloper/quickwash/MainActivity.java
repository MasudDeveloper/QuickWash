package com.mrdeveloper.quickwash;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.mrdeveloper.quickwash.Fragments.CartFragment;
import com.mrdeveloper.quickwash.Helper.CartManager;
import com.mrdeveloper.quickwash.Fragments.HomeFragment;
import com.mrdeveloper.quickwash.Fragments.OrdersFragment;
import com.mrdeveloper.quickwash.Fragments.ProfileFragment;
import com.mrdeveloper.quickwash.Interface.ApiInterface;
import com.mrdeveloper.quickwash.Interface.ApiResponse;
import com.mrdeveloper.quickwash.Interface.RetrofitClient;
import com.mrdeveloper.quickwash.Model.User;

import de.hdodenhof.circleimageview.CircleImageView;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    SmoothBottomBar botNavView;
    NavigationView navigationView;
    CircleImageView profile_image;

    TextView user_name, refer_id;
    ImageView notification_icon, toolBarVerifyIcon;

    RelativeLayout loading_layout;

    public static String PHONE;
    public static String USER_NAME;
    public static int USER_ID;
    String phone;
    boolean openCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.blue));

        sharedPreferences = getSharedPreferences("MyPrefs",MODE_PRIVATE);
        phone = sharedPreferences.getString("phone","");
        openCart = getIntent().getBooleanExtra("openCart", false);

        if (phone.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        fetchUserByPhone(phone);

        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);


        botNavView = findViewById(R.id.bottomNavigationView);
        navigationView = findViewById(R.id.navigationView);


        //loadData(number);
        if (openCart) {
            replaceFragment(new CartFragment());
            botNavView.setItemActiveIndex(2); // যদি Cart second item হয়
        } else {
            replaceFragment(new HomeFragment());
            botNavView.setItemActiveIndex(0); // Home
        }



        View headerView = navigationView.getHeaderView(0);
        TextView navTvName = headerView.findViewById(R.id.tvName);
        TextView navTvRefer = headerView.findViewById(R.id.tvRefer);
        ImageView navCopyButton = headerView.findViewById(R.id.copyButton);


        navigationView.setItemIconTintList(null);

        //setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this,
                drawerLayout, toolbar, R.string.open, R.string.close
        );

        drawerLayout.addDrawerListener(toggle);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.home) {
                    replaceFragment(new HomeFragment());
                    drawerLayout.close();
                    return true;
                } else if (item.getItemId() == R.id.logout) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("phone","").apply();
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                    return true;
                }

                return false;
            }
        });

        //replaceFragment(new HomeFragment());

        botNavView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                switch (i) {
                    case 0:
                        replaceFragment(new HomeFragment());
                        return true;
                    case 1:
                        replaceFragment(new OrdersFragment());
                        return true;
                    case 2:
                        replaceFragment(new CartFragment());
                        return true;
                    case 3:
                        replaceFragment(new ProfileFragment());
                        return true;
                }
                return false;
            }
        });


        navCopyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String referText = navTvRefer.getText().toString();
                if (!referText.isEmpty()) {
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Copied Text", referText);
                    clipboardManager.setPrimaryClip(clipData);
                    //Toast.makeText(MainActivity.this, "Copied: " + referText, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No data to copy", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 2. BadgeDrawable তৈরি করুন



        //========================== OnBackPressed =====================

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Exit")
                        .setMessage("Are You Sure Exit?")
                        .setIcon(R.drawable.only_logo)
                        .setPositiveButton("Yes", (dialogInterface, i) -> finish())
                        .setNegativeButton("No", null)
                        .show();
            }
        });

    } // ======================= On Create End ========================

    private void replaceFragment (Fragment fragment) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, fragment);
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "ফ্র্যাগমেন্ট লোড করতে ব্যর্থ হয়েছে", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUserByPhone(String phone) {
        ApiInterface apiService = RetrofitClient.getApiService();
        Call<ApiResponse> call = apiService.getUserByPhone(phone);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        User user = response.body().getUser();

                        USER_ID = user.getId();
                        USER_NAME = user.getName();
                        PHONE = user.getPhone();

                    } else {
                        Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}