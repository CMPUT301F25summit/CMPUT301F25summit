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
import com.example.summit.model.Event;
import com.example.summit.model.EventDescription;
import com.example.summit.adapters.EventAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventListFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> sampleEvents;
    private List<EventDescription> eventList = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        recyclerView = view.findViewById(R.id.event_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new EventAdapter(sampleEvents, event -> {
            Bundle args = new Bundle();
            args.putString("eventId", event.getDescription().getTitle()); // placeholder

            NavHostFragment.findNavController(EventListFragment.this)
                    .navigate(R.id.action_EventListFragment_to_EventDetailsFragment, args);
        });
        recyclerView.setAdapter(adapter);

        loadEventsFromFirestore();
        return view;
    }

    private void loadEventsFromFirestore() {
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        EventDescription event = doc.toObject(EventDescription.class);
                        eventList.add(event);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void loadMockEvents() {
        events.add(new Event(new EventDescription("Basketball", "Fun sports", null, null, null, null, 99, null)));
        events.add(new Event(new EventDescription("Swimming", "Edmonton AB", null, null, null, null, 50, null)));
        adapter.notifyDataSetChanged();
    }
}

