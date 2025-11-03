package com.example.summit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.summit.R;
import com.example.summit.adapters.EventAdapter;
import com.example.summit.model.Event;
import com.example.summit.model.EventDescription;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EventListFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        recyclerView = view.findViewById(R.id.event_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new EventAdapter(getContext(), event -> {
            Bundle args = new Bundle();
            args.putString("eventId", event.getId());

            NavHostFragment.findNavController(EventListFragment.this)
                    .navigate(R.id.action_EventListFragment_to_EventDetailsFragment, args);
        });

        recyclerView.setAdapter(adapter);

        loadEventsFromFirestore();

        return view;
    }

    private void loadEventsFromFirestore() {
        db.collection("events")
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    List<Event> updatedEvents = new ArrayList<>();

                    for (DocumentSnapshot doc : value.getDocuments()) {
                        EventDescription desc = doc.toObject(EventDescription.class);
                        if (desc != null) {
                            Event event = new Event(desc);
                            event.setId(doc.getId()); //this assigns the event id
                            updatedEvents.add(event);
                        }
                    }

                    adapter.updateEvents(updatedEvents);
                });
    }

}



