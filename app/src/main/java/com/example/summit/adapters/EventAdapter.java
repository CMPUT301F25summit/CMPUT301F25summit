package com.example.summit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.summit.R;
import com.example.summit.model.Event;
import com.example.summit.model.EventDescription;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    private List<Event> events = new ArrayList<>();
    private Context context;
    private OnEventClickListener listener;

    public EventAdapter(Context context, OnEventClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void updateEvents(List<Event> newEvents) {
        this.events = newEvents;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitle;
        TextView eventDates;
        ImageView eventPoster;

        public ViewHolder(View view) {
            super(view);
            eventTitle = view.findViewById(R.id.eventTitle);
            eventDates = view.findViewById(R.id.eventDates);
            eventPoster = view.findViewById(R.id.eventPoster);
        }
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_event_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        EventDescription desc = event.getDescription();

        holder.eventTitle.setText(desc.getTitle());
        holder.eventDates.setText(desc.getStartDate() + " - " + desc.getEndDate());

        Glide.with(context)
                .load(desc.getPosterUrl())
                .placeholder(R.drawable.placeholder_event)
                .into(holder.eventPoster);

        holder.itemView.setOnClickListener(v -> listener.onEventClick(event));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}



