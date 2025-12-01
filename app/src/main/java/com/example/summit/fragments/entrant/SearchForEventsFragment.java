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

    private final List<Event> eventList = new ArrayList<>();
    private final List<Event> filteredList = new ArrayList<>();

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
        });

        recyclerView.setAdapter(adapter);
        loadEvents();

        view.findViewById(R.id.btn_filter).setOnClickListener(v -> openFilterDialogue());

        // SEARCH BUTTON
        view.findViewById(R.id.btn_search).setOnClickListener(v -> {
            EditText searchInput = view.findViewById(R.id.et_search);
            applySearchOnly(searchInput.getText().toString().trim());
        });

        return view;
    }

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

    private void openFilterDialogue() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_event_filter_entrant, null);

        EditText keywordInput = dialogView.findViewById(R.id.input_keyword);
        AutoCompleteTextView locationInput = dialogView.findViewById(R.id.input_location);
        EditText capacityInput = dialogView.findViewById(R.id.input_capacity);
        EditText startDateInput = dialogView.findViewById(R.id.input_start);
        EditText endDateInput = dialogView.findViewById(R.id.input_end);

        Set<String> locationsSet = new HashSet<>();
        for (Event e : eventList) {
            EventDescription d = e.getDescription();
            if (d != null && d.getLocation() != null && !d.getLocation().isEmpty()) {
                locationsSet.add(d.getLocation());
            }
        }
        ArrayAdapter<String> adapterLoc = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>(locationsSet)
        );
        locationInput.setAdapter(adapterLoc);

        // Date picker logic
        startDateInput.setOnClickListener(v -> openDatePicker(startDateInput));
        endDateInput.setOnClickListener(v -> openDatePicker(endDateInput));

        new AlertDialog.Builder(requireContext())
                .setTitle("Filter Events")
                .setView(dialogView)
                .setPositiveButton("Apply", (dialog, which) -> {
                    filterEvents(
                            keywordInput.getText().toString().trim(),
                            locationInput.getText().toString().trim(),
                            capacityInput.getText().toString().trim(),
                            startDateInput.getText().toString().trim(),
                            endDateInput.getText().toString().trim()
                    );
                })
                .setNegativeButton("Reset", (dialog, which) -> {
                    adapter.updateEvents(eventList);
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void openDatePicker(EditText target) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(),
                (view, year, month, day) -> {
                    String formatted = year + "-" + String.format("%02d", month + 1)
                            + "-" + String.format("%02d", day);
                    target.setText(formatted);
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void filterEvents(
            String keyword,
            String location,
            String capacityStr,
            String start,
            String end
    ) {
        filteredList.clear();

        for (Event e : eventList) {
            EventDescription d = e.getDescription();
            if (d == null) continue;

            boolean okKeyword = true;
            boolean okLocation = true;
            boolean okCapacity = true;
            boolean okDate = true;

            // KEYWORD
            if (!keyword.isEmpty()) {
                String lower = keyword.toLowerCase();
                okKeyword =
                        (d.getTitle() != null && d.getTitle().toLowerCase().contains(lower)) ||
                                (d.getDescription() != null && d.getDescription().toLowerCase().contains(lower));
            }

            // LOCATION
            if (!location.isEmpty()) {
                okLocation = d.getLocation() != null &&
                        d.getLocation().equalsIgnoreCase(location);
            }

            // CAPACITY
            if (!capacityStr.isEmpty()) {
                try {
                    long cap = Long.parseLong(capacityStr);
                    if (cap > 10000) cap = 10000; // cap at 10k

                    okCapacity = d.getCapacity() != null &&
                            d.getCapacity() <= cap;
                } catch (NumberFormatException ignored) {}
            }

            // DATE RANGE
            if (!start.isEmpty() && !end.isEmpty() && d.getEventStart() != null && d.getEventEnd() != null) {
                okDate =
                        d.getEventStart().compareTo(start) >= 0 &&
                        d.getEventEnd().compareTo(end) <= 0;
            }

            if (okKeyword && okLocation && okCapacity && okDate) {
                filteredList.add(e);
            }
        }

        adapter.updateEvents(filteredList);
    }

    // SEARCH BAR ONLY
    private void applySearchOnly(String keyword) {
        if (keyword.isEmpty()) {
            adapter.updateEvents(eventList);
            return;
        }

        List<Event> result = new ArrayList<>();
        for (Event e : eventList) {
            EventDescription d = e.getDescription();
            if (d == null) continue;

            if (d.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                    d.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(e);
            }
        }

        adapter.updateEvents(result);
    }
}
