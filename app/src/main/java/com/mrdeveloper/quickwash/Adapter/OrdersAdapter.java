package com.mrdeveloper.quickwash.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mrdeveloper.quickwash.Model.OrderRequest;
import com.mrdeveloper.quickwash.R;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {
    Context context;
    List<OrderRequest> orderList;

    public OrdersAdapter(Context context, List<OrderRequest> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderViewHolder(LayoutInflater.from(context).inflate(R.layout.order_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderRequest order = orderList.get(position);
        holder.tvShop.setText(order.getShop_name());
        holder.tvStatus.setText(order.getStatus());
        holder.tvDate.setText(order.getCreated_at());
        holder.tvOrderTotal.setText("Total: ৳" + order.getTotal_amount());
        holder.tvOrderId.setText("Order ID: " + order.getId());

        holder.btnViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvShop, tvOrderId, tvAmount, tvStatus, tvDate, tvOrderTotal;
        Button btnViewDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvShop = itemView.findViewById(R.id.tvShopName);
            //tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
