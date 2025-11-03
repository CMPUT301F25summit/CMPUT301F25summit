package com.example.summit.fragments.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.example.summit.session.Session;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ManageEventsFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private EventAdapter adapter;

    public ManageEventsFragment() {
        super(R.layout.fragment_manage_events);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_manage_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_my_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new EventAdapter(getContext(), event -> {
            Bundle args = new Bundle();
            args.putString("eventId", event.getId());

            // placeholder for future ManageEntrantsFragment
            Toast.makeText(getContext(), "Event selected: " + event.getDescription().getTitle(), Toast.LENGTH_SHORT).show();

//            NavHostFragment.findNavController(this)
//                    .navigate(R.id.action_to_ManageEntrantsFragment, args);
        });

        recyclerView.setAdapter(adapter);
        loadMyEvents();
    }

    private void loadMyEvents() {
        String organizerId = Session.getOrganizer().getDeviceId();

        db.collection("events")
                .whereEqualTo("organizerId", organizerId)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    List<Event> ownedEvents = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        EventDescription desc = doc.toObject(EventDescription.class);
                        if (desc != null) {
                            Event event = new Event(desc);
                            event.setId(doc.getId());
                            ownedEvents.add(event);
                        }
                    }
                    adapter.updateEvents(ownedEvents);
                });
    }
}

