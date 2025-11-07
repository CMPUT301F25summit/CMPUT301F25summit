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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Fragment} that displays a list of events created by the current organizer.
 *
 * This fragment queries the "events" collection based on the organizer's ID
 * (from the {@link Session}) and displays them in a {@link RecyclerView}.
 * It also ensures the "Add Event" FAB is visible.
 */
public class ManageEventsFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private EventAdapter adapter;

    public ManageEventsFragment() {
        super(R.layout.fragment_manage_events);
    }

    /**
     * Inflates the layout for this fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_manage_events, container, false);
    }

    /**
     * Initializes the view, shows the "Add Event" FAB, and sets up the
     * {@link RecyclerView} with an {@link EventAdapter}.
     *
     * The adapter's click listener navigates to the event details screen
     * ({@code EventDetailsOrganizerFragment}) for the selected event.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = requireActivity().findViewById(R.id.fab_add_event);
        fab.setVisibility(View.VISIBLE);


        recyclerView = view.findViewById(R.id.rv_my_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new EventAdapter(getContext(), event -> {

            Bundle args = new Bundle();
            args.putString("eventId", event.getId());

            // placeholder for future ManageEntrantsFragment
//            Toast.makeText(getContext(), "Event selected: " + event.getDescription().getTitle(), Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_manageEvents_to_eventDetailsOrganizer, args);


        }
        );

        recyclerView.setAdapter(adapter);
        loadMyEvents();
    }

    /**
     * Fetches all events from Firestore where the "organizerId" matches
     * the current organizer's ID from the {@link Session}.
     *
     * Attaches a snapshot listener to update the list in real-time.
     */
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

