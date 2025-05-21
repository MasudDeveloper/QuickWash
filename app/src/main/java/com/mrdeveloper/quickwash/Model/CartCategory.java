package com.mrdeveloper.quickwash.Model;

import java.util.List;

public class CartCategory {
    private LaundryCategory category;
    private List<Product> productList;

    public CartCategory(LaundryCategory category, List<Product> productList) {
        this.category = category;
        this.productList = productList;
    }

    public LaundryCategory getCategory() {
        return category;
    }

    public List<Product> getProductList() {
        return productList;
    }
}
