package com.example.summit.adapters;

import android.content.Context;
import android.util.Base64;
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

/**
 * A {@link RecyclerView.Adapter} for displaying a list of {@link Event} objects
 * in a card-based layout.
 * <p>
 * This adapter is responsible for inflating the event card layout ({@code R.layout.item_event_card}),
 * binding {@link Event} data to the views, and handling click events on
 * individual items via the {@link OnEventClickListener} interface. It uses
 * the Glide library to load event poster images.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    /**
     * A callback interface to handle click events on an event item
     * in the RecyclerView.
     */
    public interface OnEventClickListener {
        /**
         * Called when an event item view is clicked.
         *
         * @param event The {@link Event} object that was clicked.
         */
        void onEventClick(Event event);
    }

    /**
     * The list of events this adapter displays.
     */
    private List<Event> events = new ArrayList<>();
    /**
     * The application context, used for inflating layouts and Glide.
     */
    private Context context;
    /**
     * The callback listener for item click events.
     */
    private OnEventClickListener listener;

    /**
     * Constructs a new EventAdapter.
     *
     * @param context  The context for layout inflation and Glide operations.
     * @param listener The listener to handle item clicks.
     */
    public EventAdapter(Context context, OnEventClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    /**
     * Updates the adapter's data set with a new list of events and notifies
     * the RecyclerView to refresh the UI.
     *
     * @param newEvents The new {@link List} of {@link Event} objects to display.
     */
    public void updateEvents(List<Event> newEvents) {
        this.events = newEvents;
        notifyDataSetChanged();
    }

    /**
     * A static inner class that describes an item view and caches references
     * to the subviews within it.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitle;
        TextView eventDates;
        ImageView eventPoster;

        /**
         * Constructs a new ViewHolder and finds the view references for the child views.
         *
         * @param view The root view of the item layout (inflated in
         * {@link #onCreateViewHolder(ViewGroup, int)}).
         */
        public ViewHolder(View view) {
            super(view);
            eventTitle = view.findViewById(R.id.eventTitle);
            eventDates = view.findViewById(R.id.eventDates);
            eventPoster = view.findViewById(R.id.eventPoster);
        }
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder}.
     * <p>
     * This inflates the {@code R.layout.item_event_card} layout.
     *
     * @param parent   The ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds the view for an event item.
     */
    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_event_card, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * <p>
     * This method gets the {@link Event} at the given position and binds its data
     * (title, dates, poster) to the {@link ViewHolder}. It also sets the
     * {@link android.view.View.OnClickListener} for the item.
     *
     * @param holder   The ViewHolder to be updated.
     * @param position The position of the item in the data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        EventDescription desc = event.getDescription();

        holder.eventTitle.setText(desc.getTitle());
        holder.eventDates.setText(
                "Registration: " + desc.getRegistrationStart() + " - " + desc.getRegistrationEnd()
        );

        String base64 = event.getDescription().getPosterBase64();

        if (base64 != null && !base64.isEmpty()) {
            byte[] decoded = Base64.decode(base64, Base64.DEFAULT);
            Glide.with(context).asBitmap().load(decoded).into(holder.eventPoster);
        } else {
            holder.eventPoster.setImageResource(R.drawable.placeholder_event);
        }


        holder.itemView.setOnClickListener(v -> listener.onEventClick(event));
    }

    /**
     * Returns the total number of items (events) in the data set held by the adapter.
     *
     * @return The total number of events in the list.
     */
    @Override
    public int getItemCount() {
        return events.size();
    }
}



