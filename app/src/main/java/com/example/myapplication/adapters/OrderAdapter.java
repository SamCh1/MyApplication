package com.example.myapplication.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.OrderDetailActivity;
import com.example.myapplication.R;
import com.example.myapplication.models.Order;

import java.text.SimpleDateFormat;
import java.util.*;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public OrderAdapter(List<Order> orders) {
        this.orders = orders;
    }

    public void updateList(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false));
    }

    @Override public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.orderId.setText(order.getOrderId());
        holder.timestamp.setText(dateFormat.format(new Date(order.getTimestamp())));
        holder.total.setText(String.format("%,d VNĐ", order.getTotalAmount()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), OrderDetailActivity.class);
            intent.putExtra("order", order);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, timestamp, total;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            timestamp = itemView.findViewById(R.id.order_date);
            total = itemView.findViewById(R.id.order_total);
        }
    }
}
