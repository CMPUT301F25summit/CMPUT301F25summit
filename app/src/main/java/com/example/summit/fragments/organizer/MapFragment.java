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
import java.util.List;
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
                        List<String> waitingListIds = (List<String>) document.get("acceptedList");
                        if (waitingListIds != null && !waitingListIds.isEmpty()) {

                            AtomicInteger loadedCount = new AtomicInteger(0);
                            int total = waitingListIds.size();

                            for (String id : waitingListIds) {
                                fetchAndPlotEntrant(id, () -> {
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

    private void fetchAndPlotEntrant(String entrantId, Runnable onComplete) {
        db.collection("entrants").document(entrantId).get()
                .addOnSuccessListener(entrantDoc -> {
                    if (entrantDoc.exists()) {

                        Boolean isShared = entrantDoc.getBoolean("locationShared");
                        com.google.firebase.firestore.GeoPoint firebasePoint = entrantDoc.getGeoPoint("location");

                        if (Boolean.TRUE.equals(isShared) && firebasePoint != null) {
                            GeoPoint osmPoint = new GeoPoint(firebasePoint.getLatitude(), firebasePoint.getLongitude());
                            String name = entrantDoc.getString("name");

                            addMarker(osmPoint, name);
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

    private void addMarker(GeoPoint point, String title) {
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title != null ? title : "Anonymous");

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