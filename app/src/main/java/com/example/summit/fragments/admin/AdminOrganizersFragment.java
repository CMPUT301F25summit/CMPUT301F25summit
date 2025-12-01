package com.example.summit.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.summit.adapters.AdminOrganizerAdapter;
import com.example.summit.model.Firebase;
import com.example.summit.R;

/**
 * Fragment for managing organizers in the admin dashboard.
 * <p>
 * This is currently a placeholder fragment showing "Coming Soon" message.
 * Future functionality will include viewing and managing all event organizers.
 */
public class AdminOrganizersFragment extends Fragment implements AdminOrganizerAdapter.OnOrganizerActionListener {

    private RecyclerView recyclerView;
    private AdminOrganizerAdapter adapter;
    private TextView emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_organizers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_organizers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AdminOrganizerAdapter(getContext());
        adapter.setOnOrganizerActionListener(this);
        recyclerView.setAdapter(adapter);

        loadOrganizers();
    }

    private void loadOrganizers() {
        Firebase.loadAllOrganizersRealtime(new com.example.summit.interfaces.OrganizerLoadCallback() {
            @Override
            public void onOrganizersLoaded(java.util.List<com.example.summit.model.Organizer> organizers) {
                adapter.updateOrganizers(organizers);
            }

            @Override
            public void onLoadFailure(String error) {
                if (getContext() != null) {
                    android.widget.Toast.makeText(getContext(), "Error loading organizers: " + error,
                            android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onViewProfile(com.example.summit.model.Organizer organizer) {
        Bundle args = new Bundle();
        args.putString("user_id", organizer.getDeviceId());
        args.putString("user_role", "organizer");
        androidx.navigation.Navigation.findNavController(recyclerView)
                .navigate(R.id.action_adminOrganizersFragment_to_adminProfileViewFragment, args);
    }

    @Override
    public void onViewNotifications(com.example.summit.model.Organizer organizer) {
        Bundle args = new Bundle();
        args.putString("organizer_id", organizer.getDeviceId());
        androidx.navigation.Navigation.findNavController(recyclerView)
                .navigate(R.id.action_adminOrganizersFragment_to_organizerNotificationHubFragment, args);
    }
}
