package com.example.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.models.CartItem;
import com.example.myapplication.models.OrderItem;
import com.example.myapplication.models.Product;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private List<CartItem> orderItems;
    private Context context;

    public OrderItemAdapter(Context context, List<CartItem> orderItems) {
        this.context = context;
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        CartItem item = orderItems.get(position);
        holder.itemName.setText(item.getName());
        holder.itemQuantity.setText("Số lượng: " + item.getQuantity());
        holder.itemPrice.setText(String.format("%,d VNĐ", item.getPrice() * item.getQuantity()));

        Glide.with(context)
                .load(item.getImageUrl())
                .centerCrop()
                .into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity, itemPrice;
        ImageView itemImage;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.order_item_name);
            itemQuantity = itemView.findViewById(R.id.order_item_quantity);
            itemPrice = itemView.findViewById(R.id.order_item_price);
            itemImage = itemView.findViewById(R.id.order_item_image);
        }
    }
}
