package com.example.summit.fragments.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.summit.R;
import com.example.summit.adapters.AdminUserAdapter;
import com.example.summit.interfaces.DeleteCallback;
import com.example.summit.interfaces.UserLoadCallback;
import com.example.summit.model.Firebase;
import com.example.summit.model.UserProfile;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fragment for managing user profiles in the admin dashboard.
 * <p>
 * This fragment provides administrators with comprehensive user management capabilities including:
 * - Viewing all users (Entrants, Organizers, Admins) with real-time updates
 * - Searching users by name
 * - Filtering users by city and role
 * - Selecting single or multiple users for batch operations
 * - Deleting selected users with confirmation
 * - Pagination support (20 users per page)
 * <p>
 * The fragment maintains two lists: allUsers (complete dataset) and filteredUsers (after search/filter).
 * User deletion removes users from their respective Firestore collections based on their role.
 */
public class AdminUserProfilesFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminUserAdapter adapter;
    private TextInputEditText searchEditText;
    private Spinner filterCitySpinner;
    private Spinner filterRoleSpinner;
    private Button selectAllButton;
    private Button clearSelectionButton;
    private Button deleteButton;
    private Button loadMoreButton;
    private TextView selectedCountText;
    private TextView paginationText;

    private List<UserProfile> allUsers = new ArrayList<>();
    private List<UserProfile> filteredUsers = new ArrayList<>();
    private Set<String> uniqueCities = new HashSet<>();

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     *
     * @param inflater The LayoutInflater object to inflate views
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState Previous state data if the fragment is being re-created
     * @return The root View for the fragment's UI
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_user_profiles, container, false);
    }

    /**
     * Sets up:
     * - RecyclerView with LinearLayoutManager and AdminUserAdapter
     * - Search bar with real-time text filtering
     * - City and role filter spinners
     * - Selection buttons (Select All, Clear, Delete)
     * - Load More button for pagination
     * - Real-time user loading from Firestore
     *
     * @param view The View returned by onCreateView()
     * @param savedInstanceState Previous state data if the fragment is being re-created
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        recyclerView = view.findViewById(R.id.usersRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
        filterCitySpinner = view.findViewById(R.id.filterCitySpinner);
        filterRoleSpinner = view.findViewById(R.id.filterRoleSpinner);
        selectAllButton = view.findViewById(R.id.selectAllButton);
        clearSelectionButton = view.findViewById(R.id.clearSelectionButton);
        deleteButton = view.findViewById(R.id.deleteButton);
        loadMoreButton = view.findViewById(R.id.loadMoreButton);
        selectedCountText = view.findViewById(R.id.selectedCountText);
        paginationText = view.findViewById(R.id.paginationText);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminUserAdapter(getContext());
        adapter.setOnSelectionChangedListener(this::updateSelectionUI);
        recyclerView.setAdapter(adapter);

        // Setup role filter spinner with static options from strings.xml
        ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.user_filter_role_options,
                android.R.layout.simple_spinner_item
        );
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterRoleSpinner.setAdapter(roleAdapter);

        // Setup listeners
        setupListeners();

        // Load users with real-time updates
        loadAllUsersRealtime();
    }

    /**
     * Sets up all event listeners for UI components.
     */
    private void setupListeners() {
        // Search listener
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // City filter spinner
        filterCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Role filter spinner
        filterRoleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Select All button
        selectAllButton.setOnClickListener(v -> {
            adapter.selectAll();
            updateSelectionUI();
        });

        // Clear Selection button
        clearSelectionButton.setOnClickListener(v -> {
            adapter.clearSelection();
            updateSelectionUI();
        });

        // Delete button
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());

        // Load More button
        loadMoreButton.setOnClickListener(v -> {
            adapter.loadMore();
            updatePaginationUI();
        });
    }

    /**
     * Loads all users from Firestore with real-time updates.
     * Sets up SnapshotListeners for all three user collections (entrants, organizers, admins).
     */
    private void loadAllUsersRealtime() {
        Firebase.loadAllUsersRealtime(new UserLoadCallback() {
            @Override
            public void onUsersLoaded(List<UserProfile> users) {
                allUsers.clear();
                allUsers.addAll(users);
                updateCityFilter();
                applyFilters();
            }

            @Override
            public void onLoadFailure(String error) {
                Toast.makeText(getContext(), "Error loading users: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Dynamically populates the city filter spinner with unique cities from loaded user data.
     */
    private void updateCityFilter() {
        uniqueCities.clear();
        uniqueCities.add("All Cities");

        // Extract unique cities from users (excluding "N/A")
        for (UserProfile user : allUsers) {
            String city = user.getCity();
            if (city != null && !city.equals("N/A") && !city.isEmpty()) {
                uniqueCities.add(city);
            }
        }

        // "N/A" option
        uniqueCities.add("N/A");

        // Create and set adapter
        List<String> cityList = new ArrayList<>(uniqueCities);
        Collections.sort(cityList, (a, b) -> {
            // Keep "All Cities" first
            if (a.equals("All Cities")) return -1;
            if (b.equals("All Cities")) return 1;
            // Keep "N/A" last
            if (a.equals("N/A")) return 1;
            if (b.equals("N/A")) return -1;
            return a.compareToIgnoreCase(b);
        });

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                cityList
        );
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterCitySpinner.setAdapter(cityAdapter);
    }

    /**
     * Applies search and filter criteria to the user list.
     * Filters by search query (name), city, and role, then sorts alphabetically by name.
     */
    private void applyFilters() {
        String searchQuery = searchEditText.getText() != null ?
                searchEditText.getText().toString().toLowerCase().trim() : "";
        String cityFilter = filterCitySpinner.getSelectedItem() != null ?
                filterCitySpinner.getSelectedItem().toString() : "All Cities";
        String roleFilter = filterRoleSpinner.getSelectedItem() != null ?
                filterRoleSpinner.getSelectedItem().toString() : "All Roles";

        filteredUsers.clear();

        for (UserProfile user : allUsers) {
            // Search filter - match name
            if (!searchQuery.isEmpty()) {
                String name = user.getName() != null ? user.getName().toLowerCase() : "";
                if (!name.contains(searchQuery)) {
                    continue;
                }
            }

            // City filter
            if (!"All Cities".equals(cityFilter)) {
                String userCity = user.getCity() != null ? user.getCity() : "N/A";
                if (!cityFilter.equals(userCity)) {
                    continue;
                }
            }

            // Role filter
            if (!"All Roles".equals(roleFilter)) {
                String userRole = user.getRole() != null ? user.getRole() : "";
                if (!roleFilter.equals(userRole)) {
                    continue;
                }
            }

            filteredUsers.add(user);
        }

        // Sort alphabetically by name
        Collections.sort(filteredUsers, (u1, u2) -> {
            String name1 = u1.getName() != null ? u1.getName() : "";
            String name2 = u2.getName() != null ? u2.getName() : "";
            return name1.compareToIgnoreCase(name2);
        });

        adapter.filterUsers(filteredUsers);
        updatePaginationUI();
    }

    /**
     * Shows a confirmation dialog before deleting selected users.
     * Displays count and list of user names to be deleted.
     */
    private void showDeleteConfirmationDialog() {
        List<UserProfile> selectedUsers = adapter.getSelectedUsers();
        int count = selectedUsers.size();

        if (count == 0) {
            Toast.makeText(getContext(), "No users selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build list of names for confirmation message
        StringBuilder names = new StringBuilder();
        for (int i = 0; i < selectedUsers.size(); i++) {
            UserProfile user = selectedUsers.get(i);
            names.append("- ").append(user.getName())
                    .append(" (").append(user.getRole()).append(")");
            if (i < selectedUsers.size() - 1) {
                names.append("\n");
            }
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete " + count +
                        " user(s)?\n\n" + names.toString() +
                        "\n\nThis action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteSelectedUsers(selectedUsers))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Deletes the selected users from Firestore.
     * Users are deleted from their respective collections based on their roles.
     *
     * @param users The list of UserProfiles to delete
     */
    private void deleteSelectedUsers(List<UserProfile> users) {
        Firebase.deleteUsers(users, new DeleteCallback() {
            @Override
            public void onDeleteSuccess() {
                adapter.clearSelection();
                updateSelectionUI();
                Toast.makeText(getContext(), "Users deleted successfully",
                        Toast.LENGTH_SHORT).show();
                // Real-time listener will automatically refresh the list
            }

            @Override
            public void onDeleteFailure(String error) {
                Toast.makeText(getContext(), "Error deleting users: " + error,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Updates the selection count text display.
     */
    private void updateSelectionUI() {
        int selectedCount = adapter.getSelectedCount();
        selectedCountText.setText(selectedCount + " selected");
    }

    /**
     * Updates the pagination information and Load More button visibility.
     */
    private void updatePaginationUI() {
        paginationText.setText(adapter.getPaginationText());
        loadMoreButton.setVisibility(adapter.hasMore() ? View.VISIBLE : View.GONE);
    }
}
