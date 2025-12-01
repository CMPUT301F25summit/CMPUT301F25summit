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
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.summit.EntrantActivity;
import com.example.summit.MainActivity;
import com.example.summit.R;
import com.example.summit.adapters.EntrantEventAdapter;
import com.example.summit.model.Entrant;
import com.example.summit.model.Event;
import com.example.summit.model.EventDescription;
import com.example.summit.model.Firebase;
import com.example.summit.utils.ProfileEventFilterUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A {@link Fragment} that displays and manages an Entrant's profile.
 * <p>
 * <b>Key Features:</b>
 * <ul>
 * <li>Displays personal details (Name, Email, Phone, City).</li>
 * <li>Allows editing of profile details via a dialog.</li>
 * <li>Toggle for enabling/disabling location sharing (Geo-location).</li>
 * <li>Displays a history of events the entrant is involved in (Waitlist, Selected, Declined).</li>
 * <li>Provides Search and Filter functionality for the event history list.</li>
 * <li>Allows the user to permanently delete their account.</li>
 * </ul>
 */

public class ProfileFragment extends Fragment {

    private Button editBtn, deleteBtn;
    private ImageButton searchBtn;
    private Switch locationToggle;
    private FusedLocationProviderClient fusedLocationClient;
    private Entrant entrant;
    private FirebaseFirestore db;
    private TextView tvName, tvEmail, tvPhone, tvCity;

    private RecyclerView recyclerEventHistory;
    private EntrantEventAdapter eventAdapter;
    private EditText searchInput;
    private Button filterBtn;

    private List<Event> allUserEvents = new ArrayList<>();
    private List<String> allUserStatuses = new ArrayList<>();

    /**
     * Inflates the fragment layout, initializes Firebase, and triggers the setup of UI and data loading.
     *
     * @param inflater           The LayoutInflater object to inflate views.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState Previous saved state (if any).
     * @return The root view of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        db = FirebaseFirestore.getInstance();

        initializeViews(view);
        setupRecyclerView();
        loadEntrantData();
        setupClickListeners();
        return view;
    }

    /**
     * Binds all XML views to their respective Java variables.
     * Also initializes the {@link FusedLocationProviderClient} for location services.
     *
     * @param view The root view of the fragment.
     */
    private void initializeViews(View view) {
        locationToggle = view.findViewById(R.id.location_toggle);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        editBtn = view.findViewById(R.id.btn_edit);
        deleteBtn = view.findViewById(R.id.btn_delete_account);
        tvName = view.findViewById(R.id.tv_user_name);
        tvEmail = view.findViewById(R.id.tv_user_email);
        tvPhone = view.findViewById(R.id.tv_user_phone);
        tvCity = view.findViewById(R.id.tv_user_city);
        recyclerEventHistory = view.findViewById(R.id.recycler_event_history);
        searchInput = view.findViewById(R.id.et_search_events);
        searchBtn = view.findViewById(R.id.profile_btn_search);
        filterBtn = view.findViewById(R.id.btn_profile_filter);

        locationToggle.setEnabled(false);
    }

    /**
     * Configures the {@link RecyclerView} for displaying the user's event history.
     * Sets up the {@link EntrantEventAdapter} and defines the click listener to navigate to Event Details.
     */
    private void setupRecyclerView() {
        recyclerEventHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        eventAdapter = new EntrantEventAdapter(requireContext(), event -> {
            Bundle args = new Bundle();
            args.putString("eventId", event.getId());
            NavHostFragment.findNavController(ProfileFragment.this)
                    .navigate(R.id.EventDetailsEntrantFragment, args);
        });
        recyclerEventHistory.setAdapter(eventAdapter);
    }

    /**
     * Fetches the current user's profile data from Firestore based on their Device ID.
     * Upon success, it triggers the UI update and loads their event history.
     */
    private void loadEntrantData() {
        EntrantActivity parent = (EntrantActivity) requireActivity();
        String deviceId = parent.getDeviceID();

        db.collection("entrants").document(deviceId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        entrant = document.toObject(Entrant.class);
                        if (entrant != null) {
                            updateUI();
                            setupLocationToggle();
                            loadUserEvents(deviceId);
                        }
                    } else {
                        Toast.makeText(requireContext(), "No Entrant found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed to load entrant.", Toast.LENGTH_SHORT).show());
    }

    /**
     * Updates the TextViews on the screen with the loaded {@link Entrant} data.
     */
    private void updateUI() {
        tvName.setText(entrant.getName());
        tvEmail.setText(entrant.getEmail());
        tvPhone.setText(entrant.getPhone());
        tvCity.setText(entrant.getCity() != null ? entrant.getCity() : "Location not shared");
    }

