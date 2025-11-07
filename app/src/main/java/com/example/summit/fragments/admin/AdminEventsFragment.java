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

/**
 * Fragment for managing events in the admin dashboard.
 * <p>
 * This fragment provides administrators with comprehensive event management capabilities including:
 * - Viewing all events in the system with real-time updates
 * - Searching events by ID or title
 * - Sorting events by multiple criteria (ID, title, date)
 * - Selecting single or multiple events for batch operations
 * - Deleting selected events with confirmation
 * <p>
 * The fragment maintains two lists: allEvents (complete dataset) and filteredEvents (after search/filter).
 * Event deletion removes both the event document and its associated QR code from Firestore.
 */
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
        return inflater.inflate(R.layout.fragment_admin_events, container, false);
    }

    /**
     * Called immediately after onCreateView() has returned. Initializes all UI components.
     * <p>
     * Sets up:
     * - RecyclerView with LinearLayoutManager and AdminEventAdapter
     * - Search bar with real-time text filtering
     * - Filter and sort spinners with array adapters
     * - Selection buttons (Select All, Clear, Delete)
     * - Firestore listener for real-time event updates
     *
     * @param view The View returned by onCreateView()
     * @param savedInstanceState Previous state data if the fragment is being re-created
     */
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
        adapter.setOnSelectionChangedListener(() -> updateSelectionUI());
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

    /**
     * Sets up all event listeners for UI components.
     * <p>
     * Configures listeners for:
     * - Search bar (TextWatcher for real-time filtering)
     * - Filter spinner (OnItemSelectedListener for filter changes)
     * - Sort spinner (OnItemSelectedListener for sort changes)
     * - Select All button (bulk selection)
     * - Clear Selection button (deselect all)
     * - Delete button (shows confirmation dialog)
     * <p>
     * All filter/sort changes trigger applyFiltersAndSort() to update the display.
     */
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

    /**
     * Loads all events from Firestore using a real-time listener.
     * <p>
     * Sets up a SnapshotListener on the "events" collection that automatically
     * updates the UI whenever events are added, modified, or deleted in the database.
     * Each event document is deserialized into an EventDescription object and wrapped
     * in an Event object with its Firestore document ID.
     * <p>
     * After loading, automatically applies current filters and sorting via applyFiltersAndSort().
     */
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

    /**
     * Applies current search, filter, and sort criteria to the event list.
     * <p>
     * Processing order:
     * 1. Search filtering - Matches search query against event ID and title (case-insensitive)
     * 2. Sorting - Applies selected sort option:
     *    - Event ID (alphabetical)
     *    - Title (A-Z, case-insensitive)
     *    - Date (Oldest First or Newest First)
     * 3. Updates adapter with filtered/sorted results
     * 4. Updates selection UI to reflect current state
     * <p>
     * Called automatically on search text changes, filter changes, sort changes, and after loading events.
     */
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

    /**
     * Updates the selection UI elements to reflect the current selection state.
     * <p>
     * Updates the selected count text view to display the number of currently
     * selected events (e.g., "3 selected").
     */
    private void updateSelectionUI() {
        int selectedCount = adapter.getSelectedCount();
        selectedCountText.setText(selectedCount + " selected");
    }

    /**
     * Shows a confirmation dialog before deleting selected events.
     * <p>
     * If no events are selected, displays a toast message. Otherwise, shows an
     * AlertDialog with:
     * - Title: "Confirm Deletion"
     * - Message: Number of events to be deleted and warning that action cannot be undone
     * - Positive button: "Delete" - Proceeds with deletion via deleteSelectedEvents()
     * - Negative button: "Cancel" - Dismisses dialog without action
     */
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

    /**
     * Deletes the selected events from Firestore.
     * <p>
     * Calls Firebase.deleteEvents() which performs the following:
     * - Deletes each event document from the "events" collection
     * - Deletes each associated QR code from the "qrcodes" collection
     * <p>
     * On success:
     * - Clears the adapter selection
     * - Updates the selection UI
     * - Shows success toast message
     * <p>
     * On failure:
     * - Shows error toast with failure message
     *
     * @param eventIds Set of event IDs to delete
     */
    private void deleteSelectedEvents(Set<String> eventIds) {
        com.example.summit.model.Firebase.deleteEvents(new ArrayList<>(eventIds), new com.example.summit.interfaces.DeleteCallback() {
            @Override
            public void onDeleteSuccess() {
                adapter.clearSelection();
                updateSelectionUI();
                Toast.makeText(getContext(), "Events deleted successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteFailure(String error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
