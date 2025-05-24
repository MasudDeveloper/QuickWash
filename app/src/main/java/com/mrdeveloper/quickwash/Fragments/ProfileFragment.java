package com.mrdeveloper.quickwash.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mrdeveloper.quickwash.Interface.ApiInterface;
import com.mrdeveloper.quickwash.Interface.ApiResponse;
import com.mrdeveloper.quickwash.Interface.RetrofitClient;
import com.mrdeveloper.quickwash.LoginActivity;
import com.mrdeveloper.quickwash.MainActivity;
import com.mrdeveloper.quickwash.R;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {

    private CircleImageView profileImage;
    private TextView profileName, profileEmail, ordersPending ;
    private Button logoutButton;
    SharedPreferences prefs;
    Uri selectedImageUri;
    MaterialCardView editProfileCard;


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
        editProfileCard = myView.findViewById(R.id.edit_profile_card);

        prefs = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Load user data
        loadUserData();


        editProfileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

        // Set click listeners
        logoutButton.setOnClickListener(v -> showLogoutDialog());

        return myView;
    } // =====================================================

    private void loadUserData() {
        // Load data from shared preferences or API

//        String name = prefs.getString("user_name", "Masud Rana");
//        String email = prefs.getString("user_email", "user@example.com");
//        String phone = prefs.getString("user_phone", "01600-000000");
//        String gender = prefs.getString("user_gender", "Male");
//        String dob = prefs.getString("user_dob", "01/01/2000");
//        String address = prefs.getString("user_address", "Dhaka, Bangladesh");
//        String profilePic = prefs.getString("user_profile_pic", null);
//        int orders = prefs.getInt("total_orders", 0);
//
//        profileName.setText(name);
//        profileEmail.setText(email);

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
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("phone","").apply();

        // Navigate to login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }


    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Initialize views
        CircleImageView profileImage = dialogView.findViewById(R.id.profile_image);
        TextInputEditText etName = dialogView.findViewById(R.id.et_name);
        TextInputEditText etEmail = dialogView.findViewById(R.id.et_email);
        TextInputEditText etPhone = dialogView.findViewById(R.id.et_phone);
        AutoCompleteTextView etGender = dialogView.findViewById(R.id.et_gender);
        TextInputEditText etDob = dialogView.findViewById(R.id.et_dob);
        TextInputEditText etAddress = dialogView.findViewById(R.id.et_address);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        ImageView btnChangeImage = dialogView.findViewById(R.id.btn_change_image);

        // Load current user data
        SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        etName.setText(prefs.getString("name", ""));
        etEmail.setText(prefs.getString("email", ""));
        etPhone.setText(prefs.getString("phone", ""));
        etGender.setText(prefs.getString("gender", ""));
        etDob.setText(prefs.getString("date_of_birth", ""));
        etAddress.setText(prefs.getString("address", ""));

        // Load profile image
        String imageUrl = prefs.getString("profile_pic", null);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.demo_user)
                    .into(profileImage);
        }

        // Gender dropdown setup
        String[] genders = new String[]{"Male", "Female"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                genders
        );
        etGender.setAdapter(genderAdapter);
        etGender.setOnItemClickListener((parent, view, position, id) -> {
            etGender.setText(genderAdapter.getItem(position));
        });

        // Date picker for DOB
        etDob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(
                    getContext(),
                    (view, year, month, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        etDob.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        // Change profile image
//        btnChangeImage.setOnClickListener(v -> {
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//        });

        // Select profile picture
        btnChangeImage.setOnClickListener(v -> {

            ImagePicker.with(this)
                    .cropSquare()	    			//Crop image(Optional), Check Customization for more option
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .galleryOnly()
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickerLauncher.launch(intent);
                            return null;
                        }
                    });
        });

        // Save changes
        btnSave.setOnClickListener(v -> {
            if (validateInputs(etName, etEmail, etPhone)) {
                updateProfile(
                        etName.getText().toString(),
                        etEmail.getText().toString(),
                        etPhone.getText().toString(),
                        etGender.getText().toString(),
                        etDob.getText().toString(),
                        etAddress.getText().toString(),
                        dialog
                );
            }
        });
    }

    private boolean validateInputs(TextInputEditText... inputs) {
        boolean isValid = true;

        for (TextInputEditText input : inputs) {
            if (input.getText().toString().trim().isEmpty()) {
                input.setError("This field is required");
                isValid = false;
            } else {
                input.setError(null);
            }
        }

        return isValid;
    }

    private void updateProfile(String name, String email, String phone,
                               String gender, String dob, String address,
                               AlertDialog dialog) {

        // Show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Updating profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Get user ID from shared preferences
        SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", 0);

        // Create API request
        ApiInterface apiInterface = RetrofitClient.getApiService();
        Call<ApiResponse> call = apiInterface.updateProfile(userId, name, email, phone, gender, dob, address);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        // Update local preferences
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("name", name);
                        editor.putString("email", email);
                        editor.putString("phone", phone);
                        editor.putString("gender", gender);
                        editor.putString("date_of_birth", dob);
                        editor.putString("address", address);
                        editor.apply();

                        // Update UI
                        //updateProfileUI();
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle image selection result
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri imageUri = data.getData();
//            uploadProfileImage(imageUri);
//        }
//    }

    private void uploadProfileImage(Uri imageUri) {
        // Implement image upload to server
        // This would typically involve:
        // 1. Showing progress dialog
        // 2. Creating a file from the URI
        // 3. Uploading to server using Multipart request
        // 4. Updating user profile with new image URL
        // 5. Saving new URL in shared preferences
        // 6. Updating UI with new image
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
//                    Glide.with(getContext()).load(user.getProfile_pic_url())
//                            .placeholder(R.drawable.demo_user)
//                            .error(R.drawable.user_icon).into(profileImage);

                    // Upload image to server and update URL in database
                    profileImage.setImageURI(selectedImageUri);
                }
            });

}
