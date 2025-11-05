package com.example.summit.fragments.entrant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.summit.R;

/**
 * Fragment for searching and browsing events.
 * Allows entrants to search for events and scan QR codes to view event details.
 */
public class SearchForEventsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_searchforevents, container, false);
    }

    /**
     * Called after onCreateView returns.
     * Sets up the QR scanner button click listener.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup QR Scanner button
        Button scanQrButton = view.findViewById(R.id.button_scan_qr);
        scanQrButton.setOnClickListener(v -> {
            // Navigate to QR Scanner Fragment
            NavHostFragment.findNavController(this)
                    .navigate(R.id.qrScannerFragment);
        });
    }
}