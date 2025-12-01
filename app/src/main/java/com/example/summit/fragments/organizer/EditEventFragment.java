package com.example.summit.fragments.organizer;

import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.summit.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditEventFragment extends Fragment {

    private String eventId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ImageView posterImage;
    private EditText titleInput, descInput, capacityInput, regStartInput, regEndInput;
    private Button changePosterBtn, saveBtn;

    private Uri selectedImageUri;
    private String posterBase64 = null;

    private ActivityResultLauncher<String> pickImageLauncher;

    public EditEventFragment() {
        super(R.layout.fragment_edit_event);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventId = getArguments().getString("eventId");

        initViews(view);
        initImagePicker();
        loadEventData();
        setupButtons();
    }

    private void initViews(View view) {
        posterImage = view.findViewById(R.id.edit_event_poster);
        titleInput = view.findViewById(R.id.edit_title);
        descInput = view.findViewById(R.id.edit_description);
        capacityInput = view.findViewById(R.id.edit_capacity);
        regStartInput = view.findViewById(R.id.edit_registration_start);
        regEndInput = view.findViewById(R.id.edit_registration_end);

        changePosterBtn = view.findViewById(R.id.button_change_poster);
        saveBtn = view.findViewById(R.id.button_save_changes);
    }

    private void initImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        posterBase64 = encodeImageToBase64(uri);
                        loadBase64IntoImageView(posterBase64, posterImage);
                        Toast.makeText(getContext(), "Poster updated!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void loadEventData() {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(this::populateFields)
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void populateFields(DocumentSnapshot doc) {
        if (!doc.exists()) return;

        titleInput.setText(doc.getString("title"));
        descInput.setText(doc.getString("description"));
        capacityInput.setText(String.valueOf(doc.getLong("capacity")));

        regStartInput.setText(doc.getString("registrationStart"));
        regEndInput.setText(doc.getString("registrationEnd"));

        posterBase64 = doc.getString("posterBase64");

        if (posterBase64 != null && !posterBase64.isEmpty()) {
            loadBase64IntoImageView(posterBase64, posterImage);
        } else {
            posterImage.setImageResource(R.drawable.placeholder_event);
        }
    }

    private void setupButtons() {
        changePosterBtn.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        saveBtn.setOnClickListener(v -> saveEventChanges());
    }

    private String encodeImageToBase64(Uri uri) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadBase64IntoImageView(String base64, ImageView view) {
        try {
            byte[] decoded = Base64.decode(base64, Base64.DEFAULT);
            Glide.with(this).asBitmap().load(decoded).into(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveEventChanges() {
        Map<String, Object> updates = new HashMap<>();

        updates.put("title", titleInput.getText().toString());
        updates.put("description", descInput.getText().toString());
        updates.put("capacity", Integer.parseInt(capacityInput.getText().toString()));
        updates.put("registrationStart", regStartInput.getText().toString());
        updates.put("registrationEnd", regEndInput.getText().toString());

        if (posterBase64 != null)
            updates.put("posterBase64", posterBase64);

        db.collection("events").document(eventId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Event updated successfully!", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(this).popBackStack();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}

