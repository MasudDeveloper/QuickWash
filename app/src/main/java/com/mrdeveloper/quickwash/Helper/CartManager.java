package com.mrdeveloper.quickwash.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mrdeveloper.quickwash.Model.Product;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static final String PREF_NAME = "QuickWashCart";
    private static final String CART_KEY = "cart_items";

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
        saveCartToPrefs(context);
    }
}
