package com.example.summit.fragments.entrant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.summit.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;


public class QRScannerFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityResultLauncher<ScanOptions> barcodeLauncher;

    /**
     * Default constructor required for Fragment.
     */
    public QRScannerFragment() {
        super(R.layout.fragment_qr_scanner);
    }

    /**
     * Called when the fragment is created.
     * Initializes the barcode scanner launcher.
     *
     * @param savedInstanceState Bundle containing previously saved state
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the barcode scanner launcher
        barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() == null) {
                // User cancelled scanning
                Toast.makeText(getContext(), "Scan cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // Successfully scanned QR code
                String scannedEventId = result.getContents();
                handleScannedEventId(scannedEventId);
            }
        });
    }

    /**
     * Creates and returns the view hierarchy for the fragment.
     *
     * @param inflater The LayoutInflater to inflate views
     * @param container The parent view that the fragment's UI attaches to
     * @param savedInstanceState Bundle containing previously saved state
     * @return The View for the fragment's UI
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_scanner, container, false);
    }

    /**
     * Called after onCreateView returns.
     * Sets up the scan button click listener.
     *
     * @param view The View returned by onCreateView
     * @param savedInstanceState Bundle containing previously saved state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button scanButton = view.findViewById(R.id.button_scan_qr);
        scanButton.setOnClickListener(v -> startQRScanner());
    }

    /**
     * Starts the QR code scanner with custom options.
     * Configures the scanner to use the back camera and shows a prompt message.
     */
    private void startQRScanner() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan an event QR code");
        options.setCameraId(0);  // Use back camera
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(false);
        options.setOrientationLocked(false);

        barcodeLauncher.launch(options);
    }

    /**
     * Handles the scanned event ID by fetching event details from Firebase.
     * If the event exists, navigates to the event details page.
     * If the event doesn't exist, shows an error message.
     *
     * @param eventId The event ID extracted from the scanned QR code
     */
    private void handleScannedEventId(String eventId) {
        // Show loading message
        Toast.makeText(getContext(), "Loading event...", Toast.LENGTH_SHORT).show();

        // Query Firebase for the event
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Event found! Navigate to event details
                        navigateToEventDetails(eventId);
                    } else {
                        // Event not found
                        Toast.makeText(getContext(),
                                "Event not found. Invalid QR code.",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Error fetching event
                    Toast.makeText(getContext(),
                            "Error loading event: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    /**
     * Navigates to the event details fragment with the given event ID.
     * Creates a Bundle with the event ID and uses Navigation Component to navigate.
     *
     * @param eventId The ID of the event to display
     */
    private void navigateToEventDetails(String eventId) {
        // Create bundle with event ID
        Bundle bundle = new Bundle();
        bundle.putString("eventId", eventId);

        // Navigate to event details fragment
        // Replace R.id.action_qrScanner_to_eventDetails with your actual action ID
        try {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_qrScanner_to_eventDetails, bundle);
        } catch (Exception e) {
            // If navigation fails, show event ID to user
            Toast.makeText(getContext(),
                    "Event found! ID: " + eventId,
                    Toast.LENGTH_LONG).show();

            // You can also manually navigate or show event details here
            // For now, just showing the event ID
        }
    }

    /**
     * Alternative method: Display event details directly in a dialog or new activity.
     * Use this if you don't want to use Navigation Component.
     *
     * @param eventId The ID of the event to display
     * @param eventData The document snapshot containing event data
     */
    private void showEventDetailsDialog(String eventId, DocumentSnapshot eventData) {
        // Extract event information
        String title = eventData.getString("title");
        String description = eventData.getString("description");

        // You can create a dialog or start a new activity here
        Toast.makeText(getContext(),
                "Event: " + title + "\n" + description,
                Toast.LENGTH_LONG).show();
    }
}