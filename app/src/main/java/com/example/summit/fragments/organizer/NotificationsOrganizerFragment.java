package com.example.summit.fragments.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.summit.R;
import com.example.summit.session.Session;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationsOrganizerFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText inputMessage;
    private Spinner spinnerEvent, spinnerGroup;
    private Button btnSend;

    private List<String> eventNames = new ArrayList<>();
    private List<String> eventIds = new ArrayList<>();

    public NotificationsOrganizerFragment() {
        super(R.layout.fragment_notifications_organizer);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        inputMessage = view.findViewById(R.id.input_notification_message);
        spinnerEvent = view.findViewById(R.id.spinner_event);
        spinnerGroup = view.findViewById(R.id.spinner_target_group);
        btnSend = view.findViewById(R.id.btn_send_notification);

        setupSpinners();
        btnSend.setOnClickListener(v -> sendNotification());
    }

    private void setupSpinners() {
        String organizerId = Session.getOrganizer().getDeviceId();

        db.collection("events")
                .whereEqualTo("organizerId", organizerId)
                .get()
                .addOnSuccessListener(query -> {
                    eventNames.clear();
                    eventIds.clear();

                    eventNames.add("All Events");
                    eventIds.add("ALL");

                    for (var doc : query) {
                        eventNames.add(doc.getString("title"));
                        eventIds.add(doc.getId());
                    }

                    ArrayAdapter<String> eventAdapter = new ArrayAdapter<>(
                            requireContext(), android.R.layout.simple_spinner_dropdown_item, eventNames);
                    spinnerEvent.setAdapter(eventAdapter);
                });

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"All Entrants", "Waiting", "Selected", "Accepted"});
        spinnerGroup.setAdapter(groupAdapter);
    }

    private void sendNotification() {
        String message = inputMessage.getText().toString().trim();
        if (message.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        String chosenEventId = eventIds.get(spinnerEvent.getSelectedItemPosition());
        String targetGroup = spinnerGroup.getSelectedItem().toString();
        String organizerId = Session.getOrganizer().getDeviceId();

        db.collection("events")
                .whereEqualTo("organizerId", organizerId)
                .get()
                .addOnSuccessListener(eventsQuery -> {
                    if (eventsQuery.isEmpty()) {
                        Toast.makeText(getContext(), "No events found!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (var eventDoc : eventsQuery) {
                        String eventId = eventDoc.getId();
                        if (!chosenEventId.equals("ALL") && !chosenEventId.equals(eventId))
                            continue;

                        List<String> recipients = new ArrayList<>();

                        switch (targetGroup) {
                            case "Waiting":
                                recipients = (List<String>) eventDoc.get("waitingList");
                                break;
                            case "Selected":
                                recipients = (List<String>) eventDoc.get("selectedList");
                                break;
                            case "Accepted":
                                recipients = (List<String>) eventDoc.get("acceptedList");
                                break;
                            case "All Entrants":
                                recipients = new ArrayList<>();
                                if (eventDoc.get("waitingList") != null)
                                    recipients.addAll((List<String>) eventDoc.get("waitingList"));
                                if (eventDoc.get("selectedList") != null)
                                    recipients.addAll((List<String>) eventDoc.get("selectedList"));
                                if (eventDoc.get("acceptedList") != null)
                                    recipients.addAll((List<String>) eventDoc.get("acceptedList"));
                                break;
                        }

                        if (recipients == null || recipients.isEmpty()) continue;

                        for (String entrantId : recipients) {
                            Map<String, Object> notification = new HashMap<>();
                            notification.put("eventId", eventId);
                            notification.put("entrantId", entrantId);
                            notification.put("message", message);
                            notification.put("timestamp", System.currentTimeMillis());
                            notification.put("status", "info"); // custom messages are info only
                            notification.put("type", "custom"); //

                            db.collection("notifications").add(notification);
                        }
                    }

                    Toast.makeText(getContext(), "Notifications sent successfully ✅", Toast.LENGTH_SHORT).show();
                    inputMessage.setText("");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}




