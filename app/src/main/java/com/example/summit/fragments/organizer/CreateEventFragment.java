package com.example.summit.fragments.organizer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.summit.R;
import com.example.summit.session.Session;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Fragment that allows organizers to create new events.
 * <p>
 * Features:
 * <ul>
 *     <li>Input validation for title, description, capacity, and dates</li>
 *     <li>Poster upload via device gallery or camera</li>
 *     <li>Firebase Storage integration for image hosting</li>
 *     <li>Firestore integration for saving event data</li>
 *     <li>Automatic navigation to event confirmation upon creation</li>
 * </ul>
 *
 * @author
 *  Summit Development Team
 * @version
 *  2.0
 * @since
 *  November 2025
 */
public class CreateEventFragment extends Fragment {

    // UI Elements
    private EditText titleInput, descInput, capacityInput, regStartInput, regEndInput,
            locationInput, eventStartInput, eventEndInput;
    private CheckBox requireLocation;
    private ImageView posterImage;
    private Button btnGallery, btnCamera, btnCreate;

    // Firebase references
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    private Uri imageUri;
    private String currentEventId;

    // Request codes for image selection
    private static final int REQUEST_GALLERY = 1001;
    private static final int REQUEST_CAMERA = 1002;

    /**
     * Default constructor initializes layout binding.
     */
    public CreateEventFragment() {
        super(R.layout.fragment_create_event);
    }

    /**
     * Inflates the fragment view.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_event, container, false);
    }

    /**
     * Initializes the fragment view, sets up input listeners and button actions.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            // Navigate back to Manage Events
            NavHostFragment.findNavController(CreateEventFragment.this)
                    .navigate(R.id.action_createEventFragment_to_manageEvents);
        });


        // Bind views
        titleInput = view.findViewById(R.id.input_title);
        descInput = view.findViewById(R.id.input_description);
        capacityInput = view.findViewById(R.id.input_capacity);
        regStartInput = view.findViewById(R.id.input_reg_start);
        regEndInput = view.findViewById(R.id.input_reg_end);
        locationInput = view.findViewById(R.id.input_location);
        eventStartInput = view.findViewById(R.id.input_event_start);
        eventEndInput = view.findViewById(R.id.input_event_end);
        posterImage = view.findViewById(R.id.image_event_poster);
        btnGallery = view.findViewById(R.id.btn_select_poster);
        btnCamera = view.findViewById(R.id.btn_take_photo);
        btnCreate = view.findViewById(R.id.button_create_event);
        requireLocation = view.findViewById(R.id.require_location_checkbox);

        // Attach date pickers
        regStartInput.setOnClickListener(v -> showDatePicker(regStartInput));
        regEndInput.setOnClickListener(v -> showDatePicker(regEndInput));
        eventStartInput.setOnClickListener(v -> showDatePicker(eventStartInput));
        eventEndInput.setOnClickListener(v -> showDatePicker(eventEndInput));

        // Image selection actions
        btnGallery.setOnClickListener(v -> openGallery());
        btnCamera.setOnClickListener(v -> openCamera());

        // Event creation action
        btnCreate.setOnClickListener(v -> uploadPosterThenCreateEvent());
    }

    /**
     * Displays a date picker dialog for a given input field.
     *
     * @param targetField EditText field to populate with selected date.
     */
    private void showDatePicker(EditText targetField) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formatted = String.format("%d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);
                    targetField.setText(formatted);
                },
                year, month, day
        );
        datePicker.show();
    }

    /**
     * Opens the device gallery to select a poster image.
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    /**
     * Opens the camera app for capturing an event poster.
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    /**
     * Handles the result of the gallery or camera activity.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_GALLERY) {
                imageUri = data.getData();
                posterImage.setImageURI(imageUri);
            } else if (requestCode == REQUEST_CAMERA && data.getExtras() != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                posterImage.setImageBitmap(bitmap);
                imageUri = getImageUri(bitmap);
            }
        }
    }

    /**
     * Converts a captured bitmap into a temporary URI using MediaStore.
     *
     * @param bitmap Bitmap image captured from the camera.
     * @return URI pointing to the stored image.
     */
    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(
                requireContext().getContentResolver(), bitmap, "EventPoster", null);
        return Uri.parse(path);
    }

    /**
     * Uploads the selected or captured image to Firebase Storage.
     * On success, proceeds to create the event with the generated download URL.
     */
    private void uploadPosterThenCreateEvent() {
        if (imageUri == null) {
            // No image selected → use placeholder
            createEvent("");
            return;
        }

        StorageReference ref = storage.getReference()
                .child("event_posters/" + UUID.randomUUID().toString() + ".jpg");

        try {
            InputStream stream = requireContext().getContentResolver().openInputStream(imageUri);
            Log.d("CreateEvent", "Uploading URI: " + imageUri);
            ref.putStream(stream)
                    .addOnSuccessListener(taskSnapshot ->
                            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                                Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                                createEvent(uri.toString());
                            }))
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (FileNotFoundException e) {
            Toast.makeText(getContext(), "File not found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Creates the event in Firestore with all provided data.
     *
     * @param posterUrl Download URL of the event poster.
     */
    private void createEvent(String posterUrl) {
        String title = titleInput.getText().toString().trim();
        String desc = descInput.getText().toString().trim();
        String cap = capacityInput.getText().toString().trim();
        String regStart = regStartInput.getText().toString().trim();
        String regEnd = regEndInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        String eventStart = eventStartInput.getText().toString().trim();
        String eventEnd = eventEndInput.getText().toString().trim();
        Boolean selectedRequiredLocation = requireLocation.isChecked();

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
        eventData.put("posterUrl", posterUrl);
        eventData.put("organizerId", organizerId);
        eventData.put("location", location);
        eventData.put("eventStart", eventStart);
        eventData.put("eventEnd", eventEnd);
        eventData.put("selectedList", new ArrayList<>());
        eventData.put("waitingList", new ArrayList<>());
        eventData.put("declinedList", new ArrayList<>());
        eventData.put("acceptedList", new ArrayList<>());
        eventData.put("requiredLocation", selectedRequiredLocation);

        db.collection("events")
                .add(eventData)
                .addOnSuccessListener(docRef -> {
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
