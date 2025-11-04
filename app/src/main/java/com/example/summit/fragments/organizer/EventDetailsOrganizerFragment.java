package com.example.summit.fragments.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.summit.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailsOrganizerFragment extends Fragment {

    private TextView titleText, descText, regDatesText, capacityText,
            waitingCountText, invitedCountText, acceptedCountText;
    private ImageView posterImage;
    private Button manageEntrantsBtn, runLotteryBtn, editEventBtn;

    private String eventId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public EventDetailsOrganizerFragment() {
        super(R.layout.fragment_event_details_organizer);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventId = getArguments() != null ? getArguments().getString("eventId") : null;
        if (eventId == null) {
            Toast.makeText(getContext(), "Error: Missing event ID", Toast.LENGTH_SHORT).show();
            return;
        }

        initViews(view);
        loadEventData();
        setupButtons();
    }

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
    }

    private void loadEventData() {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(this::updateUIFromFirestore)
                .addOnFailureListener(error ->
                        Toast.makeText(getContext(), "Load failed: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

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

    private long getCount(DocumentSnapshot doc, String field) {
        return doc.get(field) != null ?
                ((java.util.List<?>) doc.get(field)).size() : 0;
    }

    private void setupButtons() {

        manageEntrantsBtn.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("eventId", eventId);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_eventDetailsOrganizer_to_manageEntrants, args);
        });

        runLotteryBtn.setOnClickListener(v -> {
            // Placeholder - trigger lottery later
            Toast.makeText(getContext(), "Lottery feature not implemented yet",
                    Toast.LENGTH_SHORT).show();
        });

        editEventBtn.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("eventId", eventId);
            Toast.makeText(getContext(), "TODO: Edit Event Screen Next",
                    Toast.LENGTH_SHORT).show();
        });
    }
}


