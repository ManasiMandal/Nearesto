package com.manasi.nearesto.modal;

import java.util.Locale;

public class FoodItem {

    private String name, description, url, type, restaurantId, restaurantName;
    private float rating, price;

    private long id, restaurant;

    public FoodItem() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isVeg() {
        return this.type.toLowerCase() == "veg";
    }

    public boolean isNonVeg() {
        switch (this.type.toLowerCase()) {
            case "nonveg":
            case "non-veg":
            case "non_veg":
                return true;
        }
        return false;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(long restaurant) {
        this.restaurant = restaurant;
    }
}
