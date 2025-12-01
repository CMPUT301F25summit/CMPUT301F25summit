package com.example.summit.fragments.organizer;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.summit.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@link Fragment} that displays a geographic map of entrants for a specific event.
 * <p>
 * This fragment uses the <b>OSMDroid</b> library to render OpenStreetMap tiles.
 * It retrieves entrant data from Firestore based on the provided Event ID.
 * <p>
 * <b>Key Features:</b>
 * <ul>
 * <li>Fetches entrants from all event lists (Waiting, Accepted, Selected, Declined).</li>
 * <li>Respects user privacy: Only plots entrants who have {@code locationShared = true}.</li>
 * <li>Visualizes status: Markers display the entrant's name and their current status.</li>
 * <li>Auto-Zoom: Automatically adjusts the map camera to fit all valid markers using a bounding box.</li>
 * </ul>
 *
 * <b>Navigation Arguments:</b>
 * <ul>
 * <li>{@code EVENT_ID} (String): The unique ID of the event to visualize.</li>
 * </ul>
 */

public class MapFragment extends Fragment {

    private MapView map;
    private FirebaseFirestore db;
    private String eventId;
    private ImageButton backButton;
    private List<GeoPoint> activeMarkers = new ArrayList<>();

    /**
     * Inflates the layout, initializes the OSMDroid configuration, and sets up the map view.
     * <p>
     * <b>Important:</b> This method loads the OSMDroid configuration using the device's
     * shared preferences. This is required to set a valid User-Agent, preventing the app
     * from being blocked by OpenStreetMap servers.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()));

        backButton = view.findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        map = view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        map.getController().setZoom(12.0);
        map.getController().setCenter(new GeoPoint(53.5461, -113.4938)); // Edmonton coordinates as default

        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            eventId = getArguments().getString("EVENT_ID");
        }

        loadEntrants();

        return view;
    }

    /**
     * Orchestrates the loading of entrant data.
     * <p>
     * Steps:
     * <ol>
     * <li>Fetches the Event document.</li>
     * <li>Aggregates user IDs from 'waitingList', 'acceptedList', 'selectedList', and 'declinedList'.</li>
     * <li>Maps each User ID to their status (e.g., "ID_123" -> "Waiting").</li>
     * <li>Iterates through the map and calls {@link #fetchAndPlotEntrant} for each user.</li>
     * <li>Uses an {@link AtomicInteger} counter to detect when all asynchronous fetches are complete to trigger the zoom.</li>
     * </ol>
     */
    private void loadEntrants() {
        if (eventId == null) return;

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Map<String, String> entrantStatusMap = new HashMap<>();

                        fillStatusMap(entrantStatusMap, document, "acceptedList", "Accepted");
                        fillStatusMap(entrantStatusMap, document, "selectedList", "Selected");
                        fillStatusMap(entrantStatusMap, document, "waitingList", "Waiting");
                        fillStatusMap(entrantStatusMap, document, "declinedList", "Declined");

                        if (!entrantStatusMap.isEmpty()) {

                            AtomicInteger loadedCount = new AtomicInteger(0);
                            int total = entrantStatusMap.size();

                            for (Map.Entry<String, String> entry : entrantStatusMap.entrySet()) {
                                String userId = entry.getKey();
                                String status = entry.getValue();

                                fetchAndPlotEntrant(userId, status, () -> {
                                    if (loadedCount.incrementAndGet() == total) {
                                        zoomToMarkers();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(getContext(), "No entrants with location data found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Helper method to extract a list of IDs from a Firestore document and add them to the status map.
     *
     * @param map         The master map to populate.
     * @param doc         The Event document snapshot.
     * @param fieldName   The name of the array field in Firestore (e.g., "waitingList").
     * @param statusLabel The human-readable status to assign (e.g., "Waiting").
     */
    private void fillStatusMap(Map<String, String> map, com.google.firebase.firestore.DocumentSnapshot doc, String fieldName, String statusLabel) {
        List<String> list = (List<String>) doc.get(fieldName);
        if (list != null) {
            for (String id : list) {
                map.put(id, statusLabel);
            }
        }
    }

    /**
     * Fetches an individual Entrant's profile, verifies privacy settings, and adds a marker.
     *
     * @param entrantId  The document ID of the entrant.
     * @param status     The status of the entrant in relation to the event.
     * @param onComplete A Runnable callback that must be executed when the operation finishes (success or failure).
     */
    private void fetchAndPlotEntrant(String entrantId, String status, Runnable onComplete) {
        db.collection("entrants").document(entrantId).get()
                .addOnSuccessListener(entrantDoc -> {
                    if (entrantDoc.exists()) {

                        Boolean isShared = entrantDoc.getBoolean("locationShared");
                        com.google.firebase.firestore.GeoPoint firebasePoint = entrantDoc.getGeoPoint("location");

                        if (Boolean.TRUE.equals(isShared) && firebasePoint != null) {
                            GeoPoint osmPoint = new GeoPoint(firebasePoint.getLatitude() + (Math.random() - 0.5) * 0.0005, firebasePoint.getLongitude() + (Math.random() - 0.5) * 0.0005);
                            String name = entrantDoc.getString("name");

                            addMarker(osmPoint, name, status);
                            activeMarkers.add(osmPoint);
                        }
                    }
                    onComplete.run();
                })
                .addOnFailureListener(e -> onComplete.run());
    }

    /**
     * Adjusts the map view to fit all currently active markers.
     * <p>
     * If multiple markers exist, it calculates a {@link BoundingBox} containing all points
     * and animates the camera to that box with padding.
     * If only one marker exists, it centers the map on that point.
     */
    private void zoomToMarkers() {
        if (activeMarkers.isEmpty()) return;

        if (activeMarkers.size() == 1) {
            map.getController().setCenter(activeMarkers.get(0));
            map.getController().setZoom(15.0);
        } else {
            BoundingBox bounds = BoundingBox.fromGeoPoints(activeMarkers);

            map.zoomToBoundingBox(bounds, true, 100);
        }

        map.invalidate();
    }

    /**
     * Adds a graphical marker to the OSMDroid map overlay.
     *
     * @param point  The geographic coordinates for the marker.
     * @param name   The name of the entrant (defaults to "Anonymous" if null).
     * @param status The status of the entrant to be displayed in the snippet.
     */
    private void addMarker(GeoPoint point, String title, String status) {
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title != null ? title : "Anonymous");
        marker.setSnippet("Status: " + status);

        map.getOverlays().add(marker);

        map.invalidate();
    }

    /**
     * Resumes the map rendering engine. Required by OSMDroid.
     */
    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    /**
     * Pauses the map rendering engine to save resources. Required by OSMDroid.
     */
    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
}