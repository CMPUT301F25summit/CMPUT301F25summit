package com.example.summit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.summit.R;
import com.example.summit.model.Event;
import com.example.summit.model.EventDescription;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * RecyclerView adapter for displaying and managing events in the admin dashboard.
 * <p>
 * This adapter provides:
 * - Display of event information (ID, title, date, location)
 * - Checkbox selection for individual events
 * - Bulk selection/deselection capabilities
 * - Real-time selection change notifications via callback interface
 * <p>
 * Selection state is maintained in a HashSet of event IDs, allowing efficient
 * lookup and modification. The adapter notifies the parent fragment when selection
 * changes so the UI can update accordingly (e.g., enable delete button, update count).
 */
public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.ViewHolder> {

    /**
     * Callback interface for notifying when event selection changes.
     * <p>
     * Implementations should update UI elements that depend on selection state,
     * such as the selected count text or delete button visibility.
     */
    public interface OnSelectionChangedListener {
        /**
         * Called when the set of selected events changes (item selected or deselected).
         */
        void onSelectionChanged();
    }

    private List<Event> events = new ArrayList<>();
    private List<Event> filteredEvents = new ArrayList<>();
    private Context context;
    private Set<String> selectedEventIds = new HashSet<>();
    private OnSelectionChangedListener selectionListener;

    /**
     * Constructs an AdminEventAdapter.
     *
     * @param context The context used for inflating layouts
     */
    public AdminEventAdapter(Context context) {
        this.context = context;
    }

    /**
     * Sets the listener for selection change events.
     *
     * @param listener The listener to be notified when selection changes
     */
    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.selectionListener = listener;
    }

    /**
     * Updates the adapter with a new complete list of events.
     * <p>
     * Clears any existing selection and notifies the adapter of the data change.
     *
     * @param newEvents The new list of events to display
     */
    public void updateEvents(List<Event> newEvents) {
        this.events = newEvents;
        this.filteredEvents = new ArrayList<>(newEvents);
        selectedEventIds.clear();
        notifyDataSetChanged();
    }

    /**
     * Updates the adapter with a filtered list of events.
     * <p>
     * Does not modify the complete events list or clear selection.
     * Used after applying search/filter/sort operations.
     *
     * @param filtered The filtered list of events to display
     */
    public void filterEvents(List<Event> filtered) {
        this.filteredEvents = filtered;
        notifyDataSetChanged();
    }

    /**
     * Returns a copy of the currently selected event IDs.
     *
     * @return A new HashSet containing the IDs of all selected events
     */
    public Set<String> getSelectedEventIds() {
        return new HashSet<>(selectedEventIds);
    }

    /**
     * Clears all event selections and refreshes the display.
     */
    public void clearSelection() {
        selectedEventIds.clear();
        notifyDataSetChanged();
    }

    /**
     * Selects all currently visible (filtered) events and refreshes the display.
     */
    public void selectAll() {
        selectedEventIds.clear();
        for (Event event : filteredEvents) {
            selectedEventIds.add(event.getId());
        }
        notifyDataSetChanged();
    }

    /**
     * Returns the number of currently selected events.
     *
     * @return The count of selected event IDs
     */
    public int getSelectedCount() {
        return selectedEventIds.size();
    }

    /**
     * ViewHolder for event list items.
     * <p>
     * Holds references to the UI components for each event card:
     * checkbox, event ID, title, date, and location.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox eventCheckbox;
        TextView eventId;
        TextView eventTitle;
        TextView eventDate;
        TextView eventLocation;

        /**
         * Constructs a ViewHolder and initializes view references.
         *
         * @param view The root view of the event card layout
         */
        public ViewHolder(View view) {
            super(view);
            eventCheckbox = view.findViewById(R.id.eventCheckbox);
            eventId = view.findViewById(R.id.eventId);
            eventTitle = view.findViewById(R.id.eventTitle);
            eventDate = view.findViewById(R.id.eventDate);
            eventLocation = view.findViewById(R.id.eventLocation);
        }
    }

    /**
     * Creates a new ViewHolder by inflating the event card layout.
     *
     * @param parent The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View (not used, all items have same type)
     * @return A new ViewHolder that holds a View of the event card layout
     */
    @NonNull
    @Override
    public AdminEventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_admin_event_card, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds event data to the ViewHolder and sets up selection listeners.
     * <p>
     * Updates the UI elements with event information and configures:
     * - Checkbox state based on whether the event is selected
     * - Checkbox listener to update selection and notify the callback
     * - Item click listener to toggle checkbox when the card is clicked
     *
     * @param holder The ViewHolder to bind data to
     * @param position The position of the item in the filtered events list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = filteredEvents.get(position);
        EventDescription desc = event.getDescription();
        String eventId = event.getId();

        holder.eventId.setText("Event ID: " + (eventId != null ? eventId : "N/A"));
        holder.eventTitle.setText(desc.getTitle());
        holder.eventDate.setText("Date: " + desc.getStartDate());
        holder.eventLocation.setText("Location: N/A");

        holder.eventCheckbox.setChecked(selectedEventIds.contains(eventId));
        holder.eventCheckbox.setOnCheckedChangeListener(null);
        holder.eventCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedEventIds.add(eventId);
            } else {
                selectedEventIds.remove(eventId);
            }
            // Notify listener of selection change
            if (selectionListener != null) {
                selectionListener.onSelectionChanged();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            holder.eventCheckbox.setChecked(!holder.eventCheckbox.isChecked());
        });
    }

    /**
     * Returns the total number of items in the filtered events list.
     *
     * @return The size of the filteredEvents list
     */
    @Override
    public int getItemCount() {
        return filteredEvents.size();
    }
}
