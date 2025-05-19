package com.mrdeveloper.quickwash.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mrdeveloper.quickwash.Helper.CartManager;
import com.mrdeveloper.quickwash.MainActivity;
import com.mrdeveloper.quickwash.Model.Product;
import com.mrdeveloper.quickwash.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<Product> productList;
    private final Context context; // কনটেক্সট যোগ করা হয়েছে

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.title.setText(product.getName());
        holder.price.setText("৳ " + product.getPrice());

        // কার্টে প্রোডাক্ট আছে কিনা চেক করুন
        boolean isInCart = CartManager.getCartList().stream()
                .anyMatch(p -> p.getId() == product.getId());

        if(isInCart) {
            holder.btnAdd.setText("✓ Added");
            holder.btnAdd.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));
        } else {
            holder.btnAdd.setText("Add to Cart");
            holder.btnAdd.setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue));
        }

        holder.btnAdd.setOnClickListener(v -> {
            CartManager.addToCart(product, context);

            // ইউজার ফিডব্যাক দিন
            Toast.makeText(context, product.getName() + " added to cart", Toast.LENGTH_SHORT).show();

            // বাটন আপডেট করুন
            holder.btnAdd.setText("✓ Added");
            holder.btnAdd.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));

            // কার্ট কাউন্ট আপডেট করুন (যদি আপনার অ্যাক্টিভিটিতে কার্ট আইকন থাকে)
            //updateCartCount();
        });
    }

//    private void updateCartCount() {
//        // যদি আপনার MainActivity বা অন্য কোথাও কার্ট কাউন্ট দেখানোর ব্যবস্থা থাকে
//        if(context instanceof MainActivity) {
//            ((MainActivity) context).updateCartCount();
//        }
//    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView title, price;
        AppCompatButton btnAdd;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewName);
            price = itemView.findViewById(R.id.textViewPrice);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            // quantity ভিউ যদি ব্যবহার না করেন তবে মুছে দিন
        }
    }
}

