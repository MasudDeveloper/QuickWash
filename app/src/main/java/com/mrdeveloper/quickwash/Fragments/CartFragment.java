package com.mrdeveloper.quickwash.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mrdeveloper.quickwash.Adapter.CartAdapter;
import com.mrdeveloper.quickwash.Helper.CartManager;
import com.mrdeveloper.quickwash.Interface.ApiInterface;
import com.mrdeveloper.quickwash.Interface.ApiResponse;
import com.mrdeveloper.quickwash.Interface.RetrofitClient;
import com.mrdeveloper.quickwash.MainActivity;
import com.mrdeveloper.quickwash.Model.OrderRequest;
import com.mrdeveloper.quickwash.Model.Product;
import com.mrdeveloper.quickwash.Model.ServiceItem;
import com.mrdeveloper.quickwash.R;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CartFragment extends Fragment {

    RecyclerView recyclerCart;
    TextView textViewTotal, textEmptyCart;
    Button btnOrderNow;
    CartAdapter adapter;
    List<Product> cartList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerCart = myView.findViewById(R.id.recyclerCart);
        textViewTotal = myView.findViewById(R.id.textViewTotal);
        textEmptyCart = myView.findViewById(R.id.textEmptyCart);
        btnOrderNow = myView.findViewById(R.id.btnOrderNow);

        CartManager.loadCartFromPrefs(requireContext());
//        cartList = CartManager.getCartList();
//
//        adapter = new CartAdapter(cartList, requireContext(), this::updateTotalUI);
        adapter = new CartAdapter(CartManager.getCartList(), requireContext(), this::updateTotalUI);
        recyclerCart.setAdapter(adapter);
        recyclerCart.setLayoutManager(new LinearLayoutManager(getContext()));

        updateTotalUI();

        btnOrderNow.setOnClickListener(v -> {
            if (cartList.isEmpty()) return;

            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Order")
                    .setMessage("Do you want to place this order?")
                    .setPositiveButton("Yes", (dialog, which) -> submitOrder())
                    .setNegativeButton("Cancel", null)
                    .show();
        });


        return myView;
    } // ======================== On Create View End ======================

    private void updateTotalUI() {
        // সামঞ্জস্য নিশ্চিত করতে সব আইটেম ক্লিয়ার করে আবার যোগ করুন
        adapter.cartList.clear();
        adapter.cartList.addAll(CartManager.getCartList());

        adapter.notifyDataSetChanged();
        if (adapter.cartList.isEmpty()) {
            textEmptyCart.setVisibility(View.VISIBLE);
            recyclerCart.setVisibility(View.GONE);
            btnOrderNow.setEnabled(false);
            textViewTotal.setText("Total: ৳ 0");
        } else {
            textEmptyCart.setVisibility(View.GONE);
            recyclerCart.setVisibility(View.VISIBLE);
            btnOrderNow.setEnabled(true);
            textViewTotal.setText("Total: ৳ " + CartManager.getTotalAmount());
        }
    }

    private void submitOrder() {

        OrderRequest order = new OrderRequest();
        order.setUser_id(MainActivity.USER_ID);
        order.setName(MainActivity.USER_NAME);
        order.setPhone(MainActivity.PHONE);
        order.setAddress("53 Nayapalton Dhaka");
        order.setPickup_date("20 May");
        order.setPickup_time("pickupTime");
        order.setDelivery_date("deliveryDate");
        order.setPayment_method("Cash On Delivery");
        order.setTotal_amount(CartManager.getTotalAmount());

        List<ServiceItem> services = new ArrayList<>();
        for (Product p : CartManager.getCartList()) {
            services.add(new ServiceItem(p.getName(), p.getQuantity(), p.getPrice()));
        }
        order.setService_list(services);


        ApiInterface apiInterface = RetrofitClient.getApiService();

        apiInterface.submitOrder(order).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    showSuccessDialog();
                    CartManager.clearCart(getContext());
                    updateTotalUI();
                    Toast.makeText(getContext(), "Order placed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to place order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Network error"+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Order Confirmed")
                .setMessage("Thank you! Your laundry order has been placed.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }


}