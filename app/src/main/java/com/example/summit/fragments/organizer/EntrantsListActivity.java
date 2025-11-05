package com.example.summit.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.summit.R;
import com.example.summit.adapters.EntrantsAdapter;
import com.example.summit.model.Entrant;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;



public class EntrantsListActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String eventId;

    private ListView listViewEntrants;
    private TextView textViewCount;
    private TextView textViewEmpty;
    private ProgressBar progressBar;

    private EntrantsAdapter adapter;
    private List<Entrant> entrantsList;

    /**
     * Called when the activity is created.
     * Initializes views, gets event ID from intent, and loads entrants list.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrants_list);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Get event ID from intent
        eventId = getIntent().getStringExtra("eventId");

        if (eventId == null) {
            Toast.makeText(this, "Error: No event ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        initializeViews();

        // Set up toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Event Entrants");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Load entrants list
        loadEntrants();
    }

    /**
     * Initializes all views and sets up the list adapter.
     */
    private void initializeViews() {
        listViewEntrants = findViewById(R.id.list_view_entrants);
        textViewCount = findViewById(R.id.text_view_count);
        textViewEmpty = findViewById(R.id.text_view_empty);
        progressBar = findViewById(R.id.progress_bar);

        // Initialize entrants list and adapter
        entrantsList = new ArrayList<>();
        adapter = new EntrantsAdapter(this, entrantsList);
        listViewEntrants.setAdapter(adapter);
    }

    /**
     * Loads the list of entrants for this event from Firebase.
     * Queries the signups collection to find all users who joined this event.
     * Updates the UI with the loaded data.
     */
    private void loadEntrants() {
        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);
        textViewEmpty.setVisibility(View.GONE);

        // Query Firebase for all signups for this event
        db.collection("signups")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    entrantsList.clear();

                    if (queryDocumentSnapshots.isEmpty()) {
                        // No entrants found
                        showEmptyState();
                        return;
                    }

                    // Process each signup document
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String entrantId = document.getString("entrantId");

                        if (entrantId != null) {
                            // Fetch entrant details
                            loadEntrantDetails(entrantId);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this,
                            "Error loading entrants: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    showEmptyState();
                });
    }

    /**
     * Loads detailed information for a specific entrant from Firebase.
     */
    private void loadEntrantDetails(String entrantId) {
        db.collection("entrants")
                .document(entrantId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Convert document to Entrant object
                        Entrant entrant = documentSnapshot.toObject(Entrant.class);

                        if (entrant != null) {
                            entrantsList.add(entrant);
                            updateUI();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Log error but continue loading other entrants
                    Toast.makeText(this,
                            "Error loading entrant: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Updates the UI after loading entrants.
     * Hides loading indicator, updates the list, and shows entrant count.
     */
    private void updateUI() {
        progressBar.setVisibility(View.GONE);

        if (entrantsList.isEmpty()) {
            showEmptyState();
        } else {
            textViewEmpty.setVisibility(View.GONE);
            listViewEntrants.setVisibility(View.VISIBLE);

            // Update adapter
            adapter.notifyDataSetChanged();

            // Update count
            updateCount();
        }
    }

    /**
     * Updates the entrant count text view.
     * Displays the total number of entrants in a user-friendly format.
     */
    private void updateCount() {
        int count = entrantsList.size();
        String countText = count == 1 ? "1 Entrant" : count + " Entrants";
        textViewCount.setText(countText);
        textViewCount.setVisibility(View.VISIBLE);
    }

    /**
     * Shows the empty state when no entrants are found.
     * Hides the list and shows an informative message.
     */
    private void showEmptyState() {
        progressBar.setVisibility(View.GONE);
        listViewEntrants.setVisibility(View.GONE);
        textViewCount.setVisibility(View.GONE);
        textViewEmpty.setVisibility(View.VISIBLE);
        textViewEmpty.setText("No entrants have joined this event yet.");
    }

    /**
     * Handles the back button press in the action bar.
     * Finishes the activity and returns to the previous screen.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}