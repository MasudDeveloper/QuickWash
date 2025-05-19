package com.mrdeveloper.quickwash.Interface;

import com.google.gson.annotations.SerializedName;
import com.mrdeveloper.quickwash.Model.LaundryCategory;
import com.mrdeveloper.quickwash.Model.Product;
import com.mrdeveloper.quickwash.Model.User;

import java.util.List;

public class ApiResponse {

    private String message;
    private boolean success;
    @SerializedName("categories")
    private List<LaundryCategory> categories;
    private List<Product> products;
    private User user;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<LaundryCategory> getCategories() {
        return categories;
    }

    public List<Product> getProducts() {
        return products;
    }

    public User getUser() {
        return user;
    }
}
