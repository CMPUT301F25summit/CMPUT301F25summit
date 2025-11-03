package com.example.summit.fragments;

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

import com.example.summit.EntrantActivity;
import com.example.summit.R;
import com.example.summit.model.Entrant;
import com.example.summit.model.Organizer;
import com.example.summit.model.Firebase;
import com.example.summit.session.Session;

public class RoleSelectionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.role_selection, container, false);

        Button entrantBtn = view.findViewById(R.id.button_entrant);
        Button organizerBtn = view.findViewById(R.id.button_organizer);

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
            Toast.makeText(getContext(), "Signed in as Organizer!", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_RoleSelectionFragment_to_OrganizerDashboardFragment);
        });

        return view;
    }
}
