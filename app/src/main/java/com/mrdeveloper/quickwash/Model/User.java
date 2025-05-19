package com.mrdeveloper.quickwash.Model;

public class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String gender;
    private String date_of_birth;
    private String address;
    private String profile_pic;
    private double wallet_balance;
    private String created_at;

    // Getters (optional: setters if needed)
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getGender() { return gender; }
    public String getDate_of_birth() { return date_of_birth; }
    public String getAddress() { return address; }
    public String getProfile_pic() { return profile_pic; }
    public double getWallet_balance() { return wallet_balance; }
    public String getCreated_at() { return created_at; }
}
