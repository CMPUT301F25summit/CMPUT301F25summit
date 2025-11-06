package com.example.summit.fragments.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.summit.R;
import com.example.summit.session.Session;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationsOrganizerFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText inputMessage;

    public NotificationsOrganizerFragment() {
        super(R.layout.fragment_notifications_organizer);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        inputMessage = view.findViewById(R.id.input_notification_message);
        Button btnSend = view.findViewById(R.id.btn_send_notification);

        btnSend.setOnClickListener(v -> sendNotificationToEntrants());
    }

    private void sendNotificationToEntrants() {
        String message = inputMessage.getText().toString().trim();
        if (message.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        String organizerId = Session.getOrganizer().getDeviceId();

        db.collection("events")
                .whereEqualTo("organizerId", organizerId)
                .get()
                .addOnSuccessListener(eventsQuery -> {

                    if (eventsQuery.isEmpty()) {
                        Toast.makeText(getContext(), "No registered entrants found!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (var eventDoc : eventsQuery) {
                        String eventId = eventDoc.getId();
                        List<String> entrants = (List<String>) eventDoc.get("waitingList");

                        if (entrants == null || entrants.isEmpty()) continue;

                        for (String entrantId : entrants) {
                            Map<String, Object> notification = new HashMap<>();
                            notification.put("eventId", eventId);
                            notification.put("recipientId", entrantId);
                            notification.put("message", message);
                            notification.put("timestamp", System.currentTimeMillis());

                            db.collection("notifications")
                                    .add(notification);
                        }
                    }

                    Toast.makeText(getContext(), "Notification Sent ✅", Toast.LENGTH_SHORT).show();
                    inputMessage.setText("");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}



