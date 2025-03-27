package com.example.myapplication.models;

public class CarList {
    private String id;
    private String title;
    private String image;
    private String Speed;
    private String seat;
    private String horsepower;
    private boolean isFavorite;

    public CarList(){

    }

    public CarList(String id, String title, String image, String Speed, String seat, String horsepower) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.Speed = Speed;
        this.seat = seat;
        this.horsepower = horsepower;
        this.isFavorite = false;
    }

    public String getId() {return id;}
    public void setId(String id) { this.id = id; }

    public String getTitle() {return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImage() {return image; }
    public void setImage(String image) { this.image = image; }

    public String getSpeed() {return Speed; }
    public void setSpeed(String Speed) { this.Speed = Speed; }

    public String getSeat() {return seat; }
    public void setSeat(String seat) { this.seat = seat; }

    public String getHorsePower() {return horsepower; }
    public void setHorsePower(String horsepower) { this.horsepower = horsepower; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}


