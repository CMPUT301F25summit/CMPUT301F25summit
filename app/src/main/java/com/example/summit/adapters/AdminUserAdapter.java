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
import com.example.summit.model.UserProfile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * adapter for displaying user profiles in the admin dashboard with pagination support.
 * <p>
 * This adapter manages a list of UserProfile objects, supports multi-selection via checkboxes,
 * and implements pagination to display users in batches (20 users per page).
 * <p>
 * Pattern: Mirrors AdminEventAdapter with added pagination functionality.
 */
public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {

    /**
     * Callback interface for selection change notifications.
     */
    public interface OnSelectionChangedListener {
        void onSelectionChanged();
    }

    private List<UserProfile> allUsers = new ArrayList<>();
    private List<UserProfile> displayedUsers = new ArrayList<>();
    private Context context;
    private Set<String> selectedUserIds = new HashSet<>();
    private OnSelectionChangedListener selectionListener;
    private static final int PAGE_SIZE = 20;
    private int currentlyDisplayed = PAGE_SIZE;

    /**
     * Constructs an AdminUserAdapter.
     *
     * @param context The context for inflating layouts
     */
    public AdminUserAdapter(Context context) {
        this.context = context;
    }

    /**
     * Sets the listener for selection change events.
     *
     * @param listener The listener to notify when selection changes
     */
    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.selectionListener = listener;
    }

    /**
     * Updates the complete user list and resets pagination.
     * Clears all selections.
     *
     * @param newUsers The new list of users to display
     */
    public void updateUsers(List<UserProfile> newUsers) {
        this.allUsers = new ArrayList<>(newUsers);
        currentlyDisplayed = Math.min(PAGE_SIZE, allUsers.size());
        if (currentlyDisplayed > 0) {
            displayedUsers = new ArrayList<>(allUsers.subList(0, currentlyDisplayed));
        } else {
            displayedUsers = new ArrayList<>();
        }
        selectedUserIds.clear();
        notifyDataSetChanged();
    }

    /**
     * Updates the displayed users based on filter results and resets pagination.
     *
     * @param filtered The filtered list of users to display
     */
    public void filterUsers(List<UserProfile> filtered) {
        this.allUsers = new ArrayList<>(filtered);
        currentlyDisplayed = Math.min(PAGE_SIZE, allUsers.size());
        if (currentlyDisplayed > 0) {
            displayedUsers = new ArrayList<>(allUsers.subList(0, currentlyDisplayed));
        } else {
            displayedUsers = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    /**
     * Loads the next page of users (up to PAGE_SIZE more users).
     */
    public void loadMore() {
        int remaining = allUsers.size() - currentlyDisplayed;
        if (remaining > 0) {
            int toLoad = Math.min(PAGE_SIZE, remaining);
            int oldSize = displayedUsers.size();
            displayedUsers.addAll(allUsers.subList(currentlyDisplayed, currentlyDisplayed + toLoad));
            currentlyDisplayed += toLoad;
            notifyItemRangeInserted(oldSize, toLoad);
        }
    }

    /**
     * Checks if there are more users to load.
     *
     * @return true if there are more users beyond the currently displayed ones
     */
    public boolean hasMore() {
        return currentlyDisplayed < allUsers.size();
    }

    /**
     * Gets pagination information text.
     *
     * @return String showing "Showing X of Y" where X is displayed users and Y is total
     */
    public String getPaginationText() {
        return "Showing " + displayedUsers.size() + " of " + allUsers.size();
    }

    /**
     * Selects all currently displayed users.
     */
    public void selectAll() {
        selectedUserIds.clear();
        for (UserProfile user : displayedUsers) {
            selectedUserIds.add(user.getDeviceId());
        }
        notifyDataSetChanged();
        if (selectionListener != null) {
            selectionListener.onSelectionChanged();
        }
    }

    /**
     * Clears all selections.
     */
    public void clearSelection() {
        selectedUserIds.clear();
        notifyDataSetChanged();
        if (selectionListener != null) {
            selectionListener.onSelectionChanged();
        }
    }

    /**
     * Gets the set of selected user device IDs.
     *
     * @return A copy of the selected user IDs set
     */
    public Set<String> getSelectedUserIds() {
        return new HashSet<>(selectedUserIds);
    }

    /**
     * Gets the list of selected UserProfile objects.
     *
     * @return List of selected UserProfiles
     */
    public List<UserProfile> getSelectedUsers() {
        List<UserProfile> selected = new ArrayList<>();
        for (UserProfile user : displayedUsers) {
            if (selectedUserIds.contains(user.getDeviceId())) {
                selected.add(user);
            }
        }
        return selected;
    }

    /**
     * Gets the count of selected users.
     *
     * @return The number of selected users
     */
    public int getSelectedCount() {
        return selectedUserIds.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_admin_user_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserProfile user = displayedUsers.get(position);
        String deviceId = user.getDeviceId();

        // Set user information
        holder.userName.setText(user.getName() != null ? user.getName() : "Unknown");
        holder.userCity.setText("City: " + (user.getCity() != null ? user.getCity() : "N/A"));
        holder.userRole.setText("Role: " + (user.getRole() != null ? user.getRole() : "Unknown"));

        // Set checkbox state
        holder.userCheckbox.setChecked(selectedUserIds.contains(deviceId));

        // Remove old listener to prevent unwanted triggers
        holder.userCheckbox.setOnCheckedChangeListener(null);

        // Set checkbox listener
        holder.userCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedUserIds.add(deviceId);
            } else {
                selectedUserIds.remove(deviceId);
            }
            if (selectionListener != null) {
                selectionListener.onSelectionChanged();
            }
        });

        // Item click toggles checkbox
        holder.itemView.setOnClickListener(v -> {
            holder.userCheckbox.setChecked(!holder.userCheckbox.isChecked());
        });
    }

    @Override
    public int getItemCount() {
        return displayedUsers.size();
    }

    /**
     * ViewHolder for user profile items.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox userCheckbox;
        TextView userName;
        TextView userCity;
        TextView userRole;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userCheckbox = itemView.findViewById(R.id.userCheckbox);
            userName = itemView.findViewById(R.id.userName);
            userCity = itemView.findViewById(R.id.userCity);
            userRole = itemView.findViewById(R.id.userRole);
        }
    }
}
