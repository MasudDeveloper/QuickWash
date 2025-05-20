package com.mrdeveloper.quickwash.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.mrdeveloper.quickwash.SelectShopActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CartFragment extends Fragment {

    RecyclerView recyclerCart;
    TextView textViewTotal, textEmptyCart;
    Button btnOrderNow;
    EditText editName, editPhone, editAddress, editPickupDate, editPickupTime, editDeliveryDate;
    Spinner spinnerPaymentMethod;

    CartAdapter adapter;
    List<Product> cartList = new ArrayList<>();

    Context context;

    CardView cardRegular, cardPremium, cardExpress;
    Button btnSelectShop;
    TextView textDeliveryTime;
    String deliveryType = "", selectedShop = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_cart, container, false);

        context = myView.getContext();
        recyclerCart = myView.findViewById(R.id.recyclerCart);
        textViewTotal = myView.findViewById(R.id.textViewTotal);
        textEmptyCart = myView.findViewById(R.id.textEmptyCart);
        btnOrderNow = myView.findViewById(R.id.btnOrderNow);
        editName = myView.findViewById(R.id.editName);
        editPhone = myView.findViewById(R.id.editPhone);
        editAddress = myView.findViewById(R.id.editAddress);
        editPickupDate = myView.findViewById(R.id.editPickupDate);
        editPickupTime = myView.findViewById(R.id.editPickupTime);
        editDeliveryDate = myView.findViewById(R.id.editDeliveryDate);
        spinnerPaymentMethod = myView.findViewById(R.id.spinnerPaymentMethod);

        // CardView ইনিশিয়ালাইজ
        cardRegular = myView.findViewById(R.id.cardRegular);
        cardPremium = myView.findViewById(R.id.cardPremium);
        cardExpress = myView.findViewById(R.id.cardExpress);
        textDeliveryTime = myView.findViewById(R.id.textDeliveryTime);
        btnSelectShop = myView.findViewById(R.id.btnSelectShop);

        // Setup Spinner
        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item,
                new String[]{"Cash On Delivery", "bKash", "Nagad"});
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(paymentAdapter);

        CartManager.loadCartFromPrefs(requireContext());
        cartList = CartManager.getCartList();

        adapter = new CartAdapter(cartList, requireContext(), this::updateTotalUI);
        recyclerCart.setAdapter(adapter);
        recyclerCart.setLayoutManager(new LinearLayoutManager(getContext()));

        // Date picker
        editPickupDate.setOnClickListener(v -> showDatePicker(editPickupDate));
        editDeliveryDate.setOnClickListener(v -> showDatePicker(editDeliveryDate));
        editPickupTime.setOnClickListener(v -> showTimePicker(editPickupTime));

        // ডেলিভারি টাইপ সিলেকশন হ্যান্ডলার
        cardRegular.setOnClickListener(v -> {
            deliveryType = "Regular";
            textDeliveryTime.setText("Delivery Time: 48 hours");
            updateCardSelection(cardRegular, cardPremium, cardExpress);
        });

        cardPremium.setOnClickListener(v -> {
            deliveryType = "Premium";
            textDeliveryTime.setText("Delivery Time: 24 hours");
            updateCardSelection(cardPremium, cardRegular, cardExpress);
        });

        cardExpress.setOnClickListener(v -> {
            deliveryType = "Express";
            textDeliveryTime.setText("Delivery Time: 12 hours");
            updateCardSelection(cardExpress, cardRegular, cardPremium);
        });

        // দোকান সিলেক্ট বাটন
        btnSelectShop.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SelectShopActivity.class);
            startActivityForResult(intent, 101);
        });


        updateTotalUI();

        btnOrderNow.setOnClickListener(v -> {

            String name = editName.getText().toString();
            String phone = editPhone.getText().toString();
            String address = editAddress.getText().toString();
            String pickupDate = editPickupDate.getText().toString();
            String pickupTime = editPickupTime.getText().toString();
            String deliveryDate = editDeliveryDate.getText().toString();
            String paymentMethod = spinnerPaymentMethod.getSelectedItem().toString();

            if (cartList.isEmpty()) {
                Toast.makeText(getContext(), "Cart is Empty", Toast.LENGTH_SHORT).show();
            } else if (name.isEmpty()) {
                Toast.makeText(getContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
                editName.setError("Please enter your name");
            } else if (phone.isEmpty()) {
                Toast.makeText(getContext(), "Please enter your phone number", Toast.LENGTH_SHORT).show();
                editPhone.setError("Please enter your phone number");
            } else if (address.isEmpty()) {
                Toast.makeText(getContext(), "Please enter your address", Toast.LENGTH_SHORT).show();
                editAddress.setError("Please enter your address");
            } else if (pickupDate.isEmpty()) {
                Toast.makeText(getContext(), "Please select pickup date", Toast.LENGTH_SHORT).show();
                editPickupDate.setError("Please select pickup date");
            } else if (pickupTime.isEmpty()) {
                Toast.makeText(getContext(), "Please select pickup time", Toast.LENGTH_SHORT).show();
                editPickupTime.setError("Please select pickup time");
            } else if (deliveryDate.isEmpty()) {
                Toast.makeText(getContext(), "Please select delivery date", Toast.LENGTH_SHORT).show();
                editDeliveryDate.setError("Please select delivery date");
            } else if (deliveryType.isEmpty()) {
                Toast.makeText(context, "Please select delivery type", Toast.LENGTH_SHORT).show();
            } else if (selectedShop.isEmpty()) {
                Toast.makeText(context, "Please Select Shop", Toast.LENGTH_SHORT).show();
            } else if (paymentMethod.isEmpty()) {
                Toast.makeText(getContext(), "Please select payment method", Toast.LENGTH_SHORT).show();
            } else {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Order")
                        .setMessage("Do you want to place this order?")
                        .setPositiveButton("Yes", (dialog, which) -> submitOrder(name, phone, address, pickupDate, pickupTime, deliveryDate, deliveryType, selectedShop, paymentMethod))
                        .setNegativeButton("Cancel", null)
                        .show();
            }

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

    private void submitOrder(String name, String phone, String address, String pickupDate,
                             String pickupTime, String deliveryDate, String deliveryType,
                             String selectedShop, String paymentMethod) {

        OrderRequest order = new OrderRequest();
        order.setUser_id(MainActivity.USER_ID);
        order.setName(name);
        order.setPhone(phone);
        order.setAddress(address);
        order.setPickup_date(pickupDate);
        order.setPickup_time(pickupTime);
        order.setDelivery_date(deliveryDate);
        order.setDelivery_type(deliveryType);
        order.setShop_name(selectedShop);
        order.setPayment_method(paymentMethod);
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
                    try {
                        // Get order ID from response
                        String responseString = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseString);
                        String orderId = jsonResponse.getString("order_id");

                        // Generate invoice
                        Bitmap invoiceBitmap = generateInvoiceBitmap(order, orderId);
                        showInvoiceDialog(invoiceBitmap, orderId);

                        // Clear cart and reset fields
                        CartManager.clearCart(getContext());
                        resetFormFields();

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error processing invoice", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to place order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, year, month, dayOfMonth) -> {
                    String date = dayOfMonth + "-" + (month + 1) + "-" + year;
                    editText.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                (view, hourOfDay, minute) -> {
                    Calendar tempCalendar = Calendar.getInstance();
                    tempCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    tempCalendar.set(Calendar.MINUTE, minute);

                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                    String time = sdf.format(tempCalendar.getTime());
                    editText.setText(time);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false); // 12-hour ফরম্যাট
        timePickerDialog.show();
    }

    // কার্ড সিলেকশন স্টাইল আপডেট করার মেথড
    private void updateCardSelection(CardView selectedCard, CardView... otherCards) {
        // সিলেক্টেড কার্ডের স্টাইল পরিবর্তন
        selectedCard.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.selected_card_color));
        selectedCard.setCardElevation(8f);

        // অন্যান্য কার্ডের স্টাইল রিসেট
        for (CardView card : otherCards) {
            card.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.default_card_color));
            card.setCardElevation(4f);
        }
    }

    // দোকান সিলেক্ট করার পর রেজাল্ট হ্যান্ডলিং
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            selectedShop = data.getStringExtra("shop_name");
            btnSelectShop.setText("Selected Shop: " + selectedShop);
        }
    }

    private Bitmap generateInvoiceBitmap(OrderRequest order, String orderId) {
        int width = 1080;
        int height = 1920;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(48);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Azad Laundry Services", width / 2, 100, titlePaint);

        Paint subTitlePaint = new Paint();
        subTitlePaint.setTextSize(32);
        subTitlePaint.setColor(Color.DKGRAY);
        subTitlePaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Customer Invoice", width / 2, 150, subTitlePaint);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(26);
        paint.setTextAlign(Paint.Align.LEFT);

        int y = 220;
        canvas.drawText("Order ID: " + orderId, 50, y, paint);
        canvas.drawText("Order Date: " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()), width / 2, y, paint);

        y += 60;
        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        canvas.drawText("Customer Info:", 50, y, paint);
        paint.setTypeface(Typeface.DEFAULT);

        y += 40;
        canvas.drawText("Name: " + order.getName(), 50, y, paint);
        canvas.drawText("Phone: " + order.getPhone(), 50, y + 40, paint);
        canvas.drawText("Address: " + order.getAddress(), 50, y + 80, paint);

        y += 140;
        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        canvas.drawText("Order Details:", 50, y, paint);
        paint.setTypeface(Typeface.DEFAULT);

        y += 40;
        canvas.drawText("Pickup: " + order.getPickup_date() + " at " + order.getPickup_time(), 50, y, paint);
        canvas.drawText("Delivery: " + order.getDelivery_date() + " (" + order.getDelivery_type() + ")", 50, y + 40, paint);
        canvas.drawText("Shop: " + order.getShop_name(), 50, y + 80, paint);
        canvas.drawText("Payment Method: " + order.getPayment_method(), 50, y + 120, paint);

        y += 180;

        // Draw table headers
        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        canvas.drawLine(50, y, width - 50, y, paint);
        canvas.drawText("Item", 60, y + 40, paint);
        canvas.drawText("Qty", width / 2 - 100, y + 40, paint);
        canvas.drawText("Unit", width / 2 + 50, y + 40, paint);
        canvas.drawText("Total", width - 180, y + 40, paint);
        canvas.drawLine(50, y + 60, width - 50, y + 60, paint);
        paint.setTypeface(Typeface.DEFAULT);

        y += 100;
        double total = 0;
        for (ServiceItem item : order.getService_list()) {
            double subtotal = item.getQuantity() * item.getPrice_per_item();
            total += subtotal;

            canvas.drawText(item.getService_name(), 60, y, paint);
            canvas.drawText(String.valueOf(item.getQuantity()), width / 2 - 100, y, paint);
            canvas.drawText(String.format("%.2f", item.getPrice_per_item()), width / 2 + 50, y, paint);
            canvas.drawText(String.format("%.2f", subtotal), width - 180, y, paint);
            y += 40;
        }

        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        canvas.drawLine(50, y + 10, width - 50, y + 10, paint);
        canvas.drawText("Total Amount: " + String.format("%.2f BDT", total), width - 400, y + 50, paint);

        // Status Seal
        drawStatusSeal(canvas, width, y + 150, "CONFIRMED");

        // Footer
        paint.setTextSize(24);
        paint.setTypeface(Typeface.DEFAULT);
        canvas.drawText("Thank you for choosing us!", 50, height - 120, paint);
        canvas.drawText("For support, call: 0123456789", 50, height - 80, paint);

        return bitmap;
    }


    private void drawStatusSeal(Canvas canvas, int width, int yPos, String status) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);

        // Draw circular seal
        int centerX = width - 150;
        int radius = 80;
        canvas.drawCircle(centerX, yPos, radius, paint);

        // Draw status text
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(status, centerX, yPos+10, paint);
        paint.setTextSize(20);
        canvas.drawText("ORDER", centerX, yPos+40, paint);
    }

    private void showInvoiceDialog(Bitmap invoiceBitmap, String orderId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Order Confirmation");

        // Create ImageView to display invoice
        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(invoiceBitmap);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // Add download button
        builder.setView(imageView);
        builder.setPositiveButton("Download", (dialog, which) -> {
            saveInvoiceToGallery(invoiceBitmap, orderId);
            Toast.makeText(getContext(), "Invoice saved to gallery", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Close", null);
        builder.show();
    }

    private void saveInvoiceToGallery(Bitmap bitmap, String orderId) {
        String fileName = "Invoice_" + orderId + ".jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        ContentResolver resolver = getContext().getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream outputStream = resolver.openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetFormFields() {
        editName.setText("");
        editPhone.setText("");
        editAddress.setText("");
        editPickupDate.setText("");
        editPickupTime.setText("");
        editDeliveryDate.setText("");
        spinnerPaymentMethod.setSelection(0);
        updateTotalUI();
    }


}