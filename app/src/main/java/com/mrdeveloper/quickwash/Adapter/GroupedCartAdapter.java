package com.mrdeveloper.quickwash.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mrdeveloper.quickwash.Helper.CartManager;
import com.mrdeveloper.quickwash.Model.CartCategory;
import com.mrdeveloper.quickwash.Model.LaundryCategory;
import com.mrdeveloper.quickwash.Model.Product;
import com.mrdeveloper.quickwash.R;

import java.util.ArrayList;
import java.util.List;

public class GroupedCartAdapter extends RecyclerView.Adapter<GroupedCartAdapter.GroupViewHolder> {

    public List<CartCategory> cartList;
    private Context context;
    Runnable updateTotalCallback;

    public GroupedCartAdapter(List<CartCategory> cartList, Context context, Runnable updateTotalCallback) {
        this.cartList = cartList;
        this.context = context;
        this.updateTotalCallback = updateTotalCallback;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        CartCategory cartCategory = cartList.get(position);
        holder.textCategoryTitle.setText(cartCategory.getCategory().getTitle());
        holder.imageCategoryIcon.setImageResource(cartCategory.getCategory().getIconResId());

        CartAdapter productAdapter = new CartAdapter(cartCategory.getProductList(),context,updateTotalCallback);
        holder.recyclerProducts.setAdapter(productAdapter);
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView textCategoryTitle;
        RecyclerView recyclerProducts;
        ImageView imageCategoryIcon;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategoryTitle = itemView.findViewById(R.id.textCategoryTitle);
            imageCategoryIcon = itemView.findViewById(R.id.imageCategoryIcon);
            recyclerProducts = itemView.findViewById(R.id.recyclerProductList);
            recyclerProducts.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}

