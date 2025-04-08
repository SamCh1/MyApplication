package com.example.myapplication.models;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String orderId;
    private String userId;
    private List<CartItem> items;
    private int totalAmount;
    private long timestamp;

    public Order() {}

    public Order(String orderId, String userId, List<CartItem> items, int totalAmount, long timestamp) {
        this.orderId = orderId;
        this.userId = userId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.timestamp = timestamp;
    }

    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public List<CartItem> getItems() { return items; }
    public int getTotalAmount() { return totalAmount; }
    public long getTimestamp() { return timestamp; }
}

