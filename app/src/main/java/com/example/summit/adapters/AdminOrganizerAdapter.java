package com.example.summit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.summit.R;
import com.example.summit.model.Organizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Adapter for displaying organizers with three-dot menu navigation.
 */
public class AdminOrganizerAdapter extends RecyclerView.Adapter<AdminOrganizerAdapter.ViewHolder> {

    public interface OnOrganizerActionListener {
        void onViewProfile(Organizer organizer);
        void onViewNotifications(Organizer organizer);
    }

    private List<Organizer> allOrganizers = new ArrayList<>();
    private List<Organizer> displayedOrganizers = new ArrayList<>();
    private Context context;
    private OnOrganizerActionListener listener;

    public AdminOrganizerAdapter(Context context) {
        this.context = context;
    }

    public void setOnOrganizerActionListener(OnOrganizerActionListener listener) {
        this.listener = listener;
    }

    public void updateOrganizers(List<Organizer> organizers) {
        this.allOrganizers = new ArrayList<>(organizers);
        Collections.sort(this.allOrganizers, (o1, o2) -> {
            String name1 = o1.getName() != null ? o1.getName() : "";
            String name2 = o2.getName() != null ? o2.getName() : "";
            return name1.compareToIgnoreCase(name2);
        });
        this.displayedOrganizers = new ArrayList<>(this.allOrganizers);
        notifyDataSetChanged();
    }

    public void filterOrganizers(String query) {
        displayedOrganizers.clear();
        if (query == null || query.trim().isEmpty()) {
            displayedOrganizers.addAll(allOrganizers);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            for (Organizer org : allOrganizers) {
                String name = org.getName() != null ? org.getName().toLowerCase() : "";
                if (name.contains(lowerQuery)) {
                    displayedOrganizers.add(org);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_admin_organizer_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Organizer organizer = displayedOrganizers.get(position);

        holder.organizerName.setText(organizer.getName() != null ? organizer.getName() : "Unknown");
        holder.organizerEmail.setText(organizer.getEmail() != null ? organizer.getEmail() : "N/A");
        holder.organizerPhone.setText(organizer.getPhone() != null ? organizer.getPhone() : "N/A");

        holder.menuButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.menuButton);
            popup.inflate(R.menu.menu_organizer_item);
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_view_profile) {
                    if (listener != null) listener.onViewProfile(organizer);
                    return true;
                } else if (item.getItemId() == R.id.action_view_notifications) {
                    if (listener != null) listener.onViewNotifications(organizer);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return displayedOrganizers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView organizerName;
        TextView organizerEmail;
        TextView organizerPhone;
        ImageButton menuButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            organizerName = itemView.findViewById(R.id.organizerName);
            organizerEmail = itemView.findViewById(R.id.organizerEmail);
            organizerPhone = itemView.findViewById(R.id.organizerPhone);
            menuButton = itemView.findViewById(R.id.btnOrganizerMenu);
        }
    }
}
