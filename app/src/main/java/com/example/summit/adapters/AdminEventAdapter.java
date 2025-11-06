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

public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.ViewHolder> {

    private List<Event> events = new ArrayList<>();
    private List<Event> filteredEvents = new ArrayList<>();
    private Context context;
    private Set<String> selectedEventIds = new HashSet<>();

    public AdminEventAdapter(Context context) {
        this.context = context;
    }

    public void updateEvents(List<Event> newEvents) {
        this.events = newEvents;
        this.filteredEvents = new ArrayList<>(newEvents);
        selectedEventIds.clear();
        notifyDataSetChanged();
    }

    public void filterEvents(List<Event> filtered) {
        this.filteredEvents = filtered;
        notifyDataSetChanged();
    }

    public Set<String> getSelectedEventIds() {
        return new HashSet<>(selectedEventIds);
    }

    public void clearSelection() {
        selectedEventIds.clear();
        notifyDataSetChanged();
    }

    public void selectAll() {
        selectedEventIds.clear();
        for (Event event : filteredEvents) {
            selectedEventIds.add(event.getId());
        }
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return selectedEventIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox eventCheckbox;
        TextView eventId;
        TextView eventTitle;
        TextView eventDate;
        TextView eventLocation;

        public ViewHolder(View view) {
            super(view);
            eventCheckbox = view.findViewById(R.id.eventCheckbox);
            eventId = view.findViewById(R.id.eventId);
            eventTitle = view.findViewById(R.id.eventTitle);
            eventDate = view.findViewById(R.id.eventDate);
            eventLocation = view.findViewById(R.id.eventLocation);
        }
    }

    @NonNull
    @Override
    public AdminEventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_admin_event_card, parent, false);
        return new ViewHolder(view);
    }

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
        });

        holder.itemView.setOnClickListener(v -> {
            holder.eventCheckbox.setChecked(!holder.eventCheckbox.isChecked());
        });
    }

    @Override
    public int getItemCount() {
        return filteredEvents.size();
    }
}
