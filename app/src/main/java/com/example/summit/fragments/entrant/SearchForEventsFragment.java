package com.example.summit.fragments.entrant;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.summit.R;
import com.example.summit.adapters.EventAdapter;
import com.example.summit.model.Event;
import com.example.summit.model.EventDescription;
import com.example.summit.model.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class SearchForEventsFragment extends Fragment {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private final List<Event> eventList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_searchforevents, container, false);

        recyclerView = view.findViewById(R.id.recycler_recommended_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = FirebaseFirestore.getInstance();

        adapter = new EventAdapter(requireContext(), event -> {
            Bundle args = new Bundle();
            args.putString("eventId", event.getId());
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_SearchForEventsFragment_to_EventDetailsEntrantFragment, args);

            // later need to navigate to EventDetail frag and pass event.getid()
        });

        recyclerView.setAdapter(adapter);
        loadEvents();

        return view;
    }

    public void onResume() {
        super.onResume();
        loadEvents();
    }

    private void loadEvents() {
        Firebase.loadEvents(events -> {
            adapter.updateEvents(events);
        });
    }

}
