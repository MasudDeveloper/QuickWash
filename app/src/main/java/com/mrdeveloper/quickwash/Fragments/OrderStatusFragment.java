package com.mrdeveloper.quickwash.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mrdeveloper.quickwash.Adapter.OrderAdapter;
import com.mrdeveloper.quickwash.Interface.ApiInterface;
import com.mrdeveloper.quickwash.Interface.RetrofitClient;
import com.mrdeveloper.quickwash.MainActivity;
import com.mrdeveloper.quickwash.Model.OrderRequest;
import com.mrdeveloper.quickwash.OrderDetailsActivity;
import com.mrdeveloper.quickwash.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderStatusFragment extends Fragment {

    private static final String ARG_STATUS = "status";
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private String statusFilter;
    private List<OrderRequest> orderList = new ArrayList<>();
    ProgressBar progressBar;
    LinearLayout animationLayout;
    SwipeRefreshLayout swipeRefreshLayout;


    public static OrderStatusFragment newInstance(String status) {
        OrderStatusFragment fragment = new OrderStatusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            statusFilter = getArguments().getString(ARG_STATUS);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_status, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        animationLayout = view.findViewById(R.id.animationLayout);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        progressBar.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new OrderAdapter(orderList, order -> {
            // Handle order click
            Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
            intent.putExtra("order", order);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadOrders();
        });

        loadOrders();

        return view;
    }

    private void loadOrders() {
        ApiInterface apiInterface = RetrofitClient.getApiService();
        Call<List<OrderRequest>> call;

        animationLayout.setVisibility(View.GONE);

        if (statusFilter != null) {
            call = apiInterface.getOrdersByStatus(MainActivity.USER_ID, statusFilter);
        } else {
            call = apiInterface.getAllOrders(MainActivity.USER_ID);
        }

        call.enqueue(new Callback<List<OrderRequest>>() {
            @Override
            public void onResponse(Call<List<OrderRequest>> call, Response<List<OrderRequest>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
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
                    Toast.makeText(getContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderRequest>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}