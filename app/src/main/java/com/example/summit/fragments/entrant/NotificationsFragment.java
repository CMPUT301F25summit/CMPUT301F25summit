package com.example.summit.fragments.entrant;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.summit.R;
import com.example.summit.session.Session;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * Entrant Notifications Fragment for EntrantActivity.
 * <purpose>
 * This fragment belongs solely to the EntrantActivity clsss.
 * The user can navigate to this fragment through the entrant_bottom_nav toolbar.
 * Upon navigation:
 *  - provides a list of the entrants notifications in a list format with
 * each list item containing information about the notification
 * - As of right now the items are not clickable (notification items), a user should be able
 * to delete and star notifications. They should also be able to accept/decline selection invites
 * from organizers.
 * <p>
 * Uses Navigation Component with nav_graph_admin for fragment navigation.
 */

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
        String entrantId = Session.getEntrant().getDeviceId();

        db.collection("notifications")
                .whereEqualTo("entrantId", entrantId)
                .get()
                .addOnSuccessListener(query -> {
                    container.removeAllViews();

                    for (DocumentSnapshot doc : query.getDocuments()) {
                        // Inflate a single notification item
                        View notifView = LayoutInflater.from(getContext())
                                .inflate(R.layout.item_notification, container, false);

                        TextView message = notifView.findViewById(R.id.text_message);
                        TextView time = notifView.findViewById(R.id.text_time);

                        message.setText(doc.getString("message"));
                        Long timestamp = doc.getLong("timestamp");
                        if (timestamp != null) {
                            time.setText("Received: " + new java.util.Date(timestamp).toString());
                        } else {
                            time.setText("Received: N/A");
                        }


                        String status = doc.getString("status");
                        if (status == null) status = "pending";
                        message.append(" (" + status.toUpperCase() + ")");
                        if (status.equals("pending")) {
                            String type = doc.getString("type");
                            if (type != null && type.equals("custom")) {
                                notifView.setAlpha(0.8f);
                                notifView.setOnClickListener(v ->
                                        Toast.makeText(getContext(), "This is an organizer notification", Toast.LENGTH_SHORT).show());
                            } else {
                                //makes it only selectable if it's an invitation typa notification
                                notifView.setOnClickListener(v ->
                                        showAcceptDeclineDialog(doc.getId(), doc.getString("eventId")));
                            }
                        } else {
                            notifView.setAlpha(0.5f);
                            notifView.setOnClickListener(null);
                        }

                        container.addView(notifView);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load notifications: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }



    private void showAcceptDeclineDialog(String notifId, String eventId) {
        Log.d("NotificationsFragment", "notifId=" + notifId + ", eventId=" + eventId);

        new AlertDialog.Builder(requireContext())
                .setTitle("Event Invitation")
                .setMessage("Would you like to accept or decline this invitation?")
                .setPositiveButton("Accept", (dialog, which) -> handleResponse(notifId, eventId, true))
                .setNegativeButton("Decline", (dialog, which) -> handleResponse(notifId, eventId, false))
                .show();
    }

    private void handleResponse(String notifId, String eventId, boolean accepted) {
        try {
            String entrantId = Session.getEntrant().getDeviceId();
            String targetList = accepted ? "acceptedList" : "declinedList";
            String addTo = accepted ? "acceptedList" : "declinedList";
            String removeFrom = accepted ? "declinedList" : "acceptedList";
            if (entrantId == null || eventId == null) {
                Toast.makeText(getContext(), "Missing entrantId or eventId!", Toast.LENGTH_SHORT).show();
                return;
            }


            db.collection("events").document(eventId)
                    .update(
                            addTo, FieldValue.arrayUnion(entrantId),
                            removeFrom, FieldValue.arrayRemove(entrantId)
                    );


            db.collection("events").document(eventId)
                    .update(targetList, FieldValue.arrayUnion(entrantId))
                    .addOnSuccessListener(aVoid -> {
                        db.collection("notifications").document(notifId)
                                .update("status", accepted ? "accepted" : "declined");
                        Toast.makeText(getContext(),
                                accepted ? "You accepted the invitation!" : "You declined the invitation.",
                                Toast.LENGTH_SHORT).show();
                        onViewCreated(requireView(), null);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            Log.e("NotificationsFragment", "handleResponse crashed", e);
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}

