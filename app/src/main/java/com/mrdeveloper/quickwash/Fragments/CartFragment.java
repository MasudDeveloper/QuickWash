package com.mrdeveloper.quickwash.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mrdeveloper.quickwash.Adapter.CartAdapter;
import com.mrdeveloper.quickwash.Adapter.GroupedCartAdapter;
import com.mrdeveloper.quickwash.Helper.CartManager;
import com.mrdeveloper.quickwash.Interface.ApiInterface;
import com.mrdeveloper.quickwash.Interface.ApiResponse;
import com.mrdeveloper.quickwash.Interface.RetrofitClient;
import com.mrdeveloper.quickwash.MainActivity;
import com.mrdeveloper.quickwash.Model.CartCategory;
import com.mrdeveloper.quickwash.Model.LaundryCategory;
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
    TextView textSubtotal, textEmptyCart, textDeliveryCharge, textDiscount, textGrandTotal;
    Button btnOrderNow, btnApplyPromo;
    EditText editName, editPhone, editAddress, editPickupDate, editPickupTime, editDeliveryDate, editPromoCode;
    Spinner spinnerPaymentMethod;

    CartAdapter adapter;
    GroupedCartAdapter groupedCartAdapter;
    List<Product> cartList = new ArrayList<>();
    List<LaundryCategory> categoryList;

    Context context;

    CardView cardRegular, cardPremium, cardExpress;
    Button btnSelectShop;
    TextView textDeliveryTime;
    String deliveryType = "", selectedShop = "";
    double subtotal, delivery, discount, grandTotal;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_cart, container, false);

        context = myView.getContext();
        recyclerCart = myView.findViewById(R.id.recyclerCart);
        textSubtotal = myView.findViewById(R.id.textSubtotal);
        textDeliveryCharge = myView.findViewById(R.id.textDeliveryCharge);
        textDiscount = myView.findViewById(R.id.textDiscount);
        textGrandTotal = myView.findViewById(R.id.textGrandTotal);
        textEmptyCart = myView.findViewById(R.id.textEmptyCart);
        btnOrderNow = myView.findViewById(R.id.btnOrderNow);
        btnApplyPromo = myView.findViewById(R.id.btnApplyPromo);
        editPromoCode = myView.findViewById(R.id.editPromoCode);
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

        addCategory();
        List<CartCategory> groupedList = CartManager.getCartGroupedByCategory(categoryList);


        groupedCartAdapter = new GroupedCartAdapter(groupedList, requireContext(), this::updateTotalUI);
        recyclerCart.setAdapter(groupedCartAdapter);

