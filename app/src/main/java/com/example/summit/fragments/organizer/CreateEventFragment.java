package com.example.summit.fragments.organizer;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.summit.R;
import com.example.summit.model.QrCode;
import com.example.summit.session.Session;
import com.example.summit.utils.QRCodeGenerator;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Fragment for creating new events with QR code generation.
 * Allows organizers to create events and automatically generates
 * a unique QR code for each event.
 *
 * @author Summit Team
 * @version 1.0
 * @since 2025-11-04
 */
public class CreateEventFragment extends Fragment {

    private EditText titleInput, descInput, capacityInput, regStartInput, regEndInput, locationInput, eventStartInput, eventEndInput;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String currentEventId;


    public CreateEventFragment() {
        super(R.layout.fragment_create_event);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_event, container, false);
    }

    private void showDatePicker(EditText targetField) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Month +1 because January = 0
                    String formatted = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    targetField.setText(formatted);
                },
                year, month, day
        );

        datePicker.show();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleInput = view.findViewById(R.id.input_title);
        descInput = view.findViewById(R.id.input_description);
        capacityInput = view.findViewById(R.id.input_capacity);
        regStartInput = view.findViewById(R.id.input_reg_start);
        regEndInput = view.findViewById(R.id.input_reg_end);
        locationInput = view.findViewById(R.id.input_location);
        eventStartInput = view.findViewById(R.id.input_event_start);
        eventEndInput = view.findViewById(R.id.input_event_end);

        regStartInput.setOnClickListener(v -> showDatePicker(regStartInput));
        regEndInput.setOnClickListener(v -> showDatePicker(regEndInput));
        eventStartInput.setOnClickListener(v -> showDatePicker(eventStartInput));
        eventEndInput.setOnClickListener(v -> showDatePicker(eventEndInput));




        Button createBtn = view.findViewById(R.id.button_create_event);
        createBtn.setOnClickListener(v -> createEvent());
    }

    /**
     * Creates a new event and generates a QR code for it.
     * Validates input, saves to Firebase, and generates QR code on success.
     */
    private void createEvent() {

        String title = titleInput.getText().toString().trim();
        String desc = descInput.getText().toString().trim();
        String cap = capacityInput.getText().toString().trim();
        String regStart = regStartInput.getText().toString().trim();
        String regEnd = regEndInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        String eventStart = eventStartInput.getText().toString().trim();
        String eventEnd = eventEndInput.getText().toString().trim();

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
        eventData.put("location", location);
        eventData.put("eventStart", eventStart);
        eventData.put("eventEnd", eventEnd);
        eventData.put("registeredEntrants", new ArrayList<>());
        eventData.put("waitingList", new ArrayList<>());
        eventData.put("declinedEntrants", new ArrayList<>());


        db.collection("events")
                .add(eventData)
                .addOnSuccessListener(docRef -> {
                    // Event created successfully!
                    currentEventId = docRef.getId();

                    Bundle args = new Bundle();
                    args.putString("eventId", currentEventId);

                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_createEvent_to_eventCreated, args);

                })

                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}