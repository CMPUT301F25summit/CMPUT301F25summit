package com.example.summit.fragments.organizer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.summit.EntrantActivity;
import com.example.summit.MainActivity;
import com.example.summit.OrganizerActivity;
import com.example.summit.R;
import com.example.summit.adapters.EventAdapter;
import com.example.summit.fragments.entrant.ProfileFragment;
import com.example.summit.model.Entrant;
import com.example.summit.model.Event;
import com.example.summit.model.EventDescription;
import com.example.summit.model.Firebase;
import com.example.summit.model.Organizer;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Fragment} displaying the current organizer's profile.
 *
 * It allows the user to view their details, edit them via a dialog,
 * or delete their account. Deleting the account also removes all events
 * created by that organizer.
 */
public class ProfileOrganizerFragment extends Fragment {
    private Button editBtn, deleteBtn;
    private Organizer organizer;
    private FirebaseFirestore db;
    private TextView tvName, tvEmail, tvPhone, tvCity;


    /**
     * Initializes the view, loads the organizer's profile data from Firestore,
     * populates the UI, and sets up listeners for edit and delete buttons.
     *
     * @param inflater The LayoutInflater object.
     * @param container This is the parent view.
     * @param savedInstanceState If non-null, this fragment is re-constructed.
     * @return The fragment's view.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_profile, container, false);
        db = FirebaseFirestore.getInstance();

        editBtn = view.findViewById(R.id.btn_edit);
        deleteBtn = view.findViewById(R.id.btn_delete_account);
        tvName = view.findViewById(R.id.tv_user_name);
        tvEmail = view.findViewById(R.id.tv_user_email);
        tvPhone = view.findViewById(R.id.tv_user_phone);

        OrganizerActivity parent = (OrganizerActivity) requireActivity();
        String deviceId = parent.getDeviceID();

        db.collection("organizers").document(deviceId)
                .get()
                .addOnSuccessListener(document -> {
                    if(document.exists()) {
                        organizer = document.toObject(Organizer.class);
                        if(organizer != null) {
                            tvName.setText(organizer.getName());
                            tvEmail.setText(organizer.getEmail());
                            tvPhone.setText(organizer.getPhone());
                            tvCity.setText("Calgary"); // need get city method
                        }
                    } else {
                        Toast.makeText(requireContext(), "No Organizer found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed to load organizer.", Toast.LENGTH_SHORT).show());

        editBtn.setOnClickListener(v -> showEditProfileDialog());
        deleteBtn.setOnClickListener(v -> confirmDeleteAccount());

        return view;
    }

    /**
     * Displays an {@link AlertDialog} with fields to edit the organizer's profile.
     * Saves the updated profile to Firestore on confirmation.
     */
    private void showEditProfileDialog() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialogue_edit_profile, null);

        EditText eName = dialogView.findViewById(R.id.et_first_name);
        EditText ePhone = dialogView.findViewById(R.id.et_phone);
        EditText eEmail = dialogView.findViewById(R.id.et_email);
        EditText eCity = dialogView.findViewById(R.id.et_city);

        eName.setText(organizer.getName());
        eEmail.setText(organizer.getEmail());
        ePhone.setText(organizer.getPhone());
        eCity.setText("Calgary"); // entrant.getCity

        new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("Edit Profile")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    organizer.setName(eName.getText().toString().trim());
                    organizer.setEmail(eEmail.getText().toString().trim());
                    // entrant.setphone
                    // entrant.setcity

                    Firebase.saveOrganizer(organizer);


                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    /**
     * Displays a confirmation dialog before deleting the account.
     *
     * On confirmation, it deletes all associated events, then deletes the
     * organizer's document from Firestore, and navigates back to {@link MainActivity}.
     */
    private void confirmDeleteAccount() {
        OrganizerActivity parent = (OrganizerActivity) requireActivity();
        String deviceId = parent.getDeviceID();

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    Firebase.loadEvents(events -> {
                        for (Event event : events) {
                            EventDescription desc = event.getDescription();
                            if (desc.getOrganizerId() != null && desc.getOrganizerId().equals(deviceId))
                                Firebase.deleteEvent(event);
                        }

                        Firebase.deleteOrganizer(organizer);

                        Intent intent = new Intent(requireContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    });

                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

}
