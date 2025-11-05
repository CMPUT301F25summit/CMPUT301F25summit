package com.example.summit.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.summit.R;
import com.example.summit.model.Entrant;

import java.util.List;

/**
 * A {@link RecyclerView.Adapter} that adapts a {@link List} of {@link Entrant} objects
 * to be displayed in a {@link RecyclerView}.
 * <p>
 * This adapter is responsible for creating {@link ViewHolder} objects and binding
 * entrant data (name and email) to the views within each ViewHolder.
 */
public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.ViewHolder> {

    /**
     * The list of entrants to be displayed by the adapter.
     */
    private List<Entrant> entrants;

    /**
     * Constructs a new EntrantAdapter.
     *
     * @param entrants The initial list of {@link Entrant} objects to display.
     */
    public EntrantAdapter(List<Entrant> entrants) {
        this.entrants = entrants;
    }

    /**
     * Adds a new entrant to the adapter's data set and notifies the
     * RecyclerView that the data has changed, causing a UI refresh.
     *
     * @param e The {@link Entrant} object to add.
     */
    public void addEntrant(Entrant e) {
        entrants.add(e);
        notifyDataSetChanged();
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type
     * to represent an item.
     * <p>
     * This new ViewHolder is constructed with a new View that is inflated from
     * the {@code R.layout.item_entrant} layout file.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is
     * bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entrant, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * <p>
     * This method gets the {@link Entrant} at the given position and
     * binds its data (name and email) to the {@link ViewHolder}'s TextViews.
     *
     * @param holder   The ViewHolder which should be updated to represent the
     * contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Entrant e = entrants.get(position);
        holder.textName.setText(e.getName());
        holder.textEmail.setText(e.getEmail());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of entrants in the list.
     */
    @Override
    public int getItemCount() {
        return entrants.size();
    }

    /**
     * A static inner class that describes an item view and caches references to
     * the subviews (TextViews) within it.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textEmail;

        /**
         * Constructs a new ViewHolder and finds the view references
         * for the child TextViews.
         *
         * @param itemView The view for a single list item, inflated in
         * {@link #onCreateViewHolder(ViewGroup, int)}.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_name);
            textEmail = itemView.findViewById(R.id.text_email);
        }
    }
}

