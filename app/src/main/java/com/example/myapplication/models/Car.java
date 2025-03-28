package com.example.myapplication.models;

import java.util.ArrayList;
import java.util.List;

public class Car {
    private String id;
    private String title;
    private String image;
    private List<String> images;
    private String price;
    private String description;
    private String horsepower;
    private String consume;
    private String Speed;
    private String seat;
    private String ratting;
    private String slug;
    private boolean favorite;


    public Car() {
        this.images = new ArrayList<>();
    }

    public Car(String id, String title, String image, List<String> images, String price, String description,
               String horsepower, String consume, String Speed, String seat,
               String ratting, String slug, boolean favorite) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.images = images != null ? images : new ArrayList<>();
        this.price = price;
        this.description = description;
        this.horsepower = horsepower;
        this.consume = consume;
        this.Speed = Speed;
        this.seat = seat;
        this.ratting = ratting;
        this.favorite = favorite;
        this.slug = slug;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    // Helper method to get primary image or first from list
    public String getPrimaryImage() {
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return image; // Fallback to the old single image
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHorsePower() {
        return horsepower;
    }

    public void setHorsePower(String horsepower) {
        this.horsepower = horsepower;
    }

    public String getConsume() {
        return consume;
    }

    public void setConsume(String consume) {
        this.consume = consume;
    }

    public String getSpeed() {
        return Speed;
    }

    public void setSpeed(String topSpeed) {
        this.Speed = Speed;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getRatting() {
        return ratting;
    }

    public void setRatting(String ratting) {
        this.ratting = ratting;
    }

    public String getSlug() {return slug;}

    public void setSlug(String slug) { this.slug = slug;}

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
