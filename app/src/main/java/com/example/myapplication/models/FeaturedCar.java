package com.example.myapplication.models;

public class FeaturedCar {
    private String title;
    private String description;
    private String image;

    public FeaturedCar(){

    }

    public FeaturedCar(String title, String description, String image){
        this.title = title;
        this.description = description;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }
}
