package com.mrdeveloper.quickwash;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.mrdeveloper.quickwash.Model.OrderRequest;
import com.mrdeveloper.quickwash.Model.ServiceItem;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderDetailsActivity extends AppCompatActivity {

    TextView tvOrderId, tvCustomerName, tvPhone, tvAddress, tvOrderDate, tvPickupDate, tvDeliveryDate, tvDeliveryType, tvTotalAmount, tvPaymentMethod;
    LinearLayout serviceContainer;
    Button btnViewInvoice, btnTrackOrder;
    OrderRequest order;
    Chip chipStatus;
    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find views by ID
        tvOrderId = findViewById(R.id.tvOrderId);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvPickupDate = findViewById(R.id.tvPickupDate);
        tvDeliveryDate = findViewById(R.id.tvDeliveryDate);
        tvDeliveryType = findViewById(R.id.tvDeliveryType);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        chipStatus = findViewById(R.id.chipStatus);
        serviceContainer = findViewById(R.id.serviceContainer);
        btnViewInvoice = findViewById(R.id.btnViewInvoice);
        btnTrackOrder = findViewById(R.id.btnTrackOrder);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get order from intent
        order = (OrderRequest) getIntent().getSerializableExtra("order");

        // Set order details
        tvOrderId.setText("Order ID: " + order.getId());
        tvCustomerName.setText("Name: " + order.getName());
        tvPhone.setText("Phone: " + order.getPhone());
        tvAddress.setText("Address: " + order.getAddress());
        tvOrderDate.setText("Date: " + order.getCreated_at());
        tvPickupDate.setText("Pickup Date: " + order.getPickup_date());
        tvDeliveryDate.setText("Delivery Date: " + order.getDelivery_date());
        tvDeliveryType.setText("Delivery Type: " + order.getDelivery_type());
        tvTotalAmount.setText("Total Amount: ৳" + order.getTotal_amount());
        tvPaymentMethod.setText("Payment Method: " + order.getPayment_method());
        chipStatus.setText(order.getStatus());
        chipStatus.setChipBackgroundColorResource(getStatusColor(order.getStatus()));

        // Clear existing views first
        serviceContainer.removeAllViews();

        // Add divider
        View divider = new View(this);
        divider.setBackgroundColor(ContextCompat.getColor(this, R.color.divider_grey));
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.divider_height)
        );
        dividerParams.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.padding_medium));
        divider.setLayoutParams(dividerParams);
        serviceContainer.addView(divider);

        // Dynamically add service items with enhanced design
        for (ServiceItem item : order.getService_list()) {
            // Create card for each service item
            MaterialCardView card = new MaterialCardView(this);
            card.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            card.setCardElevation(getResources().getDimension(R.dimen.card_elevation_small));
            card.setRadius(getResources().getDimension(R.dimen.card_corner_radius));
            card.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.card_stroke_width));
            card.setStrokeColor(ContextCompat.getColor(this, R.color.card_stroke));
            card.setUseCompatPadding(true);
            card.setContentPadding(
                    getResources().getDimensionPixelSize(R.dimen.padding_medium),
                    getResources().getDimensionPixelSize(R.dimen.padding_medium),
                    getResources().getDimensionPixelSize(R.dimen.padding_medium),
                    getResources().getDimensionPixelSize(R.dimen.padding_medium)
            );

            // Create inner layout
            LinearLayout cardLayout = new LinearLayout(this);
            cardLayout.setOrientation(LinearLayout.VERTICAL);
            cardLayout.setPadding(0, 0, 0, 0);

            // Service name with icon
            LinearLayout nameLayout = new LinearLayout(this);
            nameLayout.setOrientation(LinearLayout.HORIZONTAL);
            nameLayout.setGravity(Gravity.CENTER_VERTICAL);

            ImageView icon = new ImageView(this);
            icon.setImageResource(getServiceIcon(item.getCategory_name()));
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.icon_size),
                    getResources().getDimensionPixelSize(R.dimen.icon_size)
            );
            iconParams.setMargins(0, 0,
                    getResources().getDimensionPixelSize(R.dimen.padding_small), 0);
            icon.setLayoutParams(iconParams);
            nameLayout.addView(icon);

            TextView serviceName = new TextView(this);
            serviceName.setText(item.getService_name());
            serviceName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            serviceName.setTextColor(ContextCompat.getColor(this, R.color.primary_text));
            serviceName.setTypeface(null, Typeface.BOLD);
            nameLayout.addView(serviceName);

            cardLayout.addView(nameLayout);

            // Service details
            TextView category = new TextView(this);
            category.setText("Category: " + item.getCategory_name());
            category.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            category.setTextColor(ContextCompat.getColor(this, R.color.secondary_text));
            category.setPadding(0,
                    getResources().getDimensionPixelSize(R.dimen.padding_small), 0, 0);
            cardLayout.addView(category);

            // Price and quantity row
            LinearLayout priceQtyLayout = new LinearLayout(this);
            priceQtyLayout.setOrientation(LinearLayout.HORIZONTAL);
            priceQtyLayout.setGravity(Gravity.CENTER_VERTICAL);
            priceQtyLayout.setPadding(0,
                    getResources().getDimensionPixelSize(R.dimen.padding_small), 0, 0);

            TextView quantity = new TextView(this);
            quantity.setText("Qty: " + item.getQuantity());
            quantity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            quantity.setTextColor(ContextCompat.getColor(this, R.color.secondary_text));
            LinearLayout.LayoutParams qtyParams = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1
            );
            quantity.setLayoutParams(qtyParams);
            priceQtyLayout.addView(quantity);

            TextView price = new TextView(this);
            price.setText("৳" + (item.getPrice_per_item() * item.getQuantity()));
            price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            price.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            price.setTypeface(null, Typeface.BOLD);
            price.setGravity(Gravity.END);
            LinearLayout.LayoutParams priceParams = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1
            );
            price.setLayoutParams(priceParams);
            priceQtyLayout.addView(price);

            cardLayout.addView(priceQtyLayout);

            // Add layout to card
            card.addView(cardLayout);

            // Add card to container
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0,
                    getResources().getDimensionPixelSize(R.dimen.padding_small));
            serviceContainer.addView(card, cardParams);
        }

        // Helper method to get appropriate icon


        // Handle invoice button click
        btnViewInvoice.setOnClickListener(v -> {
            Bitmap invoiceBitmap = generateInvoiceBitmap(order, String.valueOf(order.getId()));
            if (invoiceBitmap != null) {
                showInvoiceDialog(invoiceBitmap, String.valueOf(order.getId()));
            } else {
                Toast.makeText(this, "Invoice not generated.", Toast.LENGTH_SHORT).show();
            }
        });

        btnTrackOrder.setOnClickListener(v -> {
            Intent intent = new Intent(OrderDetailsActivity.this, TrackOrderActivity.class);
            intent.putExtra("order_id", order.getId());
            intent.putExtra("status", order.getStatus());
            startActivity(intent);
        });

    }

    private int getStatusColor(String status) {
        switch (status) {
            case "Pending":
                return R.color.status_pending;
            case "Picked Up":
                return R.color.light_blue;
            case "Processing":
                return R.color.yellow;
            case "Quality Check":
                return R.color.orange;
            case "Out for Delivery":
                return R.color.blue;
            case "Delivered":
                return R.color.green;
            case "Cancelled":
                return R.color.red;
            default:
                return R.color.status_pending;
        }
    }

    private int getServiceIcon(String categoryName) {
        switch (categoryName) {
            case "Wash Only":
                return R.drawable.img_wash_only;
            case "Wash & Fold":
                return R.drawable.img_wash_and_fold;
            case "Wash & Iron":
                return R.drawable.img_wash_and_iron;
            case "Dry Cleaning":
                return R.drawable.img_dry_clean;
            case "Iron Only":
                return R.drawable.img_iron_only;
            case "Shoe Cleaning":
                return R.drawable.img_shoe_cleaning;
            case "Curtain Cleaning":
                return R.drawable.img_curtain_cleaning;
            case "Blanket Cleaning":
                return R.drawable.img_blanket_cleaning;
            default:
                return R.drawable.img_wash_only;
        }
    }

    private Bitmap generateInvoiceBitmap(OrderRequest order, String orderId) {
        int width = 1080;
        int height = 1920;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        // === Draw Logo ===
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.only_logo);
        Bitmap resizedLogo = Bitmap.createScaledBitmap(logo, 220, 200, false);
        canvas.drawBitmap(resizedLogo, (width - 200) / 2, 20, null);  // Centered

        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(48);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Quick Wash", width / 2, 250, titlePaint);

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
        double total = 0;
        for (ServiceItem item : order.getService_list()) {
            double subtotal = item.getQuantity() * item.getPrice_per_item();
            total += subtotal;

            canvas.drawText(item.getService_name(), 60, y, paint);
            canvas.drawText(item.getCategory_name(), width / 2 - 150, y, paint);
            canvas.drawText(String.valueOf(item.getQuantity()), width / 2 + 60 , y, paint);
            canvas.drawText(String.format("%.2f", item.getPrice_per_item()), width / 2 + 160, y, paint);
            canvas.drawText(String.format("%.2f", subtotal), width - 180, y, paint);
            y += 40;
        }

        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        canvas.drawLine(50, y + 10, width - 50, y + 10, paint);
        canvas.drawText("Total Amount: " + String.format("%.2f BDT", total), width - 400, y + 50, paint);

        // === Draw Seal Image instead of status text ===
        drawSealImage(canvas, width, y + 150, OrderDetailsActivity.this);

        // Footer
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
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailsActivity.this);
        builder.setTitle("Order Confirmation");

        // Create ImageView to display invoice
        ImageView imageView = new ImageView(OrderDetailsActivity.this);
        imageView.setImageBitmap(invoiceBitmap);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // Add download button
        builder.setView(imageView);
        builder.setPositiveButton("Download", (dialog, which) -> {
            saveInvoiceToGallery(invoiceBitmap, orderId);
            Toast.makeText(OrderDetailsActivity.this, "Invoice saved to gallery", Toast.LENGTH_SHORT).show();
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

        ContentResolver resolver = OrderDetailsActivity.this.getContentResolver();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}