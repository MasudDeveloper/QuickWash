package com.mrdeveloper.quickwash.Model;

import java.util.List;

public class OrderRequest {

    int user_id;
    String name, phone, address, pickup_date, pickup_time, delivery_date, delivery_type, shop_name, payment_method;
    double total_amount;
    List<ServiceItem> service_list;

    public OrderRequest() {
    }

    public OrderRequest(int user_id, String name, String phone, String address, String pickup_date, String pickup_time, String delivery_date, String payment_method, double total_amount, List<ServiceItem> service_list) {
        this.user_id = user_id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.pickup_date = pickup_date;
        this.pickup_time = pickup_time;
        this.delivery_date = delivery_date;
        this.payment_method = payment_method;
        this.total_amount = total_amount;
        this.service_list = service_list;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPickup_date() {
        return pickup_date;
    }

    public void setPickup_date(String pickup_date) {
        this.pickup_date = pickup_date;
    }

    public String getPickup_time() {
        return pickup_time;
    }

    public void setPickup_time(String pickup_time) {
        this.pickup_time = pickup_time;
    }

    public String getDelivery_date() {
        return delivery_date;
    }

    public void setDelivery_date(String delivery_date) {
        this.delivery_date = delivery_date;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    public List<ServiceItem> getService_list() {
        return service_list;
    }

    public void setService_list(List<ServiceItem> service_list) {
        this.service_list = service_list;
    }

    public String getDelivery_type() {
        return delivery_type;
    }

    public void setDelivery_type(String delivery_type) {
        this.delivery_type = delivery_type;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }
}
