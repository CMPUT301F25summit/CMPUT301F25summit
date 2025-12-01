package com.example.summit.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class for filtering and searching events.
 * Provides reusable methods for event search and filter functionality.
 */
public class EventFilterUtil {

    /**
     * Filter events by keyword only (search functionality)
     */
    public static List<Event> searchByKeyword(List<Event> events, String keyword) {
        if (keyword.isEmpty()) {
            return new ArrayList<>(events);
        }

        List<Event> result = new ArrayList<>();
        for (Event e : events) {
            EventDescription d = e.getDescription();
            if (d == null) continue;

            String lower = keyword.toLowerCase();
            if ((d.getTitle() != null && d.getTitle().toLowerCase().contains(lower)) ||
                    (d.getDescription() != null && d.getDescription().toLowerCase().contains(lower))) {
                result.add(e);
            }
        }
        return result;
    }

    /**
     * Filter events by multiple criteria
     */
    public static List<Event> filterEvents(
            List<Event> events,
            String keyword,
            String location,
            String capacityStr,
            String startDate,
            String endDate
    ) {
        List<Event> result = new ArrayList<>();

        for (Event e : events) {
            EventDescription d = e.getDescription();
            if (d == null) continue;

            if (matchesCriteria(d, keyword, location, capacityStr, startDate, endDate)) {
                result.add(e);
            }
        }

        return result;
    }

    /**
     * Check if an event matches all filter criteria
     */
    private static boolean matchesCriteria(
            EventDescription d,
            String keyword,
            String location,
            String capacityStr,
            String startDate,
            String endDate
    ) {
        // KEYWORD
        boolean okKeyword = true;
        if (!keyword.isEmpty()) {
            String lower = keyword.toLowerCase();
            okKeyword = (d.getTitle() != null && d.getTitle().toLowerCase().contains(lower)) ||
                    (d.getDescription() != null && d.getDescription().toLowerCase().contains(lower));
        }

        // LOCATION
        boolean okLocation = true;
        if (!location.isEmpty()) {
            okLocation = d.getLocation() != null && d.getLocation().equalsIgnoreCase(location);
        }

        // CAPACITY
        boolean okCapacity = true;
        if (!capacityStr.isEmpty()) {
            try {
                long cap = Long.parseLong(capacityStr);
                if (cap > 10000) cap = 10000;
                okCapacity = d.getCapacity() != null && d.getCapacity() <= cap;
            } catch (NumberFormatException ignored) {
            }
        }

        // DATE RANGE
        boolean okDate = true;
        if (!startDate.isEmpty() && !endDate.isEmpty() &&
                d.getEventStart() != null && d.getEventEnd() != null) {
            okDate = d.getEventStart().compareTo(startDate) >= 0 &&
                    d.getEventEnd().compareTo(endDate) <= 0;
        }

        return okKeyword && okLocation && okCapacity && okDate;
    }

    /**
     * Show filter dialog and return filtered events via callback
     */
    public static void showFilterDialog(
            Context context,
            List<Event> allEvents,
            FilterCallback callback
    ) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_event_filter_entrant, null);

        EditText keywordInput = dialogView.findViewById(R.id.input_keyword);
        AutoCompleteTextView locationInput = dialogView.findViewById(R.id.input_location);
        EditText capacityInput = dialogView.findViewById(R.id.input_capacity);
        EditText startDateInput = dialogView.findViewById(R.id.input_start);
        EditText endDateInput = dialogView.findViewById(R.id.input_end);

        // Populate locations dropdown
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
        startDateInput.setOnClickListener(v -> showDatePicker(context, startDateInput));
        endDateInput.setOnClickListener(v -> showDatePicker(context, endDateInput));

        new AlertDialog.Builder(context)
                .setTitle("Filter Events")
                .setView(dialogView)
                .setPositiveButton("Apply", (dialog, which) -> {
                    List<Event> filtered = filterEvents(
                            allEvents,
                            keywordInput.getText().toString().trim(),
                            locationInput.getText().toString().trim(),
                            capacityInput.getText().toString().trim(),
                            startDateInput.getText().toString().trim(),
                            endDateInput.getText().toString().trim()
                    );
                    callback.onFilterApplied(filtered);
                })
                .setNegativeButton("Reset", (dialog, which) -> {
                    callback.onFilterReset(allEvents);
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    /**
     * Show date picker dialog
     */
    public static void showDatePicker(Context context, EditText target) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(context,
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

    /**
     * Callback interface for filter results
     */
    public interface FilterCallback {
        void onFilterApplied(List<Event> filteredEvents);
        void onFilterReset(List<Event> allEvents);
    }
}