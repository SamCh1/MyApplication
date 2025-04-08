package com.example.myapplication.models;

public class Product {
    private String id;
    private String name;
    private String imageUrl;
    private int price;
    private String category;

    public Product() {
        // Required for Firebase
    }

    public Product(String id, String name, String imageUrl, int price, String category) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.category = category;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public int getPrice() { return price; }
    public String getCategory() { return category; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setPrice(int price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
}

