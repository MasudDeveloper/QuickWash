package com.mrdeveloper.quickwash.Model;

import java.io.Serializable;

public class ServiceItem implements Serializable {

    String category_name;
    String service_name;
    int quantity;
    double price_per_item;

    public ServiceItem(String category_name, String service_name, int quantity, double price_per_item) {
        this.category_name = category_name;
        this.service_name = service_name;
        this.quantity = quantity;
        this.price_per_item = price_per_item;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice_per_item() {
        return price_per_item;
    }

    public void setPrice_per_item(double price_per_item) {
        this.price_per_item = price_per_item;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}