    /**
     * Configures the Location Toggle switch.
     * Sets the initial state based on the user's profile and adds a listener to handle
     * enabling/disabling location permissions and data saving.
     */
    private void setupLocationToggle() {
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

    /**
     * Sets up click listeners for the main action buttons:
     * <ul>
     * <li><b>Edit:</b> Opens the profile edit dialog.</li>
     * <li><b>Delete:</b> Opens the account deletion confirmation.</li>
     * <li><b>Search:</b> Filters the event list by keyword using {@link ProfileEventFilterUtil}.</li>
     * <li><b>Filter:</b> Opens the filter dialog to sort by status.</li>
     * </ul>
     */
    private void setupClickListeners() {
        editBtn.setOnClickListener(v -> showEditProfileDialog());
        deleteBtn.setOnClickListener(v -> confirmDeleteAccount());

        // Search button - use utility
        searchBtn.setOnClickListener(v -> {
            String keyword = searchInput.getText().toString().trim();
            ProfileEventFilterUtil.FilterResult result =
                    ProfileEventFilterUtil.searchByKeyword(allUserEvents, allUserStatuses, keyword);
            eventAdapter.updateEvents(result.events, result.statuses);
        });

        // Filter button - use utility
        filterBtn.setOnClickListener(v -> {
            ProfileEventFilterUtil.showFilterDialog(
                    requireContext(),
                    allUserEvents,
                    allUserStatuses,
                    new ProfileEventFilterUtil.ProfileFilterCallback() {
                        @Override
                        public void onFilterApplied(List<Event> filteredEvents, List<String> filteredStatuses) {
                            eventAdapter.updateEvents(filteredEvents, filteredStatuses);
                        }

                        @Override
                        public void onFilterReset(List<Event> allEvents, List<String> allStatuses) {
                            eventAdapter.updateEvents(allEvents, allStatuses);
                        }
                    }
            );
        });
    }

    /**
     * Queries Firestore for all events and filters them locally to find ones associated with this user.
     * <p>
     * It checks if the user's Device ID is present in the 'accepted', 'waiting', or 'declined' lists
     * of any event.
     *
     * @param deviceId The unique Device ID of the current user.
     */
    private void loadUserEvents(String deviceId) {
        db.collection("events").get()
                .addOnSuccessListener(querySnapshot -> {
                    allUserEvents.clear();
                    allUserStatuses.clear();

                    querySnapshot.forEach(doc -> {
                        Event event = new Event();
                        event.setId(doc.getId());

                        EventDescription d = new EventDescription();
                        d.setTitle(doc.getString("title"));
                        d.setDescription(doc.getString("description"));
                        d.setLocation(doc.getString("location"));
                        d.setCapacity(doc.getLong("capacity"));
                        d.setEventStart(doc.getString("eventStart"));
                        d.setEventEnd(doc.getString("eventEnd"));
                        d.setPosterUrl(doc.getString("posterBase64"));
                        event.setDescription(d);

                        List<String> acceptedList = (List<String>) doc.get("acceptedList");
                        List<String> waitingList = (List<String>) doc.get("waitingList");
                        List<String> declinedList = (List<String>) doc.get("declinedList");

                        if (acceptedList != null && acceptedList.contains(deviceId)) {
                            allUserEvents.add(event);
                            allUserStatuses.add("Selected");
                        } else if (waitingList != null && waitingList.contains(deviceId)) {
                            allUserEvents.add(event);
                            allUserStatuses.add("Waitlist");
                        } else if (declinedList != null && declinedList.contains(deviceId)) {
                            allUserEvents.add(event);
                            allUserStatuses.add("Declined");
                        }
                    });

                    eventAdapter.updateEvents(allUserEvents, allUserStatuses);

                    if (allUserEvents.isEmpty()) {
                        Toast.makeText(requireContext(), "No events found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to load events: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Checks for location permissions and, if granted, retrieves the device's last known location.
     * It then reverse-geocodes the coordinates to a City name and saves the data to Firestore.
     */
    // Location methods remain the same
    private void enableLocationSharing() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
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

    /**
     * Disables location sharing by setting the entrant's location and city fields to null in Firestore.
     */
    private void disableLocationSharing() {
        entrant.setLocationShared(false);
        entrant.setLocation(null);
        entrant.setCity(null);
        tvCity.setText("Location not shared");
        Firebase.saveEntrant(entrant);
    }

    /**
     * Helper method to check if the app currently has ACCESS_FINE_LOCATION permission.
     * @return true if permission is granted, false otherwise.
     */
    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests the ACCESS_FINE_LOCATION permission from the system.
     */
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    /**
     * Displays a dialog containing EditText fields for the user to update their Name, Email, Phone, and City.
     * Upon confirmation, the data is saved to Firestore and the UI is refreshed.
     */
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
        eCity.setText(entrant.getCity());

        new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("Edit Profile")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    entrant.setName(eName.getText().toString().trim());
                    entrant.setEmail(eEmail.getText().toString().trim());
                    entrant.setPhone(ePhone.getText().toString().trim());
                    entrant.setCity(eCity.getText().toString().trim());

                    Firebase.saveEntrant(entrant);
                    updateUI();

                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    /**
     * Performs reverse geocoding to determine a City name from latitude/longitude coordinates.
     *
     * @param geoPoint The geographic coordinates.
     * @return A String representing the City (or SubAdminArea), or "Unknown City" if lookup fails.
     */
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

    /**
     * Displays a confirmation alert dialog.
     * If confirmed, the user's entrant document is deleted from Firestore and they are redirected to the Login screen.
     */
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

    /**
     * Called when the fragment resumes. Refreshes the user's event list to ensure
     * status changes (e.g., accepting an invite) are reflected immediately.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (entrant != null) {
            loadUserEvents(entrant.getDeviceId());
        }
    }
}

