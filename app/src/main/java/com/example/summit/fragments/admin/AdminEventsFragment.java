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
import com.example.summit.adapters.AdminEventAdapter;
import com.example.summit.model.Event;
import com.example.summit.model.EventDescription;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class AdminEventsFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private AdminEventAdapter adapter;
    private TextInputEditText searchEditText;
    private Spinner filterSpinner;
    private Spinner sortSpinner;
    private Button selectAllButton;
    private Button clearSelectionButton;
    private Button deleteButton;
    private TextView selectedCountText;

    private List<Event> allEvents = new ArrayList<>();
    private List<Event> filteredEvents = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        recyclerView = view.findViewById(R.id.eventsRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
        filterSpinner = view.findViewById(R.id.filterSpinner);
        sortSpinner = view.findViewById(R.id.sortSpinner);
        selectAllButton = view.findViewById(R.id.selectAllButton);
        clearSelectionButton = view.findViewById(R.id.clearSelectionButton);
        deleteButton = view.findViewById(R.id.deleteButton);
        selectedCountText = view.findViewById(R.id.selectedCountText);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminEventAdapter(getContext());
        recyclerView.setAdapter(adapter);

        // Setup filter spinner
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.event_filter_options,
                android.R.layout.simple_spinner_item
        );
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(filterAdapter);

        // Setup sort spinner
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.event_sort_options,
                android.R.layout.simple_spinner_item
        );
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        // Setup listeners
        setupListeners();

        // Load events
        loadAllEvents();
    }

    private void setupListeners() {
        // Search listener
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFiltersAndSort();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Filter spinner listener
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFiltersAndSort();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Sort spinner listener
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFiltersAndSort();
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
    }

    private void loadAllEvents() {
        db.collection("events")
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) {
                        Toast.makeText(getContext(), "Error loading events", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    allEvents.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        EventDescription desc = doc.toObject(EventDescription.class);
                        if (desc != null) {
                            Event event = new Event(desc);
                            event.setId(doc.getId());
                            allEvents.add(event);
                        }
                    }
                    applyFiltersAndSort();
                });
    }

    private void applyFiltersAndSort() {
        String searchQuery = searchEditText.getText() != null ?
                searchEditText.getText().toString().toLowerCase().trim() : "";

        // Filter events
        filteredEvents = new ArrayList<>();
        for (Event event : allEvents) {
            EventDescription desc = event.getDescription();
            String eventId = event.getId() != null ? event.getId().toLowerCase() : "";
            String title = desc.getTitle() != null ? desc.getTitle().toLowerCase() : "";

            // Search filter
            if (!searchQuery.isEmpty()) {
                if (!eventId.contains(searchQuery) && !title.contains(searchQuery)) {
                    continue;
                }
            }

            filteredEvents.add(event);
        }

        // Sort events
        String sortOption = sortSpinner.getSelectedItem() != null ?
                sortSpinner.getSelectedItem().toString() : "Event ID";

        switch (sortOption) {
            case "Event ID":
                Collections.sort(filteredEvents, (e1, e2) -> {
                    String id1 = e1.getId() != null ? e1.getId() : "";
                    String id2 = e2.getId() != null ? e2.getId() : "";
                    return id1.compareTo(id2);
                });
                break;
            case "Title (A-Z)":
                Collections.sort(filteredEvents, (e1, e2) -> {
                    String t1 = e1.getDescription().getTitle() != null ? e1.getDescription().getTitle() : "";
                    String t2 = e2.getDescription().getTitle() != null ? e2.getDescription().getTitle() : "";
                    return t1.compareToIgnoreCase(t2);
                });
                break;
            case "Date (Oldest First)":
                Collections.sort(filteredEvents, (e1, e2) -> {
                    String d1 = e1.getDescription().getStartDate() != null ? e1.getDescription().getStartDate() : "";
                    String d2 = e2.getDescription().getStartDate() != null ? e2.getDescription().getStartDate() : "";
                    return d1.compareTo(d2);
                });
                break;
            case "Date (Newest First)":
                Collections.sort(filteredEvents, (e1, e2) -> {
                    String d1 = e1.getDescription().getStartDate() != null ? e1.getDescription().getStartDate() : "";
                    String d2 = e2.getDescription().getStartDate() != null ? e2.getDescription().getStartDate() : "";
                    return d2.compareTo(d1);
                });
                break;
        }

        adapter.filterEvents(filteredEvents);
        updateSelectionUI();
    }

    private void updateSelectionUI() {
        int selectedCount = adapter.getSelectedCount();
        selectedCountText.setText(selectedCount + " selected");
        deleteButton.setEnabled(selectedCount > 0);
    }

    private void showDeleteConfirmationDialog() {
        Set<String> selectedIds = adapter.getSelectedEventIds();
        int count = selectedIds.size();

        if (count == 0) {
            Toast.makeText(getContext(), "No events selected", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete " + count +
                        " event(s)? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteSelectedEvents(selectedIds))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteSelectedEvents(Set<String> eventIds) {
        // TODO: Implement actual deletion when Firebase.deleteEvents() is ready
        Toast.makeText(getContext(),
                "Delete functionality will be implemented when Firebase deletion methods are ready",
                Toast.LENGTH_LONG).show();

        // Placeholder for future implementation:
        // Firebase.deleteEvents(new ArrayList<>(eventIds), new DeleteCallback() {
        //     @Override
        //     public void onDeleteSuccess() {
        //         adapter.clearSelection();
        //         Toast.makeText(getContext(), "Events deleted successfully", Toast.LENGTH_SHORT).show();
        //     }
        //
        //     @Override
        //     public void onDeleteFailure(String error) {
        //         Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
        //     }
        // });
    }
}
