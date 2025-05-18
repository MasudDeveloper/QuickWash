package com.mrdeveloper.quickwash.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mrdeveloper.quickwash.Model.LaundryCategory;
import com.mrdeveloper.quickwash.ProductsListActivity;
import com.mrdeveloper.quickwash.R;

import java.util.List;

public class LaundryCategoryAdapter extends RecyclerView.Adapter<LaundryCategoryAdapter.ViewHolder> {

    private Context context;
    private List<LaundryCategory> categoryList;
    private String item;

    public LaundryCategoryAdapter(Context context, List<LaundryCategory> categoryList, String item) {
        this.context = context;
        this.categoryList = categoryList;
        this.item = item;
    }

    @NonNull
    @Override
    public LaundryCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (item.equals("Item2")) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_laundry_category2, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_laundry_category, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull LaundryCategoryAdapter.ViewHolder holder, int position) {
        LaundryCategory category = categoryList.get(position);
        holder.txtTitle.setText(category.getTitle());
        holder.imgIcon.setImageResource(category.getIconResId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductsListActivity.class);
                intent.putExtra("selected_position", holder.getAdapterPosition()); // ক্যাটাগরির পজিশন পাঠালাম
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView txtTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtTitle = itemView.findViewById(R.id.txtTitle);
        }
    }
}
