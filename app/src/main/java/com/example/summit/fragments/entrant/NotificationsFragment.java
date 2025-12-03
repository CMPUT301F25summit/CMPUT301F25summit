package com.example.summit.fragments.entrant;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.summit.R;
import com.example.summit.model.Notification;
import com.example.summit.session.Session;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class NotificationsFragment extends Fragment {

    private FirebaseFirestore db;
    private LinearLayout container;

    public NotificationsFragment() {
        super(R.layout.fragment_notifications_entrant);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        container = view.findViewById(R.id.container_notifications);
        db = FirebaseFirestore.getInstance();

        // settings button
        ImageButton settingsBtn = view.findViewById(R.id.btn_settings);
        settingsBtn.setOnClickListener(v -> showSettingsDialog());

        reloadNotifications();
    }

    /**
     * Reload notifications from Firestore & apply user preferences
     */
    private void reloadNotifications() {
        if (container == null) return;

        String entrantId = Session.getEntrant().getDeviceId();
        boolean allowAdmin = Session.getEntrant().isAllowAdmin();
        boolean allowOrganizer = Session.getEntrant().isAllowOrganizer();

        db.collection("notifications")
                .whereEqualTo("entrantId", entrantId)
                .get()
                .addOnSuccessListener(query -> {
                    container.removeAllViews();

                    for (DocumentSnapshot doc : query.getDocuments()) {

                        String senderType = doc.getString("senderType"); // "admin" or "organizer"

                        // Filter based on setting toggles
                        if ("admin".equals(senderType) && !allowAdmin) continue;
                        if ("organizer".equals(senderType) && !allowOrganizer) continue;


                        View notifView = LayoutInflater.from(getContext())
                                .inflate(R.layout.item_notification, container, false);

                        TextView messageView = notifView.findViewById(R.id.text_message);
                        TextView timeView = notifView.findViewById(R.id.text_time);

                        String msg = doc.getString("message");
                        Long timestamp = doc.getLong("timestamp");
                        String status = doc.getString("status");

                        if (status == null) status = "pending";

                        final String fMessage = msg;
                        final Long fTimestamp = timestamp;
                        final String fStatus = status;

                        messageView.setText(fMessage + " (" + fStatus.toUpperCase() + ")");

                        if (timestamp != null) {
                            timeView.setText("Received: " + new Date(fTimestamp));
                        } else {
                            timeView.setText("Received: N/A");
                        }

                        String eventId = doc.getString("eventId");
                        String type = doc.getString("type"); // e.g., "custom", "invitation", etc.
                        String notifId = doc.getId();

                        notifView.setOnClickListener(v ->
                                openNotificationDetails(fMessage, fTimestamp, fStatus));

                        // If it's a pending invitation (non-custom), clicking should accept/decline instead
                        if ("pending".equals(status) && (type == null || !type.equals("custom"))) {
                            notifView.setOnClickListener(v ->
                                    showAcceptDeclineDialog(notifId, eventId));
                        } else if ("pending".equals(status) && "custom".equals(type)) {
                            // Example behaviour for custom pending notifications (organizer-type)
                            notifView.setOnClickListener(v ->
                                    openNotificationDetails(fMessage, fTimestamp, fStatus));
                        }

                        // Visually dim non-pending notifications
                        if (!"pending".equals(status)) {
                            notifView.setAlpha(0.5f);
                        }

                        container.addView(notifView);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Failed to load notifications: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    /**
     * Navigate to NotificationDetailsFragment with a back arrow
     */
    private void openNotificationDetails(String message, Long timestamp, String status) {
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        bundle.putLong("timestamp", timestamp != null ? timestamp : 0L);
        bundle.putString("status", status);

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_notificationsFragment_to_notificationDetailsFragment, bundle);
    }

    /**
     * Dialog to toggle admin & organizer notifications
     */
    private void showSettingsDialog() {
        String entrantId = Session.getEntrant().getDeviceId();

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialogue_notifications_settings, null);

        CheckBox adminCheck = dialogView.findViewById(R.id.check_admin);
        CheckBox organizerCheck = dialogView.findViewById(R.id.check_organizer);

        // Load current values from Firestore
        db.collection("entrants").document(entrantId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Boolean allowAdmin = doc.getBoolean("allowAdminNotifications");
                        Boolean allowOrg = doc.getBoolean("allowOrganizerNotifications");

                        adminCheck.setChecked(allowAdmin == null || allowAdmin);
                        organizerCheck.setChecked(allowOrg == null || allowOrg);
                    }
                });

        new AlertDialog.Builder(requireContext())
                .setTitle("Notification Settings")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    boolean allowAdmin = adminCheck.isChecked();
                    boolean allowOrganizer = organizerCheck.isChecked();

                    // Update in-memory session
                    Session.getEntrant().setAllowAdmin(allowAdmin);
                    Session.getEntrant().setAllowOrganizer(allowOrganizer);

                    // Save to Firestore
                    db.collection("entrants").document(entrantId)
                            .update(
                                    "allowAdminNotifications", allowAdmin,
                                    "allowOrganizerNotifications", allowOrganizer
                            );

                    Toast.makeText(getContext(), "Settings saved", Toast.LENGTH_SHORT).show();
                    reloadNotifications();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Accept/decline invitation dialog
     */
    private void showAcceptDeclineDialog(String notifId, String eventId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Event Invitation")
                .setMessage("Would you like to accept or decline this invitation?")
                .setPositiveButton("Accept", (dialog, which) ->
                        handleResponse(notifId, eventId, true))
                .setNegativeButton("Decline", (dialog, which) ->
                        handleResponse(notifId, eventId, false))
                .show();
    }

    /**
     * Handles updating Firestore lists for accepted/declined
     */
    private void handleResponse(String notifId, String eventId, boolean accepted) {
        try {
            String entrantId = Session.getEntrant().getDeviceId();
            String addTo = accepted ? "acceptedList" : "declinedList";
            String removeFrom = accepted ? "declinedList" : "acceptedList";

            db.collection("events").document(eventId)
                    .update(
                            addTo, FieldValue.arrayUnion(entrantId),
                            removeFrom, FieldValue.arrayRemove(entrantId)
                    )
                    .addOnSuccessListener(aVoid -> {
                        db.collection("notifications").document(notifId)
                                .update("status", accepted ? "accepted" : "declined");

                        Toast.makeText(
                                getContext(),
                                accepted ? "You accepted the invitation!" : "You declined the invitation.",
                                Toast.LENGTH_SHORT
                        ).show();

                        reloadNotifications();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(),
                                    "Failed: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show());

        } catch (Exception e) {
            Log.e("NotificationsFragment", "handleResponse crashed", e);
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}