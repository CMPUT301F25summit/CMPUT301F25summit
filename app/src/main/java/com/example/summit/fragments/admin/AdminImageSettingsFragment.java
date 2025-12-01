package com.example.summit.fragments.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.summit.R;
import com.example.summit.adapters.AdminPosterAdapter;
import com.example.summit.interfaces.EventPosterLoadCallback;
import com.example.summit.interfaces.ImageDeleteCallback;
import com.example.summit.model.EventPoster;
import com.example.summit.model.Firebase;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for managing image moderation in the admin dashboard.
 * Allows admins to view, preview, and delete event posters.
 */
public class AdminImageSettingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminPosterAdapter adapter;
    private TextView emptyStateText;
    private List<EventPoster> eventPosters = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_image_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.postersRecyclerView);
        emptyStateText = view.findViewById(R.id.emptyStateText);

        // Setup RecyclerView with 2-column grid
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new AdminPosterAdapter(getContext());
        adapter.setOnPosterActionListener(new AdminPosterAdapter.OnPosterActionListener() {
            @Override
            public void onPosterClick(EventPoster poster) {
                showFullImageDialog(poster);
            }

            @Override
            public void onDeleteClick(EventPoster poster) {
                showDeleteConfirmationDialog(poster);
            }
        });
        recyclerView.setAdapter(adapter);

        loadEventPosters();
    }

    /**
     * Loads all event posters from Firebase.
     */
    private void loadEventPosters() {
        Firebase.loadAllEventPosters(new EventPosterLoadCallback() {
            @Override
            public void onPostersLoaded(List<EventPoster> posters) {
                eventPosters = posters;
                adapter.updatePosters(posters);
                updateEmptyState();
            }

            @Override
            public void onLoadFailure(String error) {
                Toast.makeText(getContext(), "Error loading images: " + error,
                        Toast.LENGTH_SHORT).show();
                updateEmptyState();
            }
        });
    }

    /**
     * Updates the UI to show empty state message if no posters exist.
     */
    private void updateEmptyState() {
        if (eventPosters.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Displays a full-size preview of the poster in an AlertDialog.
     *
     * @param poster The EventPoster to preview
     */
    private void showFullImageDialog(EventPoster poster) {
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_full_image, null);

        ImageView fullImageView = dialogView.findViewById(R.id.fullImageView);
        TextView eventTitleText = dialogView.findViewById(R.id.eventTitleText);
        TextView organizerText = dialogView.findViewById(R.id.organizerText);

        eventTitleText.setText("Event: " + poster.getEventTitle());
        organizerText.setText("Organizer: " + poster.getOrganizerName());

        // Load full-resolution image from Base64
        String base64 = poster.getPosterUrl();
        if (base64 != null && !base64.isEmpty()) {
            try {
                byte[] decoded = Base64.decode(base64, Base64.DEFAULT);
                Glide.with(this)
                        .asBitmap()
                        .load(decoded)
                        .placeholder(R.drawable.placeholder_event)
                        .into(fullImageView);
            } catch (Exception e) {
                fullImageView.setImageResource(R.drawable.placeholder_event);
            }
        } else {
            fullImageView.setImageResource(R.drawable.placeholder_event);
        }

        new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Delete", (dialog, which) -> {
                    dialog.dismiss();
                    showDeleteConfirmationDialog(poster);
                })
                .create()
                .show();
    }

    /**
     * Shows a confirmation dialog before deleting a poster.
     *
     * @param poster The EventPoster to delete
     */
    private void showDeleteConfirmationDialog(EventPoster poster) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Image?")
                .setMessage("Remove poster for \"" + poster.getEventTitle() + "\"? " +
                        "This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteEventPoster(poster))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Deletes the poster from Firebase and refreshes the list.
     *
     * @param poster The EventPoster to delete
     */
    private void deleteEventPoster(EventPoster poster) {
        Firebase.deleteEventPoster(poster.getEventId(), new ImageDeleteCallback() {
            @Override
            public void onImageDeleteSuccess() {
                Toast.makeText(getContext(), "Image deleted successfully",
                        Toast.LENGTH_SHORT).show();
                loadEventPosters();
            }

            @Override
            public void onImageDeleteFailure(String error) {
                Toast.makeText(getContext(), "Delete failed: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
