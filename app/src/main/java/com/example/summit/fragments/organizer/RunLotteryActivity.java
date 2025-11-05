package com.example.summit.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.summit.R;
import com.example.summit.adapters.EntrantsAdapter;
import com.example.summit.model.Entrant;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class RunLotteryActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String eventId;

    private EditText editTextNumberOfWinners;
    private Button buttonRunLottery;
    private TextView textViewWaitingListCount;
    private TextView textViewWinnersLabel;
    private ListView listViewWinners;
    private ProgressBar progressBar;

    private List<Entrant> waitingList;
    private List<Entrant> selectedWinners;
    private EntrantsAdapter winnersAdapter;

    /**
     * Called when the activity is created.
     * Initializes views, gets event ID from intent, and loads waiting list.

     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_lottery);

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
            getSupportActionBar().setTitle("Run Lottery");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize lists
        waitingList = new ArrayList<>();
        selectedWinners = new ArrayList<>();

        // Load waiting list
        loadWaitingList();

        // Set up button click listener
        buttonRunLottery.setOnClickListener(v -> runLottery());
    }

    /**
     * Initializes all views and sets up the winners list adapter.
     */
    private void initializeViews() {
        editTextNumberOfWinners = findViewById(R.id.edit_text_number_of_winners);
        buttonRunLottery = findViewById(R.id.button_run_lottery);
        textViewWaitingListCount = findViewById(R.id.text_view_waiting_list_count);
        textViewWinnersLabel = findViewById(R.id.text_view_winners_label);
        listViewWinners = findViewById(R.id.list_view_winners);
        progressBar = findViewById(R.id.progress_bar);

        // Initially hide winners section
        textViewWinnersLabel.setVisibility(View.GONE);
        listViewWinners.setVisibility(View.GONE);

        // Set up winners adapter
        winnersAdapter = new EntrantsAdapter(this, selectedWinners);
        listViewWinners.setAdapter(winnersAdapter);
    }

    /**
     * Loads the waiting list for this event from Firebase.
     * Only loads entrants who are in "waiting" status (not already selected).
     * Updates the UI with the waiting list count.
     */
    private void loadWaitingList() {
        progressBar.setVisibility(View.VISIBLE);
        buttonRunLottery.setEnabled(false);

        // Query Firebase for all signups with "waiting" status
        db.collection("signups")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "waiting")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    waitingList.clear();

                    if (queryDocumentSnapshots.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        textViewWaitingListCount.setText("No entrants in waiting list");
                        Toast.makeText(this,
                                "No entrants in waiting list. Cannot run lottery.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Load details for each entrant
                    int totalEntrants = queryDocumentSnapshots.size();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String entrantId = document.getString("entrantId");

                        if (entrantId != null) {
                            loadEntrantDetails(entrantId, totalEntrants);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this,
                            "Error loading waiting list: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    /**
     * Loads detailed information for a specific entrant.
     * Called for each entrant ID in the waiting list.
     */
    private void loadEntrantDetails(String entrantId, int totalExpected) {
        db.collection("entrants")
                .document(entrantId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Entrant entrant = documentSnapshot.toObject(Entrant.class);

                        if (entrant != null) {
                            waitingList.add(entrant);
                        }
                    }

                    // Check if all entrants loaded
                    if (waitingList.size() == totalExpected) {
                        updateWaitingListUI();
                    }
                });
    }

    /**
     * Updates the UI after loading the waiting list.
     * Shows the count and enables the lottery button if there are entrants.
     */
    private void updateWaitingListUI() {
        progressBar.setVisibility(View.GONE);

        int count = waitingList.size();
        textViewWaitingListCount.setText("Waiting List: " + count + " entrants");

        if (count > 0) {
            buttonRunLottery.setEnabled(true);

            // Set default number of winners (e.g., 10% of waiting list, minimum 1)
            int defaultWinners = Math.max(1, count / 10);
            editTextNumberOfWinners.setHint("Max: " + count);
        } else {
            buttonRunLottery.setEnabled(false);
        }
    }

    /**
     * Runs the lottery to select random winners from the waiting list.
     * Validates the number of winners, performs random selection,
     * and displays the results.
     */
    private void runLottery() {
        // Get number of winners from input
        String winnersInput = editTextNumberOfWinners.getText().toString().trim();

        if (winnersInput.isEmpty()) {
            Toast.makeText(this, "Please enter number of winners", Toast.LENGTH_SHORT).show();
            return;
        }

        int numberOfWinners;
        try {
            numberOfWinners = Integer.parseInt(winnersInput);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate number of winners
        if (numberOfWinners <= 0) {
            Toast.makeText(this, "Number of winners must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        if (numberOfWinners > waitingList.size()) {
            Toast.makeText(this,
                    "Cannot select more winners than waiting list size (" + waitingList.size() + ")",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Show confirmation dialog
        showConfirmationDialog(numberOfWinners);
    }

    /**
     * Shows a confirmation dialog before running the lottery.
     * Prevents accidental lottery runs.
     */
    private void showConfirmationDialog(int numberOfWinners) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Lottery")
                .setMessage("Are you sure you want to select " + numberOfWinners +
                        " random winners from " + waitingList.size() + " entrants?")
                .setPositiveButton("Run Lottery", (dialog, which) -> performLottery(numberOfWinners))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Performs the actual lottery selection.
     * Uses random selection to pick winners from the waiting list.
     * Updates Firebase with the selected winners' status.
     */
    private void performLottery(int numberOfWinners) {
        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        buttonRunLottery.setEnabled(false);

        // Clear previous winners
        selectedWinners.clear();

        // Create a copy of waiting list for random selection
        List<Entrant> shuffledList = new ArrayList<>(waitingList);

        // Shuffle the list randomly
        Collections.shuffle(shuffledList, new Random());

        // Select the first N entrants as winners
        for (int i = 0; i < numberOfWinners; i++) {
            selectedWinners.add(shuffledList.get(i));
        }

        // Update winners in Firebase
        updateWinnersInFirebase();

        // Display winners
        displayWinners();

        Toast.makeText(this,
                "Lottery complete! Selected " + numberOfWinners + " winners.",
                Toast.LENGTH_LONG).show();
    }

    /**
     * Updates the status of selected winners in Firebase.
     * Changes their signup status from "waiting" to "selected".
     * This allows for tracking and notification purposes.
     */
    private void updateWinnersInFirebase() {
        for (Entrant winner : selectedWinners) {
            // Update the signup document for this winner
            db.collection("signups")
                    .whereEqualTo("eventId", eventId)
                    .whereEqualTo("entrantId", winner.getDeviceId()) // Assuming getDeviceId() returns entrant ID
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().update("status", "selected")
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this,
                                                    "Error updating winner status: " + e.getMessage(),
                                                    Toast.LENGTH_SHORT).show()
                                    );
                        }
                    });
        }
    }

    /**
     * Displays the selected winners in the UI.
     * Shows the winners list and updates the adapter.
     */
    private void displayWinners() {
        progressBar.setVisibility(View.GONE);

        // Show winners section
        textViewWinnersLabel.setVisibility(View.VISIBLE);
        textViewWinnersLabel.setText("Selected Winners (" + selectedWinners.size() + "):");
        listViewWinners.setVisibility(View.VISIBLE);

        // Update adapter
        winnersAdapter.notifyDataSetChanged();

        // Disable lottery button to prevent running it again
        buttonRunLottery.setEnabled(false);
        buttonRunLottery.setText("Lottery Complete");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}