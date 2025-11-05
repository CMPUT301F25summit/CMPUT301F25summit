package com.example.summit.qr;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.summit.EntrantActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Launches a camera QR scanner and routes to EventDetails upon success.
 * Expected QR payloads:
 *  - summit://event/{eventId}
 *  - https://...&eventId={eventId}
 *  - raw {eventId}
 */
public class ScanQrActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fire up the ZXing embedded scanner
        new IntentIntegrator(this)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                .setPrompt("Align the QR within the frame")
                .setBeepEnabled(true)
                .setOrientationLocked(true)
                .initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result == null || result.getContents() == null) {
            // User canceled or no result
            finish();
            return;
        }

        String contents = result.getContents();
        String eventId = parseEventId(contents);
        if (eventId == null || eventId.isEmpty()) {
            // Optionally show a Toast/log here for invalid QR
            finish();
            return;
        }

        // TODO: Update target if your Event Details class/package differs
        Intent i = new Intent(this, EntrantActivity.class);
        i.putExtra("fragment", "event_details");
        i.putExtra("eventId", eventId);
        startActivity(i);
        finish();
    }

    /**
     * Extracts an eventId from common QR formats.
     */
    private @Nullable String parseEventId(String s) {
        if (s == null) return null;

        // Format: summit://event/{eventId}
        final String scheme = "summit://event/";
        if (s.startsWith(scheme)) return s.substring(scheme.length());

        // Format: ...?eventId=XXXX[&...]
        int idx = s.indexOf("eventId=");
        if (idx >= 0) {
            String tail = s.substring(idx + "eventId=".length());
            int amp = tail.indexOf('&');
            return (amp >= 0) ? tail.substring(0, amp) : tail;
        }

        // Fallback: raw ID (alphanumeric/_/-, length ≥ 4)
        if (s.matches("^[A-Za-z0-9_-]{4,}$")) return s;

        return null;
    }
}
