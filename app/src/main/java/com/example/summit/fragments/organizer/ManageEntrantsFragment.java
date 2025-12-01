package com.example.summit.fragments.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.summit.R;
import com.example.summit.adapters.EntrantAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.summit.model.Entrant;

/**
 * A {@link Fragment} for an organizer to view a list of all entrants
 * associated with a specific event.
 *
 * This fragment retrieves all entrant IDs from the event's various lists
 * (waiting, selected, accepted, declined), fetches their details,
 * and displays them in a {@link RecyclerView} using an {@link EntrantAdapter}.
 */
public class ManageEntrantsFragment extends Fragment {

    private String eventId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EntrantAdapter adapter;
    private ImageButton backButton;

    public ManageEntrantsFragment() {
        super(R.layout.fragment_manage_entrants);
    }

    /**
     * Initializes the view, retrieves the eventId, hides the parent FAB,
     * and sets up the {@link RecyclerView} and {@link EntrantAdapter}.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_add_event);
        if (fab != null) fab.setVisibility(View.GONE);
        eventId = getArguments().getString("eventId", null);
        if (eventId == null) {
            Toast.makeText(getContext(), "Missing event ID", Toast.LENGTH_SHORT).show();
            return;
        }

        backButton = view.findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        RecyclerView rvEntrants = view.findViewById(R.id.rv_entrants);
        rvEntrants.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new EntrantAdapter(new ArrayList<>());


        rvEntrants.setAdapter(adapter);

        loadEntrants();
    }

    /**
     * Loads all entrants for the current event.
     *
     * Fetches the event document, aggregates all unique entrant IDs from
     * the 'waitingList', 'selectedList', 'acceptedList', and 'declinedList' fields.
     * It then fetches each entrant's document from the "entrants" collection
     * and adds them to the {@link EntrantAdapter}.
     */
    private void loadEntrants() {
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    List<String> waitingList = (List<String>) doc.get("waitingList");
                    List<String> selectedList = (List<String>) doc.get("selectedList");
                    List<String> acceptedList = (List<String>) doc.get("acceptedList");
                    List<String> declinedList = (List<String>) doc.get("declinedList");

// Combine all entrants
                    Set<String> waitingIds = new HashSet<>();
                    if (waitingList != null) waitingIds.addAll(waitingList);
                    if (selectedList != null) waitingIds.addAll(selectedList);
                    if (acceptedList != null) waitingIds.addAll(acceptedList);
                    if (declinedList != null) waitingIds.addAll(declinedList);

                    if (waitingIds == null || waitingIds.isEmpty()) {
                        Toast.makeText(getContext(), "No entrants yet", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (String entrantId : waitingIds) {
                        db.collection("entrants")
                                .document(entrantId)
                                .get()
                                .addOnSuccessListener(eDoc -> {
                                    Entrant e = eDoc.toObject(Entrant.class);
                                    if (e != null) {
                                        adapter.addEntrant(e);
                                    }
                                });
                    }
                });
    }
}

