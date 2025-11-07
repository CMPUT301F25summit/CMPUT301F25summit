package com.example.summit.fragments.organizer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.summit.R;
import com.example.summit.model.Entrant;
import com.example.summit.model.LotterySystem;
import com.example.summit.model.WaitingList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link Fragment} for an organizer to view the detailed dashboard for a specific event.
 *
 * This screen displays core event info, entrant list counts (waiting, invited, accepted),
 * and provides navigation to management tasks (manage entrants, edit event, view QR)
 * and a key action (run lottery).
 */
public class EventDetailsOrganizerFragment extends Fragment {

    private TextView titleText, descText, regDatesText, capacityText,
            waitingCountText, invitedCountText, acceptedCountText;
    private ImageView posterImage;
    private Button manageEntrantsBtn, runLotteryBtn, editEventBtn, btnViewQr;

    private String eventId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public EventDetailsOrganizerFragment() {
        super(R.layout.fragment_event_details_organizer);
    }

    /**
     * Initializes the view, sets up the back button, retrieves the event ID,
     * and calls helper methods to load data and configure buttons.
     * Hides the parent activity's FAB.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            // Navigate back to Manage Events
            NavHostFragment.findNavController(EventDetailsOrganizerFragment.this)
                    .navigate(R.id.action_eventDetailsOrganizer_to_manageEvents);
        });

        FloatingActionButton fab = getActivity().findViewById(R.id.fab_add_event);
        if (fab != null) fab.setVisibility(View.GONE);


        eventId = getArguments() != null ? getArguments().getString("eventId") : null;
        if (eventId == null) {
            Toast.makeText(getContext(), "Error: Missing event ID", Toast.LENGTH_SHORT).show();
            return;
        }

        initViews(view);
        loadEventData();
        setupButtons();
    }

    /**
     * Binds all the XML views (TextViews, Buttons, etc.) to their class fields.
     * @param view The fragment's root view.
     */
    private void initViews(View view) {
        posterImage = view.findViewById(R.id.image_event_poster);
        titleText = view.findViewById(R.id.text_event_title);
        descText = view.findViewById(R.id.text_event_description);
        regDatesText = view.findViewById(R.id.text_reg_dates);
        capacityText = view.findViewById(R.id.text_capacity);

        waitingCountText = view.findViewById(R.id.text_waiting_count);
        invitedCountText = view.findViewById(R.id.text_invited_count);
        acceptedCountText = view.findViewById(R.id.text_accepted_count);

        manageEntrantsBtn = view.findViewById(R.id.button_manage_entrants);
        runLotteryBtn = view.findViewById(R.id.button_run_lottery);
        editEventBtn = view.findViewById(R.id.button_edit_event);
        btnViewQr = view.findViewById(R.id.button_view_qr);
    }

