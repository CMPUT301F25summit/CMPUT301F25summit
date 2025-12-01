package com.example.summit.fragments.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.summit.AdminActivity;
import com.example.summit.EntrantActivity;
import com.example.summit.OrganizerActivity;
import com.example.summit.R;
import com.example.summit.model.Admin;
import com.example.summit.model.Entrant;
import com.example.summit.model.Organizer;
import com.example.summit.model.Firebase;
import com.example.summit.session.Session;

/**
 * A {@link Fragment} that allows a new user to select their role.
 *
 * This fragment is displayed after a user enters their details in {@link DetailsFragment}.
 * It requires user details (deviceId, name, email, phone) to be passed as navigation arguments.
 *
 * Based on the role selected (Entrant, Organizer, or Admin), this fragment:
 * 1. Creates the appropriate user model object.
 * 2. Saves the new user object to Firestore using the {@code Firebase} helper.
 * 3. Stores the user object in the global {@link Session} (for Entrant and Admin).
 * 4. Navigates the user to their respective main activity ({@link EntrantActivity},
 * {@link OrganizerActivity}, or {@link AdminActivity}).
 * 5. Finishes the current (login/setup) activity, removing it from the back stack.
 */
public class RoleSelectionFragment extends Fragment {

    /**
     * Inflates the fragment's view and sets up click listeners for the role selection buttons.
     *
     * This method retrieves the user details passed from {@link DetailsFragment}. If the
     * arguments are missing, it displays an error toast. Otherwise, it configures
     * each role button (Entrant, Organizer, Admin) to create the user, save them,
     * and redirect to the correct activity.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.role_selection, container, false);

        Button entrantBtn = view.findViewById(R.id.button_entrant);
        Button organizerBtn = view.findViewById(R.id.button_organizer);
        Button adminBtn = view.findViewById(R.id.button_admin);

        Bundle args = getArguments();
        if (args == null) {
            Toast.makeText(getContext(), "missing user data", Toast.LENGTH_SHORT).show();
            return view;
        }
        String deviceId = args.getString("deviceId");
        String name = args.getString("name");
        String email = args.getString("email");
        String phone = args.getString("phone");

        entrantBtn.setOnClickListener(v -> {
            Entrant e = new Entrant(name, email, deviceId, phone);
            Firebase.saveEntrant(e);
            Session.setEntrant(e);
            Toast.makeText(getContext(), "Signed in as Entrant!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), EntrantActivity.class));
            requireActivity().finish();
        });

        organizerBtn.setOnClickListener(v -> {
            Organizer o = new Organizer(name, email, deviceId, phone);
            Firebase.saveOrganizer(o);
            Session.setOrganizer(o);
            Toast.makeText(getContext(), "Signed in as Organizer!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), OrganizerActivity.class));
            requireActivity().finish();
        });

        // Admin role selection: Creates admin user, saves to Firestore, stores in session,
        // and navigates to AdminActivity with user details for display
        adminBtn.setOnClickListener(v -> {
            Admin a = new Admin(name, email, deviceId, phone);
            Firebase.saveAdmin(a);
            Session.setAdmin(a);
            Toast.makeText(getContext(), "Signed in as Admin!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), AdminActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("phone", phone);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }
}
