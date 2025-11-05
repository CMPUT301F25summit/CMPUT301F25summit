package com.example.summit.fragments.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.summit.R;
import com.example.summit.model.Event;
import com.example.summit.model.SignUp;
import com.example.summit.model.Entrant;
import com.example.summit.session.Session;

/**
 * Fragment for displaying event details.
 * Shows event information and allows entrants to join the waiting list.
 * Organizers can view entrants and run lottery (Task #3 - to be implemented).
 */
public class EventDetailsFragment extends Fragment {

    private Event event; // Will display event info later

    /**
     * Default constructor that sets the layout.
     */
    public EventDetailsFragment() {
        super(R.layout.fragment_event_details);
    }

    /**
     * Called after onCreateView returns.
     * Sets up the event details display and action buttons.
     * Receives event ID from navigation arguments (e.g., from QR scanner).
     *
     * @param view The View returned by onCreateView
     * @param savedInstanceState Bundle containing previously saved state
     */
    @Nullable
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Get the eventId from arguments (from QR scanner or navigation) ---
        String eventId = getArguments() != null ? getArguments().getString("eventId") : null;

        // Get current Entrant session
        Entrant currentEntrant = Session.getEntrant();

        if (currentEntrant == null) {
            Toast.makeText(getContext(), "No entrant session found", Toast.LENGTH_SHORT).show();
            return;
        }

        if (eventId == null) {
            Toast.makeText(getContext(), "Missing event id", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Load and display event details from Firebase using eventId
        // loadEventDetails(eventId);

        // --- Entrant join button logic ---
        Button joinBtn = view.findViewById(R.id.button_join);
        if (joinBtn != null) {
            joinBtn.setOnClickListener(v -> {
                SignUp signup = new SignUp();
                signup.joinEventFirestore(currentEntrant, eventId);
                Toast.makeText(getContext(), "Joined waiting list!", Toast.LENGTH_SHORT).show();
            });
        }

        // ========== TASK #3: ORGANIZER BUTTONS (COMMENTED OUT FOR NOW) ==========
        // TODO: Uncomment and implement when doing Task #3

        /*
        // --- Organizer buttons ---
        Button btnViewEntrants = view.findViewById(R.id.btn_view_entrants);
        Button btnRunLottery = view.findViewById(R.id.btn_run_lottery);

        if (btnViewEntrants != null && btnRunLottery != null) {

            // Check if current user is an Organizer
            boolean isOrganizer = Session.getOrganizer() != null;

            // Hide buttons for non-organizers
            View organizerBar = view.findViewById(R.id.organizer_actions);
            if (organizerBar != null) {
                organizerBar.setVisibility(isOrganizer ? View.VISIBLE : View.GONE);
            }

            // Only enable actions if user is organizer
            if (isOrganizer) {
                btnViewEntrants.setOnClickListener(v -> {
                    // TODO: Create EntrantsListActivity for Task #3
                    // Intent intent = new Intent(requireContext(), EntrantsListActivity.class);
                    // intent.putExtra("eventId", eventId);
                    // startActivity(intent);
                    Toast.makeText(getContext(), "View Entrants - Coming in Task #3", Toast.LENGTH_SHORT).show();
                });

                btnRunLottery.setOnClickListener(v -> {
                    // TODO: Create RunLotteryActivity for Task #3
                    // Intent intent = new Intent(requireContext(), RunLotteryActivity.class);
                    // intent.putExtra("eventId", eventId);
                    // startActivity(intent);
                    Toast.makeText(getContext(), "Run Lottery - Coming in Task #3", Toast.LENGTH_SHORT).show();
                });
            }
        }
        */
        // ========== END OF TASK #3 CODE ==========
    }

    /**
     * Loads event details from Firebase using the event ID.
     * TODO: Implement this method to fetch and display event information.
     *
     * @param eventId The unique identifier of the event to load
     */
    private void loadEventDetails(String eventId) {
        // TODO: Fetch event from Firebase
        // FirebaseFirestore.getInstance()
        //     .collection("events")
        //     .document(eventId)
        //     .get()
        //     .addOnSuccessListener(doc -> {
        //         // Display event details in UI
        //     });
    }
}