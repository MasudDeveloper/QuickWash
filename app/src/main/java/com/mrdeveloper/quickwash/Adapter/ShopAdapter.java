package com.mrdeveloper.quickwash.Adapter;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mrdeveloper.quickwash.Model.Shop;
import com.mrdeveloper.quickwash.R;

import java.util.List;
import java.util.Locale;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {
    private List<Shop> shopList;
    private OnShopSelectedListener listener;
    Context context;

    public interface OnShopSelectedListener {
        void onShopSelected(String name);
    }

    public ShopAdapter(List<Shop> shopList, OnShopSelectedListener listener) {
        this.shopList = shopList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop, parent, false);
        context = parent.getContext();
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        Shop shop = shopList.get(position);
        // Set shop name
        holder.tvShopName.setText(shop.getName());

        // Set rating
        holder.ratingBar.setRating(shop.getRating());
        holder.tvRating.setText(String.format(Locale.getDefault(), "%.1f (%d)",
                shop.getRating(), shop.getReviewCount()));

        // Set delivery time
        holder.tvShopAddress.setText(shop.getShopAddress());

        // Set opening hours and status
        holder.tvOpeningHours.setText(String.format("Open: %s - %s",
                shop.getOpeningTime(), shop.getClosingTime()));

        if (shop.isOpen()) {
            holder.tvStatus.setText("Open");
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_open));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_shop_status);
        } else {
            holder.tvStatus.setText("Closed");
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_closed));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_shop_status);
        }

        // Load shop image (using Glide/Picasso)
        holder.ivShopImage.setImageResource(shop.getImageUrl());

        holder.itemView.setOnClickListener(v -> listener.onShopSelected(shop.getName()));
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    public static class ShopViewHolder extends RecyclerView.ViewHolder {
        TextView tvShopName, tvRating, tvShopAddress, tvOpeningHours, tvStatus;
        ImageView ivShopImage;
        RatingBar ratingBar;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            ivShopImage = itemView.findViewById(R.id.ivShopImage);
            tvShopName = itemView.findViewById(R.id.tvShopName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvShopAddress = itemView.findViewById(R.id.tvShopAddress);
            tvOpeningHours = itemView.findViewById(R.id.tvOpeningHours);
            tvStatus = itemView.findViewById(R.id.tvStatus);

        }
    }
}
