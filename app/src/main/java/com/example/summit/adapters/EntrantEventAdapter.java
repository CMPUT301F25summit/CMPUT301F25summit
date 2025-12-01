package com.example.summit.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.summit.R;
import com.example.summit.model.Event;
import com.example.summit.model.EventDescription;

import java.util.ArrayList;
import java.util.List;

public class EntrantEventAdapter extends RecyclerView.Adapter<EntrantEventAdapter.EventViewHolder> {

    private List<Event> events = new ArrayList<>();
    private List<String> statuses = new ArrayList<>();
    private Context context;
    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public EntrantEventAdapter(Context context, OnEventClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void updateEvents(List<Event> newEvents, List<String> newStatuses) {
        this.events.clear();
        this.statuses.clear();
        this.events.addAll(newEvents);
        this.statuses.addAll(newStatuses);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        String status = statuses.get(position);
        EventDescription desc = event.getDescription();

        if (desc != null) {
            holder.tvEventName.setText(desc.getTitle());
            holder.tvEventDate.setText(desc.getEventStart());

            // Set organizer initial (first letter of event title)
            String initial = desc.getTitle() != null && !desc.getTitle().isEmpty()
                    ? desc.getTitle().substring(0, 1).toUpperCase()
                    : "E";
            holder.btnOrganizerInitial.setText(initial);
        }

        holder.tvEventStatus.setText(status);

        // Color code status
        switch (status) {
            case "Selected":
                holder.tvEventStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
                break;
            case "Waitlist":
                holder.tvEventStatus.setTextColor(Color.parseColor("#FF9800")); // Orange
                break;
            case "Declined":
                holder.tvEventStatus.setTextColor(Color.parseColor("#F44336")); // Red
                break;
            default:
                holder.tvEventStatus.setTextColor(Color.parseColor("#757575")); // Gray
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEventClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvEventDate, tvEventStatus;
        Button btnOrganizerInitial;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tv_event_name);
            tvEventDate = itemView.findViewById(R.id.tv_event_date);
            tvEventStatus = itemView.findViewById(R.id.tv_event_status);
            btnOrganizerInitial = itemView.findViewById(R.id.img_organizer_initial);
        }
    }
}