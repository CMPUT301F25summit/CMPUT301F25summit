package com.example.summit.fragments.organizer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.summit.R;
import com.example.summit.session.Session;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateEventFragment extends Fragment {

    private EditText titleInput, descInput, capacityInput, regStartInput, regEndInput;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CreateEventFragment() {
        super(R.layout.fragment_create_event);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleInput = view.findViewById(R.id.input_title);
        descInput = view.findViewById(R.id.input_description);
        capacityInput = view.findViewById(R.id.input_capacity);
        regStartInput = view.findViewById(R.id.input_reg_start);
        regEndInput = view.findViewById(R.id.input_reg_end);

        Button createBtn = view.findViewById(R.id.button_create_event);
        createBtn.setOnClickListener(v -> createEvent());
    }

    private void createEvent() {

        String title = titleInput.getText().toString().trim();
        String desc = descInput.getText().toString().trim();
        String cap = capacityInput.getText().toString().trim();
        String regStart = regStartInput.getText().toString().trim();
        String regEnd = regEndInput.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(desc) ||
                TextUtils.isEmpty(cap) || TextUtils.isEmpty(regStart) || TextUtils.isEmpty(regEnd)) {
            Toast.makeText(getContext(), "Fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        String organizerId = Session.getOrganizer().getDeviceId();

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", title);
        eventData.put("description", desc);
        eventData.put("capacity", Integer.parseInt(cap));
        eventData.put("startDate", regStart);
        eventData.put("endDate", regEnd);
        eventData.put("registrationStart", regStart);
        eventData.put("registrationEnd", regEnd);
        eventData.put("posterUrl", "");
        eventData.put("organizerId", organizerId);


        db.collection("events")
                .add(eventData)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(getContext(), "Event Created!", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(CreateEventFragment.this)
                            .navigateUp(); //to make it go back to dashboard
                })

                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
