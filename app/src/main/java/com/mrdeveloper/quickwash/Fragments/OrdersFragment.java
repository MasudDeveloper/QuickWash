package com.mrdeveloper.quickwash.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mrdeveloper.quickwash.Adapter.LaundryCategoryAdapter;
import com.mrdeveloper.quickwash.Model.LaundryCategory;
import com.mrdeveloper.quickwash.R;

import java.util.ArrayList;
import java.util.List;


public class OrdersFragment extends Fragment {

    Context context;
    RecyclerView rvLaundryCategory;
    List<LaundryCategory> categoryList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_orders, container, false);

        context = getContext();
        rvLaundryCategory = myView.findViewById(R.id.rvLaundryCategory);

        addCategory();

        LaundryCategoryAdapter adapter = new LaundryCategoryAdapter(context, categoryList, "Item2");

        LinearLayoutManager layoutManager = new GridLayoutManager(context,2);
        rvLaundryCategory.setLayoutManager(layoutManager);
        rvLaundryCategory.setAdapter(adapter);


        return myView;
    }  // =================================================

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

}