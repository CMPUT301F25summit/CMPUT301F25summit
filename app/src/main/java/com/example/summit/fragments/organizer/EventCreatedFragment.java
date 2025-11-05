package com.example.summit.fragments.organizer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.summit.R;
import com.example.summit.utils.QRCodeGenerator;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EventCreatedFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView qrView;
    private String eventId;

    public EventCreatedFragment() {
        super(R.layout.fragment_event_created);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventId = getArguments() != null ? getArguments().getString("eventId") : null;
        if (eventId == null) return;

        qrView = view.findViewById(R.id.img_qr);

        generateQR();

        view.findViewById(R.id.btn_done).setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .popBackStack(R.id.ManageEventsFragment, false)
        );
    }

    private void generateQR() {
        Bitmap qr = QRCodeGenerator.generateQRCode(eventId);
        qrView.setImageBitmap(qr);

        String base64 = QRCodeGenerator.bitmapToBase64(qr);

        Map<String, Object> qrData = new HashMap<>();
        qrData.put("qrCodeData", base64);

        db.collection("qrcodes")
                .document(eventId)
                .set(qrData);
    }
}
