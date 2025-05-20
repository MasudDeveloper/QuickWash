package com.mrdeveloper.quickwash.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {
    private List<String> shopList;
    private OnShopSelectedListener listener;

    public interface OnShopSelectedListener {
        void onShopSelected(String name);
    }

    public ShopAdapter(List<String> shopList, OnShopSelectedListener listener) {
        this.shopList = shopList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        String name = shopList.get(position);
        holder.textView.setText(name);
        holder.itemView.setOnClickListener(v -> listener.onShopSelected(name));
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    public static class ShopViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }
}
