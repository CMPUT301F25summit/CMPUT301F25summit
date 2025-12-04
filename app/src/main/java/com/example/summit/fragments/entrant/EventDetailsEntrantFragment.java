package com.example.summit.fragments.entrant;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.summit.R;
import com.example.summit.model.Event;
import com.example.summit.model.EventDescription;
import com.example.summit.model.Entrant;
import com.example.summit.model.LotterySystem;
import com.example.summit.session.Session;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EventDetailsEntrantFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String eventId;
    private Button joinBtn, leaveWaitlistButton;
    private Event currentEvent;

    private ProgressBar progressBar;
    private ScrollView scrollContent;

    public EventDetailsEntrantFragment() {
        super(R.layout.fragment_event_details_entrant);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventId = getArguments().getString("eventId");

        // Find views
        progressBar = view.findViewById(R.id.progress_bar);
        scrollContent = view.findViewById(R.id.scroll_content);
        TextView title = view.findViewById(R.id.text_event_title);
        TextView desc = view.findViewById(R.id.text_event_description);
        TextView capacity = view.findViewById(R.id.text_capacity);
        TextView dates = view.findViewById(R.id.text_reg_dates);
        ImageView poster = view.findViewById(R.id.image_event_poster);
        joinBtn = view.findViewById(R.id.button_join_event_entrant);
        ImageButton closeBtn = view.findViewById(R.id.button_close);
        leaveWaitlistButton = view.findViewById(R.id.button_leave_event_entrant);
        final Entrant currentUser = Session.getEntrant();

        leaveWaitlistButton.setOnClickListener(v -> {
            if (currentUser != null && currentEvent != null) {
                currentEvent.removeEntrantFromWaitList(currentUser);


                // Update the UI, show a confirmation, etc.
                Toast.makeText(getContext(), "You have left the waitlist.", Toast.LENGTH_SHORT).show();
            }
        });

        closeBtn.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        showLoading();
        loadEventDetails(title, desc, capacity, dates, poster);
        joinBtn.setOnClickListener(v -> joinEvent());
        showContent();
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        scrollContent.setVisibility(View.GONE);
    }

    private void showContent() {
        progressBar.setVisibility(View.GONE);
        scrollContent.setVisibility(View.VISIBLE);
    }

    private void loadEventDetails(TextView title, TextView desc, TextView capacity,
                                  TextView dates, ImageView poster) {

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        Toast.makeText(getContext(),
                                "Event not found",
                                Toast.LENGTH_SHORT).show();
                        showContent();
                        return;
                    }


                    EventDescription d = new EventDescription();
                    d.setTitle(doc.getString("title"));
                    d.setPosterBase64(doc.getString("posterBase64"));
                    d.setDescription(doc.getString("description"));
                    d.setLocation(doc.getString("location"));
                    d.setCapacity(doc.getLong("capacity"));
                    d.setEventStart(doc.getString("eventStart"));
                    d.setEventEnd(doc.getString("eventEnd"));

                    currentEvent = new Event();
                    currentEvent.setId(doc.getId());
                    currentEvent.setDescription(d);

                    List<String> registered = (List<String>) doc.get("acceptedList");
                    List<String> declined = (List<String>) doc.get("declinedList");
                    List<String> waitlist = (List<String>) doc.get("waitingList");

                    if (registered == null) registered = new ArrayList<>();
                    if (declined == null) declined = new ArrayList<>();
                    if (waitlist == null) waitlist = new ArrayList<>();

                    currentEvent.setRegisteredEntrants(registered);
                    currentEvent.setDeclinedEntrants(declined);
                    currentEvent.setWaitingList(waitlist);

                    updateButtonState();
                    renderUIWithImageLoad(title, desc, capacity, dates, poster);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "Failed to load event: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void renderUIWithImageLoad(TextView title, TextView desc, TextView capacity,
                                       TextView dates, ImageView poster) {

        EventDescription ed = currentEvent.getDescription();

        title.setText(ed.getTitle());
        desc.setText(ed.getDescription());
        capacity.setText("Capacity: " + ed.getCapacity());
        dates.setText("Event: " + ed.getEventStart() + " - " + ed.getEventEnd());

        String posterBase64 = ed.getPosterBase64();
        if (posterBase64 != null && !posterBase64.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(posterBase64, Base64.DEFAULT);
                Glide.with(this)
                        .load(decodedBytes)
                        .placeholder(R.drawable.ic_image)
                        .error(R.drawable.placeholder_event)
                        .into(poster);
            } catch (IllegalArgumentException e) {
                poster.setImageResource(R.drawable.placeholder_event);
            }
        } else {
            poster.setImageResource(R.drawable.placeholder_event);
        }

    }

    private void updateButtonState() {
        if (currentEvent == null) {
            joinBtn.setBackgroundColor(Color.BLACK);
            return;
        }

        String entrantId = Session.getEntrant().getDeviceId();
        List<String> waitingList = currentEvent.getWaitingList();
        List<String> registeredList = currentEvent.getRegisteredEntrants();

        if (waitingList == null) waitingList = new ArrayList<>();
        if (registeredList == null) registeredList = new ArrayList<>();

        if (registeredList.contains(entrantId)) {
            joinBtn.setText("You've Been Selected!");
            joinBtn.setEnabled(false);
            joinBtn.setBackgroundColor(Color.GREEN);
            joinBtn.setTextColor(Color.WHITE);
        }
        else if (waitingList.contains(entrantId)) {
            joinBtn.setText("Already on Waitlist");
            joinBtn.setEnabled(false);
            joinBtn.setBackgroundColor(Color.DKGRAY);
            joinBtn.setTextColor(Color.WHITE);
        }
        else {
            joinBtn.setText("Join Event");
            joinBtn.setEnabled(true);
            joinBtn.setBackgroundColor(Color.BLACK);
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

                        if (currentEvent.getWaitingList() == null)
                            currentEvent.setWaitingList(new ArrayList<>());

                        currentEvent.getWaitingList().add(entrantId);
                        updateButtonState();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(),
                                    "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}