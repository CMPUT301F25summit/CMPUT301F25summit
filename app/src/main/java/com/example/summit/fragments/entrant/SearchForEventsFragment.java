package com.example.summit.fragments.entrant;
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
import com.example.summit.model.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;



/**
 * Search Events Fragment for EntrantActivity.
 * <purpose>
 * This fragment belongs solely to the EntrantActivity clsss.
 * The user is by default redirected to this fragment upon login but can later navigate to this fragment through the entrant_bottom_nav toolbar.
 * Upon navigation:
 *  - provides a list of the Events currently in the database.
 * each event displays a photo of the event, event title, and event date
 * - As of right now the items are not clickable (event items), a user should be able
 * to open an event, and view the events description as well as more details. Entrants should also be given
 * a choice to enroll in the waitlist of that event if they have not already.
 *
 * -- Currently Missing
 * filter and search functionality. Clickable event items and enrollment of event.
 */

public class SearchForEventsFragment extends Fragment {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private final List<Event> eventList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_searchforevents, container, false);

        recyclerView = view.findViewById(R.id.recycler_recommended_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = FirebaseFirestore.getInstance();

        adapter = new EventAdapter(requireContext(), event -> {
            Bundle args = new Bundle();
            args.putString("eventId", event.getId());
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_SearchForEventsFragment_to_EventDetailsEntrantFragment, args);

            // later need to navigate to EventDetail frag and pass event.getid()
        });

        recyclerView.setAdapter(adapter);
        loadEvents();

        return view;
    }

    public void onResume() {
        super.onResume();
        loadEvents();
    }

    private void loadEvents() {
        Firebase.loadEvents(events -> {
            adapter.updateEvents(events);
        });
    }

}
