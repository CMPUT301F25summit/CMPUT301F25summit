package com.example.summit.fragments.entrant;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.example.summit.utils.EventFilterUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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


    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private FirebaseFirestore db;
    private EditText searchInput;

    private final List<Event> eventList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchforevents, container, false);

        recyclerView = view.findViewById(R.id.recycler_recommended_events);
        searchInput = view.findViewById(R.id.et_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = FirebaseFirestore.getInstance();

        adapter = new EventAdapter(requireContext(), event -> {
            Bundle args = new Bundle();
            args.putString("eventId", event.getId());
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_SearchForEventsFragment_to_EventDetailsEntrantFragment, args);
        });

        recyclerView.setAdapter(adapter);
        loadEvents();

        view.findViewById(R.id.btn_filter).setOnClickListener(v ->
                EventFilterUtil.showFilterDialog(requireContext(), eventList, new EventFilterUtil.FilterCallback() {
                    @Override
                    public void onFilterApplied(List<Event> filteredEvents) {
                        adapter.updateEvents(filteredEvents);
                    }

                    @Override
                    public void onFilterReset(List<Event> allEvents) {
                        adapter.updateEvents(allEvents);
                    }
                })
        );

        // Search button
        view.findViewById(R.id.btn_search).setOnClickListener(v -> {
            String keyword = searchInput.getText().toString().trim();
            List<Event> results = EventFilterUtil.searchByKeyword(eventList, keyword);
            adapter.updateEvents(results);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadEvents();
    }

    private void loadEvents() {
        Firebase.loadEvents(events -> {
            eventList.clear();
            eventList.addAll(events);
            adapter.updateEvents(events);
        });
    }
}