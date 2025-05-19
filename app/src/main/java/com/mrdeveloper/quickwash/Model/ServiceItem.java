package com.mrdeveloper.quickwash.Model;

public class ServiceItem {

    String service_name;
    int quantity;
    double price_per_item;

    public ServiceItem(String service_name, int quantity, double price_per_item) {
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
}
