package com.mrdeveloper.quickwash.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mrdeveloper.quickwash.Helper.CartManager;
import com.mrdeveloper.quickwash.Model.Product;
import com.mrdeveloper.quickwash.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public List<Product> cartList;
    Context context;
    Runnable updateTotalCallback;

    public CartAdapter(List<Product> cartList, Context context, Runnable updateTotalCallback) {
        this.cartList = cartList;
        this.context = context;
        this.updateTotalCallback = updateTotalCallback;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartList.get(position);

        holder.textViewName.setText(product.getName());
        holder.textViewPrice.setText("৳" + product.getPrice());
        holder.textViewQuantity.setText(String.valueOf(product.getQuantity()));

        holder.btnAdd.setOnClickListener(v -> {
            int newQuantity = product.getQuantity() + 1;
            CartManager.updateQuantity(product, newQuantity, context);
            product.setQuantity(newQuantity); // লোকাল কপি আপডেট করুন
            notifyItemChanged(position);
            updateTotalCallback.run();
        });

        holder.btnMinus.setOnClickListener(v -> {
            int newQuantity = product.getQuantity() - 1;
            if (newQuantity <= 0) {
                CartManager.removeFromCart(product, context);
                cartList.remove(position); // অ্যাডাপ্টারের লিস্ট থেকে রিমুভ করুন
                notifyItemRemoved(position);
            } else {
                CartManager.updateQuantity(product, newQuantity, context);
                product.setQuantity(newQuantity); // লোকাল কপি আপডেট করুন
                notifyItemChanged(position);
            }
            updateTotalCallback.run();
        });


    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewPrice, textViewQuantity;
        ImageButton btnAdd, btnMinus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }
}
