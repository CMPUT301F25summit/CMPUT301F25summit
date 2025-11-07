package com.example.summit.fragments.entrant;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.summit.R;
import com.example.summit.session.Session;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailsEntrantFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String eventId;

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
        Button joinBtn = view.findViewById(R.id.button_join_event);

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        title.setText(doc.getString("title"));
                        desc.setText(doc.getString("description"));
                        capacity.setText("Capacity: " + doc.getLong("capacity"));
                        String start = doc.getString("registrationStart");
                        String end = doc.getString("registrationEnd");
                        dates.setText("Registration: " + start + " - " + end);

                        Glide.with(this)
                                .load(doc.getString("posterUrl"))
                                .placeholder(R.drawable.placeholder_event)
                                .into(poster);
                    }
                });

        joinBtn.setOnClickListener(v -> {
            String entrantId = Session.getEntrant().getDeviceId();
            db.collection("events").document(eventId)
                    .update("waitingList", FieldValue.arrayUnion(entrantId))
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(),
                            "Joined event successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(),
                            "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}

