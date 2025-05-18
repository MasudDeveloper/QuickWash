package com.mrdeveloper.quickwash.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mrdeveloper.quickwash.Fragments.OrdersFragment;
import com.mrdeveloper.quickwash.Fragments.ProductFragment;
import com.mrdeveloper.quickwash.Model.LaundryCategory;

import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private List<LaundryCategory> categoryList;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<LaundryCategory> categoryList) {
        super(fragmentActivity);
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ProductFragment.newInstance(categoryList.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}

