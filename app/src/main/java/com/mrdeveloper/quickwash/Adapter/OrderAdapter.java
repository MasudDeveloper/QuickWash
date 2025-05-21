package com.mrdeveloper.quickwash.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.mrdeveloper.quickwash.Model.OrderRequest;
import com.mrdeveloper.quickwash.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<OrderRequest> orderList;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(OrderRequest order);
    }

    public OrderAdapter(List<OrderRequest> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    public void updateList(List<OrderRequest> newList) {
        orderList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderRequest order = orderList.get(position);
        holder.bind(order);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvOrderId;
        private final TextView tvShopName;
        private final TextView tvStatus;
        private final TextView tvDate;
        private final TextView tvAmount;
        private final TextView tvServiceCount;
        private final Chip chipDeliveryType;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvShopName = itemView.findViewById(R.id.tvShopName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvServiceCount = itemView.findViewById(R.id.tvServiceCount);
            chipDeliveryType = itemView.findViewById(R.id.chipDeliveryType);
        }

        public void bind(OrderRequest order) {
            // Set order ID
            tvOrderId.setText(String.format("#%d", order.getId()));

            // Set shop name
            tvShopName.setText(order.getShop_name());

            // Set status with color coding
            tvStatus.setText(order.getStatus());
            setStatusColor(order.getStatus());

            // Format and set date
            String formattedDate = formatDate(order.getCreated_at());
            tvDate.setText(formattedDate);

            // Set total amount
            tvAmount.setText(String.format(Locale.getDefault(), "৳%.2f", order.getTotal_amount()));

            // Set service count
            int serviceCount = order.getService_list() != null ? order.getService_list().size() : 0;
            tvServiceCount.setText(String.format(Locale.getDefault(), "%d services", serviceCount));

            // Set delivery type chip
            if (order.getDelivery_date() != null) {
                chipDeliveryType.setText(order.getDelivery_type());
                chipDeliveryType.setVisibility(View.VISIBLE);
            } else {
                chipDeliveryType.setVisibility(View.GONE);
            }
        }

        private void setStatusColor(String status) {
            Context context = itemView.getContext();
            int colorRes;

            switch (status.toLowerCase()) {
                case "pending":
                    colorRes = R.color.status_pending;
                    break;
                case "processing":
                    colorRes = R.color.status_processing;
                    break;
                case "quality check":
                    colorRes = R.color.light_blue;
                    break;
                case "out for delivery":
                    colorRes = R.color.status_completed;
                    break;
                case "delivered":
                    colorRes = R.color.status_delivered;
                    break;
                case "picked up":
                    colorRes = R.color.status_picked_up;
                    break;
                default:
                    colorRes = R.color.status_default;
            }

            tvStatus.setTextColor(ContextCompat.getColor(context, colorRes));
        }

        private String formatDate(String dateString) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                return outputFormat.format(date);
            } catch (ParseException e) {
                return dateString;
            }
        }
    }
}
