package com.mrdeveloper.quickwash.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mrdeveloper.quickwash.Adapter.LaundryCategoryAdapter;
import com.mrdeveloper.quickwash.Adapter.OrdersAdapter;
import com.mrdeveloper.quickwash.Interface.ApiInterface;
import com.mrdeveloper.quickwash.Interface.RetrofitClient;
import com.mrdeveloper.quickwash.MainActivity;
import com.mrdeveloper.quickwash.Model.LaundryCategory;
import com.mrdeveloper.quickwash.Model.OrderRequest;
import com.mrdeveloper.quickwash.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrdersFragment extends Fragment {

    Context context;
    RecyclerView recyclerView;
    List<OrderRequest> orderList;
    OrdersAdapter adapter;
    SharedPreferences sharedPreferences;
    int user_id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_orders, container, false);

        context = myView.getContext();
        user_id = MainActivity.USER_ID;

        recyclerView = myView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        loadOrders();


        return myView;
    }  // =================================================

    private void loadOrders() {

        ApiInterface apiService = RetrofitClient.getApiService();
        Call<List<OrderRequest>> call = apiService.getOrders(user_id);

        call.enqueue(new Callback<List<OrderRequest>>() {
            @Override
            public void onResponse(Call<List<OrderRequest>> call, Response<List<OrderRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList = response.body();
                    adapter = new OrdersAdapter(context, orderList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<OrderRequest>> call, Throwable t) {
                Toast.makeText(context, "অর্ডার লোড করতে ব্যর্থ: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}