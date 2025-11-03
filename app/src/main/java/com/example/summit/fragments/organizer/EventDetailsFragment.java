package com.example.summit.fragments.organizer;

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
import com.example.summit.session.Session;
import com.example.summit.model.Entrant;

public class EventDetailsFragment extends Fragment {

    private Event event; // Will display event info later

    public EventDetailsFragment() {
        super(R.layout.fragment_event_details);
    }

    @Nullable
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        String eventId = getArguments() != null ? getArguments().getString("eventId") : null;
        Entrant currentEntrant = Session.getEntrant();
        if (currentEntrant == null) {
            Toast.makeText(getContext(), "No entrant session found", Toast.LENGTH_SHORT).show();
            return;
        }
        if (eventId == null) {
            Toast.makeText(getContext(), "Missing event id", Toast.LENGTH_SHORT).show();
            return;
        }

        Button joinBtn = view.findViewById(R.id.button_join);
        joinBtn.setOnClickListener(v -> {
            SignUp signup = new SignUp();
            signup.joinEventFirestore(currentEntrant, eventId);
            Toast.makeText(getContext(), "Joined waiting list!", Toast.LENGTH_SHORT).show();
        });
    }

}
