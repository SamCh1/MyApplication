package com.example.myapplication.models;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String productId;
    private String name;
    private String imageUrl;
    private int price;
    private int quantity;

    public CartItem() {} // Needed for Firebase

    public CartItem(String productId, String name, String imageUrl, int price, int quantity) {
        this.productId = productId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public String getProductId() { return productId; }
    public String getImageUrl() { return imageUrl; }
    public int getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void setName(String name) { this.name = name; }
    public void setProductId(String productId) { this.productId = productId; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setPrice(int price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

}
