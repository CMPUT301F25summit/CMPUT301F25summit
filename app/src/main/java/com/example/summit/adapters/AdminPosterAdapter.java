package com.example.summit.adapters;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.summit.R;
import com.example.summit.model.EventPoster;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying event posters in a grid layout for admin moderation.
 * Handles poster click events for preview and delete actions.
 */
public class AdminPosterAdapter extends RecyclerView.Adapter<AdminPosterAdapter.ViewHolder> {

    /**
     * Callback interface for poster interactions.
     */
    public interface OnPosterActionListener {
        void onPosterClick(EventPoster poster);
        void onDeleteClick(EventPoster poster);
    }

    private List<EventPoster> posters = new ArrayList<>();
    private Context context;
    private OnPosterActionListener listener;

    public AdminPosterAdapter(Context context) {
        this.context = context;
    }

    public void setOnPosterActionListener(OnPosterActionListener listener) {
        this.listener = listener;
    }

    /**
     * Updates the adapter with a new list of posters.
     *
     * @param newPosters The new list of EventPoster objects to display
     */
    public void updatePosters(List<EventPoster> newPosters) {
        this.posters = newPosters;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_poster, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventPoster poster = posters.get(position);

        holder.eventTitleText.setText(poster.getEventTitle());
        holder.organizerNameText.setText("By: " + poster.getOrganizerName());

        // Load Base64 image using Glide
        String base64 = poster.getPosterUrl();
        if (base64 != null && !base64.isEmpty()) {
            try {
                byte[] decoded = Base64.decode(base64, Base64.DEFAULT);
                Glide.with(context)
                    .asBitmap()
                    .load(decoded)
                    .placeholder(R.drawable.placeholder_event)
                    .into(holder.posterImageView);
            } catch (Exception e) {
                holder.posterImageView.setImageResource(R.drawable.placeholder_event);
            }
        } else {
            holder.posterImageView.setImageResource(R.drawable.placeholder_event);
        }

        // Handle poster click for full preview
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPosterClick(poster);
            }
        });

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(poster);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posters.size();
    }

    /**
     * ViewHolder for poster items, caching view references for performance.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImageView;
        TextView eventTitleText;
        TextView organizerNameText;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.posterImageView);
            eventTitleText = itemView.findViewById(R.id.eventTitleText);
            organizerNameText = itemView.findViewById(R.id.organizerNameText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
