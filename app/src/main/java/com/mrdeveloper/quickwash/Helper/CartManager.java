package com.mrdeveloper.quickwash.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mrdeveloper.quickwash.Model.CartCategory;
import com.mrdeveloper.quickwash.Model.LaundryCategory;
import com.mrdeveloper.quickwash.Model.Product;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartManager {

    private static final String PREF_NAME = "QuickWashCart";
    private static final String CART_KEY = "cart_items";
    private static String deliveryType = "Regular"; // default
    private static String appliedPromoCode = "";
    private static double discountAmount = 0.0;

    public static double getDiscountAmount() {
        return discountAmount;
    }

    public static String getAppliedPromoCode() {
        return appliedPromoCode;
    }

    public static void setDeliveryType(String type) {
        deliveryType = type;
    }

    private static List<Product> cartList = new ArrayList<>();

    public static void loadCartFromPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(CART_KEY, "");
        if (!json.isEmpty()) {
            Type type = new TypeToken<List<Product>>() {}.getType();
            cartList = new Gson().fromJson(json, type);
        }
    }

    public static void saveCartToPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(cartList);
        editor.putString(CART_KEY, json);
        editor.apply();
    }

    public static List<Product> getCartList() {
        return new ArrayList<>(cartList); // রেফারেন্স ইস্যু এড়াতে কপি রিটার্ন করুন
    }

    public static void addToCart(Product product, Context context) {
        boolean exists = false;
        for (Product p : cartList) {
            if (p.getId() == product.getId()) {
                p.setQuantity(p.getQuantity() + 1);
                exists = true;
                break;
            }
        }
        if (!exists) {
            product.setQuantity(1);
            cartList.add(product);
        }
        saveCartToPrefs(context);
    }

    public static void removeFromCart(Product product, Context context) {
        cartList.removeIf(p -> p.getId() == product.getId());
        saveCartToPrefs(context);
    }

    public static void updateQuantity(Product product, int quantity, Context context) {
        for (Product p : cartList) {
            if (p.getId() == product.getId()) {
                p.setQuantity(quantity);
                break;
            }
        }
        saveCartToPrefs(context);
    }

    public static double getTotalAmount() {
        double total = 0;
        for (Product p : cartList) {
            total += p.getPrice() * p.getQuantity();
        }
        return total;
    }

    public static void clearCart(Context context) {
        cartList.clear();
        deliveryType = "Regular";
        appliedPromoCode = "";
        discountAmount = 0.0;
        saveCartToPrefs(context);
    }

    public static List<CartCategory> getCartGroupedByCategory(List<LaundryCategory> categories) {
        Map<String, List<Product>> groupedMap = new HashMap<>();

        for (Product p : cartList) {
            String categoryId = String.valueOf(p.getCategory_id());
            if (!groupedMap.containsKey(categoryId)) {
                groupedMap.put(categoryId, new ArrayList<>());
            }
            groupedMap.get(categoryId).add(p);
        }

        List<CartCategory> groupedList = new ArrayList<>();

        for (LaundryCategory cat : categories) {
            if (groupedMap.containsKey(cat.getId())) {
                groupedList.add(new CartCategory(cat, groupedMap.get(cat.getId())));
            }
        }

        return groupedList;
    }

    public static double getGrandTotal() {
        return getTotalAmount() + getDeliveryCharge() - getDiscountAmount();
    }


    public static double getDeliveryCharge() {
        switch (deliveryType) {
            case "Premium":
                return 100.0;
            case "Express":
                return 150.0;
            case "Regular":
                return 50.0;
            default:
                return 0.0;
        }
    }


    public static boolean applyPromoCode(String code) {
        switch (code.toUpperCase()) {
            case "SAVE10":
                discountAmount = 10.0;
                appliedPromoCode = code;
                return true;
            case "SAVE50":
                discountAmount = 50.0;
                appliedPromoCode = code;
                return true;
            default:
                discountAmount = 0.0;
                appliedPromoCode = "";
                return false;
        }
    }




}
