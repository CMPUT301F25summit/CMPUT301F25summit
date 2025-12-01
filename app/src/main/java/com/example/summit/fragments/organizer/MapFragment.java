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

public class MapFragment extends Fragment {

    private MapView map;
    private FirebaseFirestore db;
    private String eventId;
    private ImageButton backButton;
    private List<GeoPoint> activeMarkers = new ArrayList<>();


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
    private void fillStatusMap(Map<String, String> map, com.google.firebase.firestore.DocumentSnapshot doc, String fieldName, String statusLabel) {
        List<String> list = (List<String>) doc.get(fieldName);
        if (list != null) {
            for (String id : list) {
                map.put(id, statusLabel);
            }
        }
    }


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

    private void addMarker(GeoPoint point, String title, String status) {
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title != null ? title : "Anonymous");
        marker.setSnippet("Status: " + status);

        map.getOverlays().add(marker);

        map.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
}