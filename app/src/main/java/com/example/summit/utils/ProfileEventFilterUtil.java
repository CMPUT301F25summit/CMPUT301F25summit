package com.example.summit.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.example.summit.R;
import com.example.summit.model.Event;
import com.example.summit.model.EventDescription;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Extension of EventFilterUtil for profile events that include status information
 */
public class ProfileEventFilterUtil {

    /**
     * Container for event and its status
     */
    public static class EventWithStatus {
        public final Event event;
        public final String status;

        public EventWithStatus(Event event, String status) {
            this.event = event;
            this.status = status;
        }
    }

    /**
     * Search events with status by keyword
     */
    public static FilterResult searchByKeyword(
            List<Event> events,
            List<String> statuses,
            String keyword
    ) {
        if (keyword.isEmpty()) {
            return new FilterResult(events, statuses);
        }

        List<Event> resultEvents = new ArrayList<>();
        List<String> resultStatuses = new ArrayList<>();

        for (int i = 0; i < events.size(); i++) {
            Event e = events.get(i);
            EventDescription d = e.getDescription();
            if (d == null) continue;

            String lower = keyword.toLowerCase();
            if ((d.getTitle() != null && d.getTitle().toLowerCase().contains(lower)) ||
                    (d.getDescription() != null && d.getDescription().toLowerCase().contains(lower))) {
                resultEvents.add(e);
                resultStatuses.add(statuses.get(i));
            }
        }

        return new FilterResult(resultEvents, resultStatuses);
    }

    /**
     * Filter events with status
     */
    public static FilterResult filterEvents(
            List<Event> events,
            List<String> statuses,
            String keyword,
            String location,
            String capacityStr,
            String startDate,
            String endDate
    ) {
        List<Event> resultEvents = new ArrayList<>();
        List<String> resultStatuses = new ArrayList<>();

        for (int i = 0; i < events.size(); i++) {
            Event e = events.get(i);
            EventDescription d = e.getDescription();
            if (d == null) continue;

            // Reuse the filter logic from EventFilterUtil
            List<Event> singleEventList = new ArrayList<>();
            singleEventList.add(e);
            List<Event> filtered = EventFilterUtil.filterEvents(
                    singleEventList, keyword, location, capacityStr, startDate, endDate
            );

            if (!filtered.isEmpty()) {
                resultEvents.add(e);
                resultStatuses.add(statuses.get(i));
            }
        }

        return new FilterResult(resultEvents, resultStatuses);
    }

    /**
     * Show filter dialog for profile events
     */
    public static void showFilterDialog(
            Context context,
            List<Event> allEvents,
            List<String> allStatuses,
            ProfileFilterCallback callback
    ) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_event_filter_entrant, null);

        EditText keywordInput = dialogView.findViewById(R.id.input_keyword);
        AutoCompleteTextView locationInput = dialogView.findViewById(R.id.input_location);
        EditText capacityInput = dialogView.findViewById(R.id.input_capacity);
        EditText startDateInput = dialogView.findViewById(R.id.input_start);
        EditText endDateInput = dialogView.findViewById(R.id.input_end);

        // Populate locations
        Set<String> locationsSet = new HashSet<>();
        for (Event e : allEvents) {
            EventDescription d = e.getDescription();
            if (d != null && d.getLocation() != null && !d.getLocation().isEmpty()) {
                locationsSet.add(d.getLocation());
            }
        }
        ArrayAdapter<String> adapterLoc = new ArrayAdapter<>(
                context,
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>(locationsSet)
        );
        locationInput.setAdapter(adapterLoc);

        // Date pickers
        startDateInput.setOnClickListener(v ->
                EventFilterUtil.showDatePicker(context, startDateInput));
        endDateInput.setOnClickListener(v ->
                EventFilterUtil.showDatePicker(context, endDateInput));

        new AlertDialog.Builder(context)
                .setTitle("Filter Events")
                .setView(dialogView)
                .setPositiveButton("Apply", (dialog, which) -> {
                    FilterResult result = filterEvents(
                            allEvents,
                            allStatuses,
                            keywordInput.getText().toString().trim(),
                            locationInput.getText().toString().trim(),
                            capacityInput.getText().toString().trim(),
                            startDateInput.getText().toString().trim(),
                            endDateInput.getText().toString().trim()
                    );
                    callback.onFilterApplied(result.events, result.statuses);
                })
                .setNegativeButton("Reset", (dialog, which) -> {
                    callback.onFilterReset(allEvents, allStatuses);
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    /**
     * Result container for filtered events with statuses
     */
    public static class FilterResult {
        public final List<Event> events;
        public final List<String> statuses;

        public FilterResult(List<Event> events, List<String> statuses) {
            this.events = events;
            this.statuses = statuses;
        }
    }

    /**
     * Callback for profile filter operations
     */
    public interface ProfileFilterCallback {
        void onFilterApplied(List<Event> filteredEvents, List<String> filteredStatuses);
        void onFilterReset(List<Event> allEvents, List<String> allStatuses);
    }
}