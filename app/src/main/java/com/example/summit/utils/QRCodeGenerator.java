package com.example.summit.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Base64;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.example.summit.model.Event;
import com.example.summit.model.QrCode;

import java.io.ByteArrayOutputStream;

/**
 * Utility class for generating QR codes for events.
 * This class provides methods to create QR code bitmaps that can be displayed
 * or saved for event identification and scanning purposes.
 *
 * Integrates with the Event and QrCode model classes to provide seamless
 * QR code generation for the Summit event management system.
 *
 * @author Summit Team
 * @version 1.0
 * @since 2025-11-04
 */
public class QRCodeGenerator {

    /**
     * Default width for generated QR codes in pixels.
     */
    private static final int DEFAULT_QR_WIDTH = 512;

    /**
     * Default height for generated QR codes in pixels.
     */
    private static final int DEFAULT_QR_HEIGHT = 512;

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private QRCodeGenerator() {
        throw new UnsupportedOperationException("QRCodeGenerator is a utility class and cannot be instantiated");
    }

    /**
     * Generates a QR code for an Event object.
     * Uses the event's ID to create the QR code with default dimensions.
     *
     * @param event The Event object to generate a QR code for
     * @return A Bitmap containing the QR code image, or null if generation fails
     * @throws IllegalArgumentException if event is null or event ID is null/empty
     */
    public static Bitmap generateQRCodeForEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        return generateQRCode(event.getId());
    }

    /**
     * Generates a QR code for a QrCode object.
     * Uses the QrCode's encoded data (URL if available, otherwise event ID).
     *
     * @param qrCode The QrCode object to generate a bitmap for
     * @return A Bitmap containing the QR code image, or null if generation fails
     * @throws IllegalArgumentException if qrCode is null or has no encoded data
     */
    public static Bitmap generateQRCodeFromQrCode(QrCode qrCode) {
        if (qrCode == null) {
            throw new IllegalArgumentException("QrCode object cannot be null");
        }
        return generateQRCode(qrCode.getEncodedData());
    }

    /**
     * Generates a QR code bitmap from the given event ID with default dimensions.
     *
     * @param eventId The unique identifier for the event to encode in the QR code
     * @return A Bitmap containing the QR code image, or null if generation fails
     * @throws IllegalArgumentException if eventId is null or empty
     */
    public static Bitmap generateQRCode(String eventId) {
        return generateQRCode(eventId, DEFAULT_QR_WIDTH, DEFAULT_QR_HEIGHT);
    }

    /**
     * Generates a QR code bitmap from the given event ID with custom dimensions.
     * The QR code will encode the event ID which can later be scanned to retrieve event details.
     *
     * @param eventId The unique identifier for the event to encode in the QR code
     * @param width The desired width of the QR code image in pixels
     * @param height The desired height of the QR code image in pixels
     * @return A Bitmap containing the QR code image, or null if generation fails
     * @throws IllegalArgumentException if eventId is null or empty, or if width/height are invalid
     */
    public static Bitmap generateQRCode(String eventId, int width, int height) {
        if (eventId == null || eventId.trim().isEmpty()) {
            throw new IllegalArgumentException("Event ID cannot be null or empty");
        }

        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive values");
        }

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(eventId, BarcodeFormat.QR_CODE, width, height);

            return convertBitMatrixToBitmap(bitMatrix);

        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generates a QR code bitmap that encodes a URL to the event details.
     * This is useful for creating scannable codes that directly link to event information.
     *
     * @param eventId The unique identifier for the event
     * @param baseUrl The base URL of your application (e.g., "https://summit.app/event/")
     * @return A Bitmap containing the QR code image with the full URL, or null if generation fails
     * @throws IllegalArgumentException if eventId or baseUrl is null or empty
     */
    public static Bitmap generateQRCodeWithUrl(String eventId, String baseUrl) {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Base URL cannot be null or empty");
        }

        String fullUrl = baseUrl + eventId;
        return generateQRCode(fullUrl);
    }

    /**
     * Converts a BitMatrix to a Bitmap image.
     * Black pixels in the BitMatrix become black in the Bitmap, white pixels become white.
     *
     * @param bitMatrix The BitMatrix to convert, generated by the QR code encoder
     * @return A Bitmap representation of the BitMatrix
     */
    private static Bitmap convertBitMatrixToBitmap(BitMatrix bitMatrix) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }

        return bitmap;
    }

    /**
     * Generates a QR code with custom colors instead of the default black and white.
     *
     * @param eventId The unique identifier for the event to encode in the QR code
     * @param width The desired width of the QR code image in pixels
     * @param height The desired height of the QR code image in pixels
     * @param foregroundColor The color for the QR code pattern (default is black)
     * @param backgroundColor The color for the QR code background (default is white)
     * @return A Bitmap containing the colored QR code image, or null if generation fails
     * @throws IllegalArgumentException if eventId is null or empty, or if width/height are invalid
     */
    public static Bitmap generateColoredQRCode(String eventId, int width, int height,
                                               int foregroundColor, int backgroundColor) {
        if (eventId == null || eventId.trim().isEmpty()) {
            throw new IllegalArgumentException("Event ID cannot be null or empty");
        }

        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive values");
        }

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(eventId, BarcodeFormat.QR_CODE, width, height);

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? foregroundColor : backgroundColor);
                }
            }

            return bitmap;

        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts a Bitmap to a Base64 encoded string for storage in Firebase.
     * This allows the QR code image to be stored in the database.
     *
     * @param bitmap The Bitmap to convert
     * @return A Base64 encoded string representing the bitmap, or null if conversion fails
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts a Base64 encoded string back to a Bitmap.
     * This is used to retrieve QR code images from Firebase.
     *
     * @param base64String The Base64 encoded string to convert
     * @return A Bitmap object, or null if conversion fails
     */
    public static Bitmap base64ToBitmap(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }

        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a complete QrCode object with generated bitmap data for an Event.
     * This method generates the QR code, converts it to Base64, and populates
     * a QrCode object ready to be saved to Firebase.
     *
     * @param event The Event to create a QR code for
     * @return A QrCode object with all fields populated, or null if generation fails
     */
    public static QrCode createQrCodeObjectForEvent(Event event) {
        if (event == null || event.getId() == null) {
            return null;
        }

        // Generate the QR code bitmap
        Bitmap qrBitmap = generateQRCodeForEvent(event);
        if (qrBitmap == null) {
            return null;
        }

        // Create QrCode object
        QrCode qrCode = new QrCode(event.getId());

        // Convert bitmap to Base64 and store
        String base64Data = bitmapToBase64(qrBitmap);
        qrCode.setQrCodeData(base64Data);

        return qrCode;
    }
}