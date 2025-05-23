package com.mrdeveloper.quickwash.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.mrdeveloper.quickwash.Adapter.LaundryCategoryAdapter;
import com.mrdeveloper.quickwash.Adapter.OrderAdapter;
import com.mrdeveloper.quickwash.Interface.ApiInterface;
import com.mrdeveloper.quickwash.Interface.RetrofitClient;
import com.mrdeveloper.quickwash.MainActivity;
import com.mrdeveloper.quickwash.Model.LaundryCategory;
import com.mrdeveloper.quickwash.Model.OrderRequest;
import com.mrdeveloper.quickwash.OrderDetailsActivity;
import com.mrdeveloper.quickwash.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    RecyclerView rvLaundryCategory, orderRecyclerView;
    TextView noOrderTv;
    Context context;
    ImageSlider imageSlider;
    List<LaundryCategory> categoryList;
    AppCompatButton btnCurrent, btnPrevious;
    ProgressBar progressBar;
    LinearLayout animationLayout;
    OrderAdapter adapter;
    String statusFilter;
    List<OrderRequest> orderList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_home, container, false);

        context = getContext();
        rvLaundryCategory = myView.findViewById(R.id.rvLaundryCategory);
        orderRecyclerView = myView.findViewById(R.id.orderRecyclerView);
        imageSlider = myView.findViewById(R.id.image_slider);
        btnCurrent = myView.findViewById(R.id.btnCurrent);
        btnPrevious = myView.findViewById(R.id.btnPrevious);
        noOrderTv = myView.findViewById(R.id.noOrderTv);
        progressBar = myView.findViewById(R.id.progressBar);
        animationLayout = myView.findViewById(R.id.animationLayout);

        // SignIn বাটন সিলেক্ট করুন (ডিফল্ট)
        btnCurrent.setSelected(true);
        statusFilter = "current"; // complete ছাড়া সব অর্ডার
        loadOrders();

        // SignUp বাটনে ক্লিক লিসেনার
        btnCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPrevious.setSelected(false);
                btnCurrent.setSelected(true);
                btnCurrent.setTextColor(Color.WHITE);
                btnPrevious.setTextColor(Color.BLACK);
//                signUpLayout.setVisibility(View.VISIBLE);
//                signInLayout.setVisibility(View.GONE);
                noOrderTv.setText("No Current Orders");
                statusFilter = "current"; // complete ছাড়া সব অর্ডার
                loadOrders();
            }
        });

        // SignIn বাটনে ক্লিক লিসেনার
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPrevious.setSelected(true);
                btnCurrent.setSelected(false);
                btnCurrent.setTextColor(Color.BLACK);
                btnPrevious.setTextColor(Color.WHITE);
//                signUpLayout.setVisibility(View.GONE);
//                signInLayout.setVisibility(View.VISIBLE);
                noOrderTv.setText("No Previous Orders");
                statusFilter = "Delivered"; // শুধুমাত্র complete order
                loadOrders();
            }
        });

        addCategory();

        LaundryCategoryAdapter adapterCategory = new LaundryCategoryAdapter(context, categoryList, "Item1");

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvLaundryCategory.setLayoutManager(layoutManager);
        rvLaundryCategory.setAdapter(adapterCategory);


        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.cover1,  ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.cover2,  ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);





        return myView;
    } // =============== ============== On Create End =============


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

    private void loadOrders() {
        ApiInterface apiInterface = RetrofitClient.getApiService();
        Call<List<OrderRequest>> call;

        adapter = new OrderAdapter(orderList, order -> {
            // Handle order click
            Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
            intent.putExtra("order", order);
            startActivity(intent);
        });

        orderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderRecyclerView.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);
        animationLayout.setVisibility(View.GONE);

        call = apiInterface.getOrdersByStatusHome(MainActivity.USER_ID, statusFilter);

        call.enqueue(new Callback<List<OrderRequest>>() {
            @Override
            public void onResponse(Call<List<OrderRequest>> call, Response<List<OrderRequest>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body());
                    adapter.updateList(orderList);
                    if (orderList.isEmpty()) {
                        animationLayout.setVisibility(View.VISIBLE);
                    } else {
                        animationLayout.setVisibility(View.GONE);
                    }
                } else {
                    loadOrders();
                }
            }

            @Override
            public void onFailure(Call<List<OrderRequest>> call, Throwable t) {
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }




}