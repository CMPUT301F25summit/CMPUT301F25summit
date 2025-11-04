package com.example.summit.fragments.organizer;

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

    private EditText titleInput, descInput, capacityInput, regStartInput, regEndInput;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // QR Code views and data
    private ImageView qrCodeImageView;
    private Button saveQrCodeButton;
    private String currentEventId;
    private Bitmap currentQrCodeBitmap;
    private QrCode currentQrCode;

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

        // Initialize QR code views
        qrCodeImageView = view.findViewById(R.id.qr_code_image);
        saveQrCodeButton = view.findViewById(R.id.button_save_qr_code);

        // Initially hide QR code section
        if (qrCodeImageView != null) {
            qrCodeImageView.setVisibility(View.GONE);
        }
        if (saveQrCodeButton != null) {
            saveQrCodeButton.setVisibility(View.GONE);
            saveQrCodeButton.setOnClickListener(v -> saveQrCodeToFirebase());
        }

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
                    // Event created successfully!
                    currentEventId = docRef.getId();

                    Toast.makeText(getContext(), "Event Created!", Toast.LENGTH_SHORT).show();

                    // Generate QR code for the event
                    generateAndDisplayQRCode(currentEventId);

                    // Optional: Comment this out if you want to show QR code before navigating away
                    // NavHostFragment.findNavController(CreateEventFragment.this)
                    //         .navigateUp();
                })

                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Generates and displays a QR code for the given event ID.
     * The QR code encodes the event ID which can be scanned to view event details.
     *
     * @param eventId The unique identifier of the event
     */
    private void generateAndDisplayQRCode(String eventId) {
        try {
            // Generate QR code bitmap
            currentQrCodeBitmap = QRCodeGenerator.generateQRCode(eventId);

            if (currentQrCodeBitmap != null && qrCodeImageView != null) {
                // Display the QR code
                qrCodeImageView.setImageBitmap(currentQrCodeBitmap);
                qrCodeImageView.setVisibility(View.VISIBLE);

                if (saveQrCodeButton != null) {
                    saveQrCodeButton.setVisibility(View.VISIBLE);
                }

                // Create QrCode object with Base64 data for Firebase storage
                currentQrCode = new QrCode(eventId);
                String base64Data = QRCodeGenerator.bitmapToBase64(currentQrCodeBitmap);
                currentQrCode.setQrCodeData(base64Data);

                // Automatically save QR code to Firebase
                saveQrCodeToFirebase();

                Toast.makeText(getContext(), "QR Code generated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to generate QR code",
                        Toast.LENGTH_LONG).show();
            }

        } catch (IllegalArgumentException e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Saves the generated QR code to Firebase Firestore.
     * Stores the QrCode object including the Base64 encoded image data.
     */
    private void saveQrCodeToFirebase() {
        if (currentQrCode == null || currentEventId == null) {
            return;
        }

        // Save QR code data to Firebase
        db.collection("qrcodes")
                .document(currentEventId)
                .set(currentQrCode)
                .addOnSuccessListener(aVoid -> {
                    // QR code saved successfully - silent save
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save QR code: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}