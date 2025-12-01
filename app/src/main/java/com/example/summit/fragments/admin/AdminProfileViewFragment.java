package com.example.summit.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.summit.R;
import com.example.summit.model.Entrant;
import com.example.summit.model.Organizer;
import com.example.summit.model.UserProfile;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminProfileViewFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";
    private static final String ARG_USER_ROLE = "user_role";

    private String userId;
    private String userRole;
    private TextView tvName, tvEmail, tvPhone, tvCity, tvRole;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static AdminProfileViewFragment newInstance(String userId, String userRole) {
        AdminProfileViewFragment fragment = new AdminProfileViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        args.putString(ARG_USER_ROLE, userRole);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
            userRole = getArguments().getString(ARG_USER_ROLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_profile_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvName = view.findViewById(R.id.tv_profile_name);
        tvEmail = view.findViewById(R.id.tv_profile_email);
        tvPhone = view.findViewById(R.id.tv_profile_phone);
        tvCity = view.findViewById(R.id.tv_profile_city);
        tvRole = view.findViewById(R.id.tv_profile_role);

        loadUserProfile();
    }

    private void loadUserProfile() {
        if (userId == null || userRole == null)
            return;

        String collectionName = "entrants"; // Default
        if ("organizer".equalsIgnoreCase(userRole)) {
            collectionName = "organizers";
        } else if ("admin".equalsIgnoreCase(userRole)) {
            collectionName = "admins";
        }

        db.collection(collectionName).document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        String phone = documentSnapshot.getString("phone");
                        String city = documentSnapshot.getString("city"); // Might be null for organizers

                        tvName.setText(name != null ? name : "N/A");
                        tvEmail.setText(email != null ? email : "N/A");
                        tvPhone.setText(phone != null ? phone : "N/A");
                        tvCity.setText(city != null ? city : "N/A");
                        tvRole.setText(userRole);
                    } else {
                        Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error loading profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
