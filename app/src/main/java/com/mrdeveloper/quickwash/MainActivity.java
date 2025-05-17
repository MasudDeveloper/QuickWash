package com.mrdeveloper.quickwash;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.mrdeveloper.quickwash.Fragments.HomeFragment;
import com.mrdeveloper.quickwash.Fragments.OrdersFragment;
import com.mrdeveloper.quickwash.Fragments.ProfileFragment;

import de.hdodenhof.circleimageview.CircleImageView;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    SmoothBottomBar botNavView;
    NavigationView navigationView;
    CircleImageView profile_image;

    TextView user_name, refer_id;
    ImageView notification_icon, toolBarVerifyIcon;

    ProgressBar toolbarProgressBar;
    RelativeLayout loading_layout;

    public static String REFER_ID;
    public static String USER_NAME;
    public static int USER_ID;
    public static int IS_VERIFIED = 0;
    String phone;

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

        if (phone.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.customToolbar);
        profile_image = findViewById(R.id.profile_image);
        user_name = findViewById(R.id.user_name);
        refer_id = findViewById(R.id.refer_id);
        notification_icon = findViewById(R.id.notification_icon);
        toolbarProgressBar = findViewById(R.id.toolbarProgressBar);
//        toolBarVerifyIcon = findViewById(R.id.toolBarVerifyIcon);
//        loading_layout = findViewById(R.id.loading_layout);


        botNavView = findViewById(R.id.bottomNavigationView);
        navigationView = findViewById(R.id.navigationView);


        //loadData(number);

        toolbarProgressBar.setIndeterminateTintList(ColorStateList.valueOf(Color.WHITE));

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

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.open();
            }
        });


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
                        replaceFragment(new ProfileFragment());
                        return true;
                    default:
                        replaceFragment(new HomeFragment());
                        return true;
                }
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
        BadgeDrawable badgeDrawable = BadgeDrawable.create(this);
        badgeDrawable.setNumber(5); // Badge-এ সংখ্যা দেখাতে চাইলে
        badgeDrawable.setBackgroundColor(Color.RED); // Badge-এর ব্যাকগ্রাউন্ড কালার
        badgeDrawable.setBadgeTextColor(Color.WHITE); // টেক্সটের কালার
        badgeDrawable.setVisible(true); // দেখানোর জন্য

        //BadgeUtils.attachBadgeDrawable(badgeDrawable, notification_icon, null);


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
}