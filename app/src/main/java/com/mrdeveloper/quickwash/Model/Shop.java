package com.mrdeveloper.quickwash.Model;

public class Shop {
    private String id;
    private String name;
    private int imageUrl;
    private float rating;
    private int reviewCount;
    private String shopAddress;
    private String openingTime;
    private String closingTime;

    private boolean isOpen;

    public Shop(String id, String name, int imageUrl, float rating, int reviewCount, String shopAddress, String openingTime, String closingTime, boolean isOpen) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.shopAddress = shopAddress;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.isOpen = isOpen;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public float getRating() {
        return rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public boolean isOpen() {
        return isOpen;
    }
}