//        adapter = new CartAdapter(cartList, requireContext(), this::updateTotalUI);
//        recyclerCart.setAdapter(adapter);
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
            CartManager.setDeliveryType("Regular");
            updateTotalUI();
        });

        cardPremium.setOnClickListener(v -> {
            deliveryType = "Premium";
            textDeliveryTime.setText("Delivery Time: 24 hours");
            updateCardSelection(cardPremium, cardRegular, cardExpress);
            CartManager.setDeliveryType("Premium");
            updateTotalUI();
        });

        cardExpress.setOnClickListener(v -> {
            deliveryType = "Express";
            textDeliveryTime.setText("Delivery Time: 12 hours");
            updateCardSelection(cardExpress, cardRegular, cardPremium);
            CartManager.setDeliveryType("Express");
            updateTotalUI();
        });

        // দোকান সিলেক্ট বাটন
        btnSelectShop.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SelectShopActivity.class);
            startActivityForResult(intent, 101);
        });

        updateTotalUI();

        btnApplyPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String promo = editPromoCode.getText().toString().trim().toUpperCase();

                CartManager.applyPromoCode(promo); // Method below 👇
                updateTotalUI(); // উপরের checkout UI update method
            }
        });

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
        List<CartCategory> groupedList = CartManager.getCartGroupedByCategory(categoryList); // laundryCategories অবশ্যই পাওয়া যাবে
        groupedCartAdapter.cartList.clear();
        groupedCartAdapter.cartList.addAll(groupedList);

        groupedCartAdapter.notifyDataSetChanged();
        if (groupedCartAdapter.cartList.isEmpty()) {
            textEmptyCart.setVisibility(View.VISIBLE);
            recyclerCart.setVisibility(View.GONE);
            btnOrderNow.setEnabled(false);
            textSubtotal.setText("৳ 0");
            textDeliveryCharge.setText("৳ 0");
            textDiscount.setText("৳ 0");
            textGrandTotal.setText("৳ 0");
        } else {
            textEmptyCart.setVisibility(View.GONE);
            recyclerCart.setVisibility(View.VISIBLE);
            btnOrderNow.setEnabled(true);
            subtotal = CartManager.getTotalAmount();
            delivery = CartManager.getDeliveryCharge();
            discount = CartManager.getDiscountAmount();
            grandTotal = CartManager.getGrandTotal();

            textSubtotal.setText("৳" + String.format("%.2f", subtotal));
            textDeliveryCharge.setText("৳" + String.format("%.2f", delivery));
            textDiscount.setText("-৳" + String.format("%.2f", discount));
            textGrandTotal.setText("৳" + String.format("%.2f", grandTotal));
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
        order.setDelivery_charge(CartManager.getDeliveryCharge());
        order.setDiscount_amount(CartManager.getDiscountAmount());
        order.setShop_name(selectedShop);
        order.setPayment_method(paymentMethod);
        order.setTotal_amount(CartManager.getTotalAmount());

        List<ServiceItem> services = new ArrayList<>();
        List<CartCategory> groupedCart = CartManager.getCartGroupedByCategory(categoryList);
        for (CartCategory cartCategory : groupedCart) {
            String categoryName = cartCategory.getCategory().getTitle();
            for (Product p : cartCategory.getProductList()) {
                services.add(new ServiceItem(categoryName, p.getName(), p.getQuantity(), p.getPrice()));
            }
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
        Dialog successDialog = new Dialog(getContext());
        successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successDialog.setContentView(R.layout.dialog_order_success);

        successDialog.show();


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
        int height = 1800;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        Context context = getContext();

        // === Draw Logo ===
        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.only_logo);
        Bitmap resizedLogo = Bitmap.createScaledBitmap(logo, 220, 200, false);
        canvas.drawBitmap(resizedLogo, (width - 200) / 2, 20, null);  // Centered

        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(48);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Fast Wash", width / 2, 250, titlePaint);

        Paint subTitlePaint = new Paint();
        subTitlePaint.setTextSize(32);
        subTitlePaint.setColor(Color.DKGRAY);
        subTitlePaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Customer Invoice", width / 2, 300, subTitlePaint);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(26);
        paint.setTextAlign(Paint.Align.LEFT);

        int y = 360;
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

        // === Table Header with Category ===
        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        canvas.drawLine(50, y, width - 50, y, paint);
        canvas.drawText("Item", 60, y + 40, paint);
        canvas.drawText("Cat", width / 2 - 150, y + 40, paint);
        canvas.drawText("Qty", width / 2 + 60, y + 40, paint);
        canvas.drawText("Unit", width / 2 + 160, y + 40, paint);
        canvas.drawText("Total", width - 180, y + 40, paint);
        canvas.drawLine(50, y + 60, width - 50, y + 60, paint);
        paint.setTypeface(Typeface.DEFAULT);

        y += 100;
        double subtotal = 0;
        for (ServiceItem item : order.getService_list()) {
            double itemTotal = item.getQuantity() * item.getPrice_per_item();
            subtotal += itemTotal;

            canvas.drawText(item.getService_name(), 60, y, paint);
            canvas.drawText(item.getCategory_name(), width / 2 - 150, y, paint);
            canvas.drawText(String.valueOf(item.getQuantity()), width / 2 + 60 , y, paint);
            canvas.drawText(String.format("%.2f", item.getPrice_per_item()), width / 2 + 160, y, paint);
            canvas.drawText(String.format("%.2f", itemTotal), width - 180, y, paint);
            y += 40;
        }

        // === Checkout Summary ===
        double deliveryCharge = order.getDelivery_charge();   // Make sure this exists
        double discount = order.getDiscount_amount();         // Make sure this exists
        double grandTotal = subtotal + deliveryCharge - discount;

        y += 20;
        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        canvas.drawLine(50, y, width - 50, y, paint);
        y += 40;

        canvas.drawText("Subtotal:", width - 400, y, paint);
        canvas.drawText(String.format("%.2f BDT", subtotal), width - 200, y, paint);
        y += 40;

        canvas.drawText("Delivery Charge:", width - 400, y, paint);
        canvas.drawText(String.format("%.2f BDT", deliveryCharge), width - 200, y, paint);
        y += 40;

        canvas.drawText("Discount:", width - 400, y, paint);
        canvas.drawText(String.format("-%.2f BDT", discount), width - 200, y, paint);
        y += 40;

        canvas.drawText("Grand Total:", width - 400, y, paint);
        canvas.drawText(String.format("%.2f BDT", grandTotal), width - 200, y, paint);

        // === Draw Seal Image instead of status text ===
        drawSealImage(canvas, width, y + 100, context);

        // === Footer ===
        paint.setTextSize(24);
        paint.setTypeface(Typeface.DEFAULT);
        canvas.drawText("Thank you for choosing us!", 50, height - 120, paint);
        canvas.drawText("For support, call: 0123456789", 50, height - 80, paint);

        return bitmap;
    }





    private void drawSealImage(Canvas canvas, int width, int yPos, Context context) {
        Bitmap seal = BitmapFactory.decodeResource(context.getResources(), R.drawable.seal_pending); // seal.png
        Bitmap resizedSeal = Bitmap.createScaledBitmap(seal, 160, 160, false);
        canvas.drawBitmap(resizedSeal, width - 220, yPos, null);  // Top-right position
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

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showSuccessDialog();
            }
        });
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
        btnSelectShop.setText("Select Shop");
        selectedShop = "";
        updateCardSelection(cardPremium, cardRegular, cardExpress);
        CartManager.setDeliveryType("Regular");
        deliveryType = "Regular";
        CartManager.clearCart(getContext());
        editPromoCode.setText("");
        updateTotalUI();
    }

    public void addCategory() {

        categoryList = new ArrayList<>();

        categoryList.add(new LaundryCategory("1","Wash Only", "শুধু ধুয়ে দেয়া হবে", R.drawable.img_wash_only));
        categoryList.add(new LaundryCategory("2","Wash & Fold", "ধুয়ে ভাঁজ করে দেয়া হবে", R.drawable.img_wash_and_fold));
        categoryList.add(new LaundryCategory("3","Wash & Iron", "ধুয়ে ইস্ত্রি করা হবে", R.drawable.img_wash_and_iron));
        categoryList.add(new LaundryCategory("4","Dry Cleaning", "ড্রাই ক্লিনিং পরিষেবা (সুট, গাউন ইত্যাদি)", R.drawable.img_dry_clean));
        categoryList.add(new LaundryCategory("5","Iron Only", "শুধু ইস্ত্রি করা হবে", R.drawable.img_iron_only));
        categoryList.add(new LaundryCategory("6","Shoe Cleaning", "জুতা ধোয়া ও পলিশ", R.drawable.img_shoe_cleaning));
        categoryList.add(new LaundryCategory("7","Curtain Cleaning", "পর্দা ধোয়া ও ইস্ত্রি", R.drawable.img_curtain_cleaning));
        categoryList.add(new LaundryCategory("8","Blanket Cleaning", "কম্বল ধোয়া", R.drawable.img_blanket_cleaning));

    }


}