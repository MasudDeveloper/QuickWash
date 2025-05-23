package com.mrdeveloper.quickwash.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mrdeveloper.quickwash.Adapter.ProductAdapter;
import com.mrdeveloper.quickwash.Helper.CartManager;
import com.mrdeveloper.quickwash.Interface.ApiInterface;
import com.mrdeveloper.quickwash.Interface.ApiResponse;
import com.mrdeveloper.quickwash.Interface.RetrofitClient;
import com.mrdeveloper.quickwash.Model.Product;
import com.mrdeveloper.quickwash.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductFragment extends Fragment {

    Context context;
    private static final String ARG_CAT_ID = "cat_id";
    private String categoryId;
    ProgressBar progressBar;
    LinearLayout animationLayout;

    public static ProductFragment newInstance(String catId) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CAT_ID, catId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getString(ARG_CAT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_product, container, false);
        context = view.getContext();

        progressBar = view.findViewById(R.id.progressBar);
        animationLayout = view.findViewById(R.id.animationLayout);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        CartManager.loadCartFromPrefs(context);

        loadProductsByCategory(categoryId, recyclerView);

        return view;
    }

    private void loadProductsByCategory(String catId, RecyclerView recyclerView) {
        int category_id = Integer.parseInt(catId);

        ApiInterface apiInterface = RetrofitClient.getApiService();
        
        progressBar.setVisibility(View.VISIBLE);

        apiInterface.getProducts(category_id).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> productList = response.body().getProducts();
                    if (productList.isEmpty()) {
                        animationLayout.setVisibility(View.VISIBLE);
                        return;
                    }
                    animationLayout.setVisibility(View.GONE);
                    ProductAdapter adapter = new ProductAdapter(productList, getContext());
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Error handling
                Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


}

