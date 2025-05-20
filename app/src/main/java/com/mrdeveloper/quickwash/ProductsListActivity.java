package com.mrdeveloper.quickwash;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mrdeveloper.quickwash.Adapter.ViewPagerAdapter;
import com.mrdeveloper.quickwash.Model.LaundryCategory;

import java.util.ArrayList;
import java.util.List;

public class ProductsListActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager;
    List<LaundryCategory> categoryList;
    MaterialToolbar toolbar;
    AppCompatButton btnPlaceOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_products_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        toolbar = findViewById(R.id.toolbar);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        int selectedPosition = getIntent().getIntExtra("selected_position", 0);

        addCategory();
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, categoryList);
        viewPager.setAdapter(adapter);


        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            View customTab = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            ImageView tabIcon = customTab.findViewById(R.id.tabIcon);
            TextView tabText = customTab.findViewById(R.id.tabText);

            tabText.setText(categoryList.get(position).getTitle());
            tabIcon.setImageResource(categoryList.get(position).getIconResId());
            tabIcon.setImageTintList(null);

            tab.setCustomView(customTab);
        }).attach();

        viewPager.setCurrentItem(selectedPosition, false);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectedTabPosition = tab.getPosition();
                TabLayout.TabLayoutOnPageChangeListener listener = new TabLayout.TabLayoutOnPageChangeListener(tabLayout);
                viewPager.setCurrentItem(selectedTabPosition, true);

                tabLayout.post(() -> {
                    View tabView = tab.view;
                    int scrollX = tabView.getLeft() - (tabLayout.getWidth() - tabView.getWidth()) / 2;
                    tabLayout.smoothScrollTo(scrollX, 0);
                });

                // Highlight selected tab
                //updateTabAppearance(tab, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //updateTabAppearance(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductsListActivity.this, MainActivity.class);
                intent.putExtra("openCart", true);
                startActivity(intent);
                finish();
            }
        });


    }


    public void addCategory() {

        categoryList = new ArrayList<>();

        categoryList.add(new LaundryCategory("1","Wash Only", "শুধু ধুয়ে দেয়া হবে", R.drawable.img_wash_only));
        categoryList.add(new LaundryCategory("2","Wash & Fold", "ধুয়ে ভাঁজ করে দেয়া হবে", R.drawable.img_wash_and_fold));
        categoryList.add(new LaundryCategory("3","Wash & Iron", "ধুয়ে ইস্ত্রি করা হবে", R.drawable.img_wash_and_iron));
        categoryList.add(new LaundryCategory("4","Dry Cleaning", "ড্রাই ক্লিনিং পরিষেবা (সুট, গাউন ইত্যাদি)", R.drawable.img_dry_clean));
        categoryList.add(new LaundryCategory("5","Iron Only", "শুধু ইস্ত্রি করা হবে", R.drawable.img_iron_only));
        categoryList.add(new LaundryCategory("6","Shoe Cleaning", "জুতা ধোয়া ও পলিশ", R.drawable.img_shoe_cleaning));
        categoryList.add(new LaundryCategory("7","Curtain Cleaning", "পর্দা ধোয়া ও ইস্ত্রি", R.drawable.img_curtain_cleaning));
        categoryList.add(new LaundryCategory("8","Blanket Cleaning", "কম্বল ধোয়া", R.drawable.img_blanket_cleaning));

    }

    // Helper method
    private void updateTabAppearance(TabLayout.Tab tab, boolean isSelected) {
        if (tab.getCustomView() != null) {
            TextView text = tab.getCustomView().findViewById(R.id.tabText);
            ImageView icon = tab.getCustomView().findViewById(R.id.tabIcon);
            if (isSelected) {
                text.setTextColor(getResources().getColor(R.color.blue));
                icon.setColorFilter(getResources().getColor(R.color.blue));
            } else {
                text.setTextColor(getResources().getColor(R.color.gray));
                icon.setColorFilter(getResources().getColor(R.color.gray));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}