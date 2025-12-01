package com.example.summit.fragments.entrant;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.example.summit.EntrantActivity;
import com.example.summit.MainActivity;
import com.example.summit.R;
import com.example.summit.model.Entrant;
import com.example.summit.model.Firebase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * Profile Fragment for EntrantActivity.
 * <purpose>
 * This fragment belongs solely to the EntrantActivity clsss.
 * The user can navigate to this fragment through the entrant_bottom_nav toolbar.
 * Upon navigation:
 *  - provides a layout view of the users profile (their details, edit/delete account options)
 *  - pulls entrants data from the database by referencing the device id
 *  - we pull the actual entrant object from the EntrantActivity class in order to get its deviceID.
 *  - Entrants can delete their account, upon confirmation it will be removed from the database and user
 *  will be redirected to the login page
 *  - Entrant may also edit their personal information using the edit button in which they must also confirm
 *  any changes before it is saved to the database.
 *
 *  -- Currently Missing
 *  - Entrants should enter their city as a field when logging in or signing up.
 *  - Entrants do not yet have a getCity() method or city attribute to pull data from. It must be hardcoded as of now
 *  - this fragment should have a list of the entrants previous events. Entrants should have a list attribute of all their
 *  previous/active events
 */
// array adapter for users previous events.
// each item should be clickable. If user selects edit, and commits changes
// firebase console should be updated at the same time. same for delete. Except user should be thrown
// to the mainactivity(login screen)

public class ProfileFragment extends Fragment {

    private Button editBtn, deleteBtn;
    private Switch locationToggle;
    private FusedLocationProviderClient fusedLocationClient;
    private Entrant entrant;
    private FirebaseFirestore db;
    private TextView tvName, tvEmail, tvPhone, tvCity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        db = FirebaseFirestore.getInstance();

        locationToggle = view.findViewById(R.id.location_toggle);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        editBtn = view.findViewById(R.id.btn_edit);
        deleteBtn = view.findViewById(R.id.btn_delete_account);
        tvName = view.findViewById(R.id.tv_user_name);
        tvEmail = view.findViewById(R.id.tv_user_email);
        tvPhone = view.findViewById(R.id.tv_user_phone);
        tvCity = view.findViewById(R.id.tv_user_city);

        EntrantActivity parent = (EntrantActivity) requireActivity();
        String deviceId = parent.getDeviceID();

        locationToggle.setEnabled(false);

       db.collection("entrants").document(deviceId)
                       .get()
                       .addOnSuccessListener(document -> {
                                if(document.exists()) {
                                    entrant = document.toObject(Entrant.class);
                                    if(entrant != null) {
                                        tvName.setText(entrant.getName());
                                        tvEmail.setText(entrant.getEmail());
                                        tvPhone.setText(entrant.getPhone());
                                        tvCity.setText(entrant.getCity() != null ? entrant.getCity() : "Location not shared");

                                        locationToggle.setEnabled(true);
                                        locationToggle.setChecked(entrant.getLocationShared());

                                        locationToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                            if (isChecked) {
                                                if (hasLocationPermission()) {
                                                    enableLocationSharing();
                                                } else {
                                                    requestLocationPermission();
                                                    locationToggle.setChecked(false);
                                                }
                                            } else {
                                                disableLocationSharing();
                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "No Entrant found.", Toast.LENGTH_SHORT).show();
                                }
                               })
                                .addOnFailureListener(e ->
                                               Toast.makeText(requireContext(), "Failed to load entrant.", Toast.LENGTH_SHORT).show());

        editBtn.setOnClickListener(v -> showEditProfileDialog());
        deleteBtn.setOnClickListener(v -> confirmDeleteAccount());

        return view;
    }

    private void enableLocationSharing() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            GeoPoint locationPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                            String city = getCityFromGeoPoint(locationPoint);
                            entrant.setLocation(locationPoint);
                            entrant.setCity(city);
                            entrant.setLocationShared(true);
                            Firebase.saveEntrant(entrant);

                            tvCity.setText(city);
                        } else {
                            Toast.makeText(requireContext(), "Turn on GPS device settings", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void disableLocationSharing() {
        entrant.setLocationShared(false);
        entrant.setLocation(null);
        entrant.setCity(null);
        tvCity.setText("Location not shared");
        Firebase.saveEntrant(entrant);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }


    private void showEditProfileDialog() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialogue_edit_profile, null);

        EditText eName = dialogView.findViewById(R.id.et_first_name);
        EditText ePhone = dialogView.findViewById(R.id.et_phone);
        EditText eEmail = dialogView.findViewById(R.id.et_email);
        EditText eCity = dialogView.findViewById(R.id.et_city);

        eName.setText(entrant.getName());
        eEmail.setText(entrant.getEmail());
        ePhone.setText(entrant.getPhone());
        eCity.setText(entrant.getCity());// entrant.getCity

        new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("Edit Profile")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    entrant.setName(eName.getText().toString().trim());
                    entrant.setEmail(eEmail.getText().toString().trim());
                    // entrant.setphone
                    entrant.setCity(eCity.getText().toString().trim());

                    Firebase.saveEntrant(entrant);


                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private String getCityFromGeoPoint(GeoPoint geoPoint) {
        if (geoPoint == null) return "Unknown Location";

        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    geoPoint.getLatitude(),
                    geoPoint.getLongitude(),
                    1
            );

            if (addresses != null && !addresses.isEmpty()) {
                String city = addresses.get(0).getLocality();

                if (city == null) {
                    city = addresses.get(0).getSubAdminArea();
                }
                return city;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown City";
    }


    private void confirmDeleteAccount() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Delete", (dialog, which) -> {
            Firebase.deleteEntrant(entrant);
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

}
