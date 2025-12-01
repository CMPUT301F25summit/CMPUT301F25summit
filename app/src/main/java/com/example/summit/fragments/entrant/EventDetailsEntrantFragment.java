package com.example.summit.fragments.entrant;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
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

import com.example.summit.model.Event;
import com.example.summit.model.EventDescription;
import com.example.summit.model.Entrant;
import com.example.summit.session.Session;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class EventDetailsEntrantFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String eventId;
    private Button joinBtn;
    private Event currentEvent;

    public EventDetailsEntrantFragment() {
        super(R.layout.fragment_event_details_entrant);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventId = getArguments().getString("eventId");

        TextView title = view.findViewById(R.id.text_event_title);
        TextView desc = view.findViewById(R.id.text_event_description);
        TextView capacity = view.findViewById(R.id.text_capacity);
        TextView dates = view.findViewById(R.id.text_reg_dates);
        ImageView poster = view.findViewById(R.id.image_event_poster);
        joinBtn = view.findViewById(R.id.button_join_event);
        ImageButton closeBtn = view.findViewById(R.id.button_close);

        closeBtn.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        loadEventDetails(title, desc, capacity, dates, poster);

        joinBtn.setOnClickListener(v -> joinEvent());

    }

    private void loadEventDetails(TextView title, TextView desc, TextView capacity,
                                  TextView dates, ImageView poster) {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) return;

                    currentEvent = new Event();
                    currentEvent.setId(doc.getId());

                    // Build EventDescription manually
                    EventDescription d = new EventDescription();
                    d.setTitle(doc.getString("title"));
                    d.setDescription(doc.getString("description"));
                    d.setLocation(doc.getString("location"));
                    d.setCapacity(doc.getLong("capacity"));
                    d.setEventStart(doc.getString("eventStart"));
                    d.setEventEnd(doc.getString("eventEnd"));

                    // posterBase64 is stored in Firestore → we treat it as the posterUrl
                    d.setPosterUrl(doc.getString("posterBase64"));

                    currentEvent.setDescription(d);

                    // Load list fields
                    List<String> registered = (List<String>) doc.get("acceptedList");
                    List<String> declined = (List<String>) doc.get("declinedList");

                    if (registered == null) registered = new ArrayList<>();
                    if (declined == null) declined = new ArrayList<>();

                    currentEvent.setRegisteredEntrants(registered);
                    currentEvent.setDeclinedEntrants(declined);

                    // Firestore may not have waitingList at all
                    List<String> waitlist = (List<String>) doc.get("waitingList");
                    if (waitlist == null) waitlist = new ArrayList<>();
                    currentEvent.setWaitingList(waitlist);

                    // Update UI
                    renderUI(title, desc, capacity, dates, poster);
                    updateButtonState();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "Failed to load event: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    });
    }

    private void renderUI(TextView title, TextView desc, TextView capacity,
                          TextView dates, ImageView poster) {
        EventDescription ed = currentEvent.getDescription();

        title.setText(ed.getTitle());
        desc.setText(ed.getDescription());
        capacity.setText("Capacity: " + ed.getCapacity());

        dates.setText("Event: " + ed.getEventStart() + " - " + ed.getEventEnd());

        Glide.with(this)
                .load(ed.getPosterUrl())
                .placeholder(R.drawable.placeholder_event)
                .into(poster);


    }

    private void updateButtonState() {
        if(currentEvent == null) return;

        String entrantId = Session.getEntrant().getDeviceId();

        List<String> waitingList = currentEvent.getWaitingList();
        List<String> registeredList = currentEvent.getRegisteredEntrants();

        if (waitingList == null) waitingList = new ArrayList<>();
        if (registeredList == null) registeredList = new ArrayList<>();

        if(registeredList.contains(entrantId)) {
            joinBtn.setText("You've Been Selected!");
            joinBtn.setEnabled(false);
            joinBtn.setBackgroundResource(R.drawable.join_event_button_grn);
            joinBtn.setTextColor(Color.WHITE);
        }
        else if (waitingList.contains(entrantId)) {
            joinBtn.setText("Already on Waitlist");
            joinBtn.setEnabled(false);
            joinBtn.setBackgroundResource(R.drawable.join_event_button_gray);
            joinBtn.setTextColor(Color.WHITE);
        }
        else {
            joinBtn.setText("Join Event");
            joinBtn.setEnabled(true);
            joinBtn.setBackgroundResource(R.drawable.join_event_button_blk);
            joinBtn.setTextColor(Color.WHITE);

        }
    }

     private void joinEvent() {
        Entrant entrant = Session.getEntrant();
        String entrantId = entrant.getDeviceId();

        DocumentReference eventRef = db.collection("events").document(eventId);

        eventRef.get().addOnSuccessListener(document -> {

            boolean isLocationRequired = Boolean.TRUE.equals(document.getBoolean("requiredLocation"));
            boolean entrantLocationValid =
                    entrant.getLocationShared() && entrant.getLocation() != null;

            // ADD: geolocation validation (from main)
            if (isLocationRequired && !entrantLocationValid) {
                Toast.makeText(getContext(),
                        "This event requires your location. Please enable location sharing.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            eventRef.update("waitingList", FieldValue.arrayUnion(entrantId))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(),
                                "Joined waitlist successfully!", Toast.LENGTH_SHORT).show();

                        if (currentEvent.getWaitingList() == null) currentEvent.setWaitingList(new ArrayList<>());

                        currentEvent.getWaitingList().add(entrantId);
                        updateButtonState();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(),
                                    "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}