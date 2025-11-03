package com.example.summit.fragments.login;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.summit.R;
import com.example.summit.model.Entrant;
import com.example.summit.model.Organizer;
import com.example.summit.session.Session;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeviceIDFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_device_id, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button continueButton = view.findViewById(R.id.continue_button);
        continueButton.setOnClickListener(v -> checkUser());
    }

    private void checkUser() {

        String deviceId = Settings.Secure.getString(
                requireContext().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("entrants")
                .document(deviceId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Entrant e = doc.toObject(Entrant.class);
                        Session.setEntrant(e);
                        NavHostFragment.findNavController(this)
                                .navigate(R.id.action_DeviceIDFragment_to_SearchForEventsFragment);
                    } else {
                        db.collection("organizers")
                                .document(deviceId)
                                .get()
                                .addOnSuccessListener(doc2 -> {
                                    if (doc2.exists()) {
                                        Organizer o = doc2.toObject(Organizer.class);
                                        Session.setOrganizer(o);
                                        NavHostFragment.findNavController(this)
                                                .navigate(R.id.action_DeviceIDFragment_to_OrganizerDashboardFragment);
                                    } else {
                                        Bundle args = new Bundle();
                                        args.putString("deviceId", deviceId);
                                        NavHostFragment.findNavController(this)
                                                .navigate(R.id.action_DeviceIDFragment_to_DetailsFragment, args);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Login failed, try again", Toast.LENGTH_SHORT).show()
                );
    }
}


