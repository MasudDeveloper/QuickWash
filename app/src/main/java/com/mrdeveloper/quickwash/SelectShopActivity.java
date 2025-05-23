package com.mrdeveloper.quickwash;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mrdeveloper.quickwash.Adapter.ShopAdapter;
import com.mrdeveloper.quickwash.Model.Shop;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SelectShopActivity extends AppCompatActivity {

    MapView mapView;
    RecyclerView recyclerView;
    //List<String> shopListString = Arrays.asList("Bismillah Laundry-2", "Palton Cleaners", "Dhaka Dry Wash");
    List<Shop> shopList;
    MyLocationNewOverlay locationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_shop);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // OSMdroid কনফিগারেশন
        Configuration.getInstance().setUserAgentValue(getPackageName());
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        // ম্যাপ ভিউ ইনিশিয়ালাইজেশন
        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // লোকেশন ওভারলে সেটআপ
        setupLocationOverlay();

        addShopList();

        // রিসাইক্লার ভিউ সেটআপ
        setupRecyclerView();

        // শপ মার্কার যোগ করুন
        addShopMarkers();
    }

    private void setupLocationOverlay() {
        // লোকেশন পারমিশন চেক করুন
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            initLocationOverlay();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    1);
        }
    }

    private void initLocationOverlay() {
        // লোকেশন ওভারলে ইনিশিয়ালাইজ করুন
        locationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(this),
                mapView);

        locationOverlay.enableMyLocation();
        locationOverlay.enableFollowLocation();
        locationOverlay.setDrawAccuracyEnabled(true);

        mapView.getOverlays().add(locationOverlay);

        // ডিফল্ট লোকেশনে জুম করুন
        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        locationOverlay.runOnFirstFix(() -> runOnUiThread(() -> {
            mapController.animateTo(locationOverlay.getMyLocation());
        }));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLocationOverlay();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerShops);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ShopAdapter(shopList, name -> {
            Intent result = new Intent();
            result.putExtra("shop_name", name);
            setResult(RESULT_OK, result);
            finish();
        }));
    }

    private void addShopMarkers() {
        List<GeoPoint> shopPoints = Arrays.asList(
                new GeoPoint(23.73581669155619, 90.41531594590718), // Bismillah Laundry-2
                new GeoPoint(23.728081601040326, 90.42133898994176), // Palton Cleaners
                new GeoPoint(23.734052792520597, 90.4179465100099), // Dhaka Dry Wash
                new GeoPoint(23.734809517604457, 90.41040305855019),  // Dhaka Dry Wash
                new GeoPoint(23.73027186725626, 90.4104961381954)  // Dhaka Dry Wash

        );

        for (int i = 0; i < shopPoints.size(); i++) {
            Marker marker = new Marker(mapView);
            marker.setPosition(shopPoints.get(i));
            marker.setTitle(shopList.get(i).getName());
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(ContextCompat.getDrawable(this, R.drawable.icon_marker));
            mapView.getOverlays().add(marker);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    public void addShopList(){
        shopList = new ArrayList<>();
        shopList.add(new Shop("1", "Clean & Fresh Laundry", R.drawable.img_wash_and_fold, 4.2f, 120, "Nayapalton, Dhaka", "8AM", "10PM", true));
        shopList.add(new Shop("2", "Bright Wash", R.drawable.img_wash_only, 4.5f, 150, "Motijheel, Dhaka", "9AM", "11PM", true));
        shopList.add(new Shop("3", "Sparkling Dry Clean", R.drawable.img_dry_clean, 4.0f, 100, "Fakirapool, Dhaka", "7AM", "9PM", true));
        shopList.add(new Shop("4", "Eco Laundry", R.drawable.img_iron_only, 4.8f, 200, "Bijoynagar, Dhaka", "8AM", "10PM", true));
        shopList.add(new Shop("5", "Fresh & Clean", R.drawable.img_wash_and_iron, 4.3f, 130, "Palton, Dhaka", "9AM", "11PM", true));
    }

}

