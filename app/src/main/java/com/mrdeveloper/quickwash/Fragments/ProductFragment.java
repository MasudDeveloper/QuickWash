package com.mrdeveloper.quickwash.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrdeveloper.quickwash.Adapter.ProductAdapter;
import com.mrdeveloper.quickwash.Model.Product;
import com.mrdeveloper.quickwash.R;

import java.util.ArrayList;
import java.util.List;


public class ProductFragment extends Fragment {

    private static final String ARG_CAT_ID = "cat_id";
    private String categoryId;

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

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Dummy Data - replace with Retrofit or DB call based on categoryId
        List<Product> productList = getProductsByCategory(categoryId);

        ProductAdapter adapter = new ProductAdapter(productList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<Product> getProductsByCategory(String catId) {
        // Dummy data (real implementation will use API or DB based on catId)
        List<Product> list = new ArrayList<>();
        if (catId.equals("1")) {
            list.add(new Product("Shirt", 30));
            list.add(new Product("Pant", 40));
        } else if (catId.equals("2")) {
            list.add(new Product("Blazer", 100));
        }
        return list;
    }
}
