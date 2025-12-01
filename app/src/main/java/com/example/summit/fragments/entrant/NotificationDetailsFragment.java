package com.example.summit.fragments.entrant;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.summit.R;

import java.util.Date;

public class NotificationDetailsFragment extends Fragment {

    public NotificationDetailsFragment() {
        super(R.layout.fragment_notifications_details);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        TextView tvMessage = view.findViewById(R.id.tv_message);
        TextView tvTimestamp = view.findViewById(R.id.tv_timestamp);
        TextView tvStatus = view.findViewById(R.id.tv_status);

        Bundle args = getArguments();
        if (args != null) {
            String message = args.getString("message", "No message");
            long timestamp = args.getLong("timestamp", 0L);
            String status = args.getString("status", "Unknown");

            tvMessage.setText(message);
            tvStatus.setText("Status: " + status);

            if (timestamp != 0L) {
                tvTimestamp.setText("Received: " + new Date(timestamp));
            } else {
                tvTimestamp.setText("Received: N/A");
            }
        }

        btnBack.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );
    }
}
