package com.example.summit.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.summit.R;
import com.example.summit.adapters.NotificationLogAdapter;
import com.example.summit.model.NotificationLogItem;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class OrganizerNotificationHubFragment extends Fragment {

    private static final String ARG_ORGANIZER_ID = "organizer_id";
    private String organizerId;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private NotificationLogAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static OrganizerNotificationHubFragment newInstance(String organizerId) {
        OrganizerNotificationHubFragment fragment = new OrganizerNotificationHubFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORGANIZER_ID, organizerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            organizerId = getArguments().getString(ARG_ORGANIZER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organizer_notification_hub, container, false);
    }

    private Button btnLoadMore;
    private com.google.firebase.firestore.DocumentSnapshot lastVisible;
    private static final int PAGE_SIZE = 20;
    private boolean isLoading = false;
    private List<NotificationLogItem> currentLogs = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recycler_notification_logs);
        emptyView = view.findViewById(R.id.text_empty_log);
        btnLoadMore = view.findViewById(R.id.btn_load_more);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationLogAdapter();
        recyclerView.setAdapter(adapter);

        btnLoadMore.setOnClickListener(v -> loadLogs());

        loadLogs();
    }

    private void loadLogs() {
        if (organizerId == null || isLoading)
            return;
        isLoading = true;

        Query query = db.collection("notification_logs")
                .whereEqualTo("organizerId", organizerId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(PAGE_SIZE);

        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    isLoading = false;
                    if (queryDocumentSnapshots.isEmpty() && currentLogs.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        btnLoadMore.setVisibility(View.GONE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        if (!queryDocumentSnapshots.isEmpty()) {
                            lastVisible = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);

                            List<NotificationLogItem> newLogs = queryDocumentSnapshots
                                    .toObjects(NotificationLogItem.class);
                            currentLogs.addAll(newLogs);
                            adapter.setLogs(currentLogs);

                            // Show/Hide Load More button
                            if (queryDocumentSnapshots.size() < PAGE_SIZE) {
                                btnLoadMore.setVisibility(View.GONE);
                            } else {
                                btnLoadMore.setVisibility(View.VISIBLE);
                            }
                        } else {
                            btnLoadMore.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    isLoading = false;
                    Toast.makeText(getContext(), "Error loading logs: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
