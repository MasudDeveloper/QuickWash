package com.mrdeveloper.quickwash.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mrdeveloper.quickwash.LoginActivity;
import com.mrdeveloper.quickwash.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private CircleImageView profileImage;
    private TextView profileName, profileEmail, ordersPending ;
    private Button logoutButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_profile, container, false);


        // Initialize views
        profileImage = myView.findViewById(R.id.profile_image);
        profileName = myView.findViewById(R.id.profile_name);
        profileEmail = myView.findViewById(R.id.profile_email);
        ordersPending = myView.findViewById(R.id.ordersPending);
        logoutButton = myView.findViewById(R.id.logout_button);

        // Load user data
        loadUserData();

        // Set click listeners
        logoutButton.setOnClickListener(v -> showLogoutDialog());

        return myView;
    } // =====================================================

    private void loadUserData() {
        // Load data from shared preferences or API
        SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        String name = prefs.getString("user_name", "User Name");
        String email = prefs.getString("user_email", "user@example.com");
        int orders = prefs.getInt("total_orders", 0);

        profileName.setText(name);
        profileEmail.setText(email);
        ordersPending.setText(String.valueOf(orders));

        // Load profile image (using Glide/Picasso)
        String imageUrl = prefs.getString("profile_image", null);
        if (imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.user_icon)
                    .error(R.drawable.user_icon)
                    .into(profileImage);
        }
    }

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    // Perform logout
                    performLogout();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performLogout() {
        // Clear user session
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        // Navigate to login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
