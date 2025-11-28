package com.example.summit.fragments.entrant;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.summit.EntrantActivity;
import com.example.summit.MainActivity;
import com.example.summit.R;
import com.example.summit.model.Entrant;
import com.example.summit.model.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * Profile Fragment for EntrantActivity.
 * <purpose>
 * This fragment belongs solely to the EntrantActivity clsss.
 * The user can navigate to this fragment through the entrant_bottom_nav toolbar.
 * Upon navigation:
 *  - provides a layout view of the users profile (their details, edit/delete account options)
 *  - pulls entrants data from the database by referencing the device id
 *  - we pull the actual entrant object from the EntrantActivity class in order to get its deviceID.
 *  - Entrants can delete their account, upon confirmation it will be removed from the database and user
 *  will be redirected to the login page
 *  - Entrant may also edit their personal information using the edit button in which they must also confirm
 *  any changes before it is saved to the database.
 *
 *  -- Currently Missing
 *  - Entrants should enter their city as a field when logging in or signing up.
 *  - Entrants do not yet have a getCity() method or city attribute to pull data from. It must be hardcoded as of now
 *  - this fragment should have a list of the entrants previous events. Entrants should have a list attribute of all their
 *  previous/active events
 */
// array adapter for users previous events.
// each item should be clickable. If user selects edit, and commits changes
// firebase console should be updated at the same time. same for delete. Except user should be thrown
// to the mainactivity(login screen)

public class ProfileFragment extends Fragment {

    private Button editBtn, deleteBtn;
    private Entrant entrant;
    private FirebaseFirestore db;
    private TextView tvName, tvEmail, tvPhone, tvCity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        db = FirebaseFirestore.getInstance();

        editBtn = view.findViewById(R.id.btn_edit);
        deleteBtn = view.findViewById(R.id.btn_delete_account);
        tvName = view.findViewById(R.id.tv_user_name);
        tvEmail = view.findViewById(R.id.tv_user_email);
        tvPhone = view.findViewById(R.id.tv_user_phone);
        tvCity = view.findViewById(R.id.tv_user_city);

        EntrantActivity parent = (EntrantActivity) requireActivity();
        String deviceId = parent.getDeviceID();


       db.collection("entrants").document(deviceId)
                       .get()
                       .addOnSuccessListener(document -> {
                                if(document.exists()) {
                                    entrant = document.toObject(Entrant.class);
                                    if(entrant != null) {
                                        tvName.setText(entrant.getName());
                                        tvEmail.setText(entrant.getEmail());
                                        tvPhone.setText(entrant.getPhone());
                                        tvCity.setText(entrant.getCity()); // need get city method
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "No Entrant found.", Toast.LENGTH_SHORT).show();
                                }
                               })
                                .addOnFailureListener(e ->
                                               Toast.makeText(requireContext(), "Failed to load entrant.", Toast.LENGTH_SHORT).show());

        editBtn.setOnClickListener(v -> showEditProfileDialog());
        deleteBtn.setOnClickListener(v -> confirmDeleteAccount());

        return view;
    }


    private void showEditProfileDialog() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialogue_edit_profile, null);

        EditText eName = dialogView.findViewById(R.id.et_first_name);
        EditText ePhone = dialogView.findViewById(R.id.et_phone);
        EditText eEmail = dialogView.findViewById(R.id.et_email);
        EditText eCity = dialogView.findViewById(R.id.et_city);

        eName.setText(entrant.getName());
        eEmail.setText(entrant.getEmail());
        ePhone.setText(entrant.getPhone());
        eCity.setText(entrant.getCity());// entrant.getCity

        new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("Edit Profile")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    entrant.setName(eName.getText().toString().trim());
                    entrant.setEmail(eEmail.getText().toString().trim());
                    // entrant.setphone
                    entrant.setCity(eCity.getText().toString().trim());

                    Firebase.saveEntrant(entrant);


                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }



    private void confirmDeleteAccount() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Delete", (dialog, which) -> {
            Firebase.deleteEntrant(entrant);
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

}