    /**
     * Fetches the event document from Firestore using {@code eventId} and
     * calls {@link #updateUIFromFirestore(DocumentSnapshot)} on success.
     */
    private void loadEventData() {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(this::updateUIFromFirestore)
                .addOnFailureListener(error ->
                        Toast.makeText(getContext(), "Load failed: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    /**
     * Populates the UI fields (TextViews, ImageView) with data from the
     * fetched Firestore {@link DocumentSnapshot}.
     *
     * @param doc The {@link DocumentSnapshot} containing the event data.
     */
    private void updateUIFromFirestore(DocumentSnapshot doc) {
        if (!doc.exists()) return;

        String title = doc.getString("title");
        String desc = doc.getString("description");
        String regStart = doc.getString("registrationStart");
        String regEnd = doc.getString("registrationEnd");
        Long capacity = doc.getLong("capacity");
        String poster = doc.getString("posterUrl");

        titleText.setText(title);
        descText.setText(desc);
        regDatesText.setText("Registration: " + regStart + " - " + regEnd);
        capacityText.setText("Capacity: " + capacity);

        long waiting = getCount(doc, "waitingList");
        long invited = getCount(doc, "invited");
        long accepted = getCount(doc, "accepted");

        waitingCountText.setText("Waiting: " + waiting);
        invitedCountText.setText("Invited: " + invited);
        acceptedCountText.setText("Accepted: " + accepted);

        Glide.with(this)
                .load(poster)
                .placeholder(R.drawable.placeholder_event)
                .into(posterImage);
    }

    /**
     * Helper to safely get the size of a list field from a {@link DocumentSnapshot}.
     *
     * @param doc The Firestore document.
     * @param field The name of the array/list field.
     * @return The size of the list, or 0 if the field is null or missing.
     */
    private long getCount(DocumentSnapshot doc, String field) {
        return doc.get(field) != null ?
                ((java.util.List<?>) doc.get(field)).size() : 0;
    }

    /**
     * Configures click listeners for all primary action buttons
     * (Manage Entrants, Run Lottery, Edit, View QR).
     */
    private void setupButtons() {

        manageEntrantsBtn.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("eventId", eventId);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_eventDetailsOrganizer_to_manageEntrants, args);
        });


        runLotteryBtn.setOnClickListener(v -> runLottery());



        editEventBtn.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("eventId", eventId);
            Toast.makeText(getContext(), "TODO: Edit Event Screen Next",
                    Toast.LENGTH_SHORT).show();
        });

        btnViewQr.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("eventId", eventId);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_eventDetailsOrganizer_to_eventCreated, args);
        });

    }

    /**
     * Executes the lottery logic.
     *
     * Fetches the event, calculates remaining capacity, and samples entrants
     * from the {@code waitingList}. It then updates Firestore, moving selected
     * entrants from {@code waitingList} to {@code selectedList}, and triggers
     * {@link #sendSelectionNotifications(List, String)}.
     */
    private void runLottery() {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    // Get event fields
                    Long capacityLong = doc.getLong("capacity");
                    List<String> waitingList = (List<String>) doc.get("waitingList");
                    List<String> selectedList = (List<String>) doc.get("selectedList");

                    if (waitingList == null || waitingList.isEmpty()) {
                        Toast.makeText(getContext(), "No entrants in waiting list", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int capacity = capacityLong != null ? capacityLong.intValue() : 0;
                    int remaining = capacity - (selectedList != null ? selectedList.size() : 0);

                    if (remaining <= 0) {
                        Toast.makeText(getContext(), "Event is already full!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    WaitingList wl = new WaitingList();
                    for (String entrantId : waitingList) {
                        wl.addEntrant(new Entrant(entrantId));
                    }

                    LotterySystem lottery = new LotterySystem(capacity);
                    List<Entrant> invited = lottery.sampleEntrants(wl, remaining);

                    List<String> invitedIds = new ArrayList<>();
                    for (Entrant e : invited) {
                        invitedIds.add(e.getDeviceId());
                    }

                    db.collection("events").document(eventId)
                            .update(
                                    "selectedList", FieldValue.arrayUnion(invitedIds.toArray()),
                                    "waitingList", FieldValue.arrayRemove(invitedIds.toArray())
                            )
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(),
                                        "Lottery run successfully! " + invitedIds.size() + " entrants selected.",
                                        Toast.LENGTH_SHORT).show();

                                sendSelectionNotifications(invitedIds, doc.getString("title"));
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "Error updating Firestore: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show()
                            );
                });
    }

    /**
     * Creates and saves a new notification document in the "notifications"
     * collection for each newly invited entrant.
     *
     * @param invitedIds List of entrant IDs who were selected.
     * @param eventTitle The title of the event, for the notification message.
     */
    private void sendSelectionNotifications(List<String> invitedIds, String eventTitle) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        long timestamp = System.currentTimeMillis();

        for (String entrantId : invitedIds) {
            Map<String, Object> notif = new HashMap<>();
            notif.put("entrantId", entrantId);
            notif.put("eventId", eventId);
            notif.put("eventTitle", eventTitle);
            notif.put("message", "You have been selected for \"" + eventTitle + "\"! Please accept or decline.");
            notif.put("timestamp", timestamp);
            notif.put("status", "pending");

            db.collection("notifications").add(notif);
        }
    }



}


