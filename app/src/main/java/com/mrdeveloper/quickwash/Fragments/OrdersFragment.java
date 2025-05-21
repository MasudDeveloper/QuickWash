package com.mrdeveloper.quickwash.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mrdeveloper.quickwash.Adapter.LaundryCategoryAdapter;
import com.mrdeveloper.quickwash.Adapter.OrdersAdapter;
import com.mrdeveloper.quickwash.Adapter.ViewPagerAdapter;
import com.mrdeveloper.quickwash.Interface.ApiInterface;
import com.mrdeveloper.quickwash.Interface.RetrofitClient;
import com.mrdeveloper.quickwash.MainActivity;
import com.mrdeveloper.quickwash.Model.LaundryCategory;
import com.mrdeveloper.quickwash.Model.OrderRequest;
import com.mrdeveloper.quickwash.Model.StatusType;
import com.mrdeveloper.quickwash.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrdersFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 viewPager;
    OrdersPagerAdapter pagerAdapter;
    List<StatusType> statusTypeList;

    Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_orders, container, false);

        context = myView.getContext();

        tabLayout = myView.findViewById(R.id.tabLayout);
        viewPager = myView.findViewById(R.id.viewPager);

        //setupViewPager();
        addStatusType();

        pagerAdapter = new OrdersPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            View customTab = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
            ImageView tabIcon = customTab.findViewById(R.id.tabIcon);
            TextView tabText = customTab.findViewById(R.id.tabText);

            tabText.setText(statusTypeList.get(position).getTitle());
            tabIcon.setImageResource(statusTypeList.get(position).getIconResId());
            tabIcon.setImageTintList(null);

            tab.setCustomView(customTab);
        }).attach();

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



        return myView;
    }  // =================================================

    private void addStatusType() {
        statusTypeList = new ArrayList<>();

        statusTypeList.add(new StatusType("1", "All", R.drawable.img_wash_and_iron));
        statusTypeList.add(new StatusType("2", "Pending", R.drawable.icon_pending));
        statusTypeList.add(new StatusType("3", "Picked Up", R.drawable.icon_picked_up));
        statusTypeList.add(new StatusType("4", "Processing", R.drawable.img_wash_only));
        statusTypeList.add(new StatusType("5", "Quality Check", R.drawable.img_wash_and_fold));
        statusTypeList.add(new StatusType("6", "Out for Delivery", R.drawable.icon_premium_delivery));
        statusTypeList.add(new StatusType("7", "Delivered", R.drawable.icon_tick));

    }

    private void setupViewPager() {
        pagerAdapter = new OrdersPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("All");
                    break;
                case 1:
                    tab.setText("Pending");
                    break;
                case 2:
                    tab.setText("Picked Up");
                    break;
                case 3:
                    tab.setText("Processing");
                    break;
                case 4:
                    tab.setText("Quality Check");
                    break;
                case 5:
                    tab.setText("Out for Delivery");
                    break;
                case 6:
                    tab.setText("Delivered");
                    break;
            }
        }).attach();
    }

    // Nested adapter class
    private static class OrdersPagerAdapter extends FragmentStateAdapter {

        private static final int NUM_TABS = 7;

        public OrdersPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return OrderStatusFragment.newInstance(null); // All orders
                case 1:
                    return OrderStatusFragment.newInstance("Pending");
                case 2:
                    return OrderStatusFragment.newInstance("Picked Up");
                case 3:
                    return OrderStatusFragment.newInstance("Processing");
                case 4:
                    return OrderStatusFragment.newInstance("Quality Check");
                case 5:
                    return OrderStatusFragment.newInstance("Out for Delivery");
                case 6:
                    return OrderStatusFragment.newInstance("Delivered");
                default:
                    return OrderStatusFragment.newInstance(null);
            }
        }

        @Override
        public int getItemCount() {
            return NUM_TABS;
        }
    }

}