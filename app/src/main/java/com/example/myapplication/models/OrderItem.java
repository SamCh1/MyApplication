package com.example.myapplication.models;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private String productId;
    private String productName;
    private String imageUrl;
    private int quantity;
    private long price;

    public OrderItem() {}

    public OrderItem(String productId, String productName, String imageUrl, int quantity, long price) {
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }
}
