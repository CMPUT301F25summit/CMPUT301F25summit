package com.example.summit.fragments.organizer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
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

import com.bumptech.glide.Glide;
import com.example.summit.R;
import com.example.summit.session.Session;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateEventFragment extends Fragment {

   
    // UI Elements
    private EditText titleInput, descInput, capacityInput, regStartInput, regEndInput,
            locationInput, eventStartInput, eventEndInput;
    private CheckBox requireLocation;
    private ImageView posterImage;
    private Button btnGallery, btnCamera, btnCreate;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Uri imageUri;
    private String posterBase64 = null;

    private static final int REQUEST_GALLERY = 1001;
    private static final int REQUEST_CAMERA = 1002;

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

        ImageButton backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_createEventFragment_to_manageEvents)
        );

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

        regStartInput.setOnClickListener(v -> showDatePicker(regStartInput));
        regEndInput.setOnClickListener(v -> showDatePicker(regEndInput));
        eventStartInput.setOnClickListener(v -> showDatePicker(eventStartInput));
        eventEndInput.setOnClickListener(v -> showDatePicker(eventEndInput));

        btnGallery.setOnClickListener(v -> openGallery());
        btnCamera.setOnClickListener(v -> openCamera());
        btnCreate.setOnClickListener(v -> createEvent());
    }

    private void showDatePicker(EditText targetField) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                getContext(),
                (view, y, m, d) -> targetField.setText(String.format("%d-%02d-%02d", y, m + 1, d)),
                year, month, day
        );
        datePicker.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_GALLERY) {
                imageUri = data.getData();
                posterBase64 = encodeImageUriToBase64(imageUri);
                loadBase64IntoImage(posterBase64);
            } else if (requestCode == REQUEST_CAMERA && data.getExtras() != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                posterBase64 = encodeBitmapToBase64(bitmap);
                loadBase64IntoImage(posterBase64);
            }
        }
    }

    private String encodeImageUriToBase64(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String encodeBitmapToBase64(Bitmap bitmap) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            byte[] bytes = stream.toByteArray();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadBase64IntoImage(String base64) {
        try {
            byte[] decoded = Base64.decode(base64, Base64.DEFAULT);
            Glide.with(this).asBitmap().load(decoded).into(posterImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createEvent() {
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
                TextUtils.isEmpty(cap) || TextUtils.isEmpty(regStart) ||
                TextUtils.isEmpty(regEnd)) {

            Toast.makeText(getContext(), "Fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        String organizerId = Session.getOrganizer().getDeviceId();

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", title);
        eventData.put("description", desc);
        eventData.put("capacity", Integer.parseInt(cap));
        eventData.put("registrationStart", regStart);
        eventData.put("registrationEnd", regEnd);
        eventData.put("location", location);
        eventData.put("eventStart", eventStart);
        eventData.put("eventEnd", eventEnd);
        eventData.put("posterBase64", posterBase64);
        eventData.put("organizerId", organizerId);

        // initialize empty lists
        eventData.put("waitingList", new ArrayList<>());
        eventData.put("selectedList", new ArrayList<>());
        eventData.put("acceptedList", new ArrayList<>());
        eventData.put("requiredLocation", selectedRequiredLocation);

        db.collection("events")
                .add(eventData)
                .addOnSuccessListener(docRef -> {
                    Bundle args = new Bundle();
                    args.putString("eventId", docRef.getId());
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_createEvent_to_eventCreated, args);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
