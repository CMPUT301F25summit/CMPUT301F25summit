package com.example.summit; // Or your app's package name

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.example.summit.interfaces.DeleteCallback;
import com.example.summit.interfaces.EventLoadCallback;
import com.example.summit.model.Admin;
import com.example.summit.model.Entrant;
import com.example.summit.model.Event;
import com.example.summit.model.EventDescription;
import com.example.summit.model.Firebase;
import com.example.summit.model.Organizer;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for the static Firebase.java utility class.
 * These tests run against the Firebase Local Emulator Suite and do NOT test UI.
 * They run in the `androidTest` source set.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class FirebaseIntegrationTest {

    static int firestorePort = 8080;
    static String androidLocalhost = "10.0.2.2";

    /**
     * Connects the app to the local Firebase emulator ONCE before any tests are run.
     */
    @BeforeClass
    public static void setup() {
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, firestorePort);
    }

    /**
     * Seeds the database with dummy data BEFORE EACH TEST.
     * This ensures every test starts with a clean, known state.
     */
    @Before
    public void seedDatabase() throws ExecutionException, InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Entrant entrant1 = new Entrant("Test Entrant", "test", "test-entrant-1", "123");

        Organizer organizer1 = new Organizer("Test Organizer", "test", "test-organizer-1", "123");

        Admin admin1 = new Admin("Test Admin", "test", "test-admin-1", "123");
        admin1.setName("Test Admin");

        EventDescription eventDesc1 = new EventDescription();
        eventDesc1.setTitle("Test Event 1");

        EventDescription eventDesc2 = new EventDescription();
        eventDesc2.setTitle("Test Event 2");

        List<Task<Void>> tasks = Arrays.asList(
                db.collection("entrants").document("test-entrant-1").set(entrant1),
                db.collection("organizers").document("test-organizer-1").set(organizer1),
                db.collection("admins").document("test-admin-1").set(admin1),
                db.collection("events").document("event-id-1").set(eventDesc1),
                db.collection("qrcodes").document("event-id-1").set(new HashMap<>()), // Add dummy QR
                db.collection("events").document("event-id-2").set(eventDesc2),
                db.collection("qrcodes").document("event-id-2").set(new HashMap<>())  // Add dummy QR
        );

        Tasks.await(Tasks.whenAll(tasks));
        Log.i("TestSetup", "Database seeding complete.");
    }

    /**
     * Clears all data from the Firestore emulator AFTER EACH TEST.
     * This prevents tests from interfering with each other.
     */
    @After
    public void tearDown() {
        String projectId = "summit-4de72";

        URL url = null;
        try {
            url = new URL("http://" + androidLocalhost + ":" + firestorePort +
                    "/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
        } catch (MalformedURLException exception) {
            Log.e("URL Error", Objects.requireNonNull(exception.getMessage()));
        }

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            int response = urlConnection.getResponseCode();
            Log.i("Response Code", "Emulator Clear Response: " + response);
        } catch (IOException exception) {
            Log.e("IO Error", Objects.requireNonNull(exception.getMessage()));
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private FirebaseFirestore getDb() {
        return FirebaseFirestore.getInstance();
    }

    @Test
    public void testSaveEntrant() throws ExecutionException, InterruptedException {
        Entrant newEntrant = new Entrant("entrant-new");
        newEntrant.setName("New Guy");
        
        Firebase.saveEntrant(newEntrant);

        Thread.sleep(500);

        DocumentSnapshot doc = Tasks.await(getDb().collection("entrants").document("entrant-new").get());
        assertTrue("Document should exist", doc.exists());
        assertEquals("New Guy", doc.getString("name"));
    }

    @Test
    public void testSaveOrganizer() throws ExecutionException, InterruptedException {
        Organizer newOrg = new Organizer("New Org", "test", "org-new", "123");

        Firebase.saveOrganizer(newOrg);

        Thread.sleep(500);
        DocumentSnapshot doc = Tasks.await(getDb().collection("organizers").document("org-new").get());
        assertTrue("Document should exist", doc.exists());
        assertEquals("New Org", doc.getString("name"));
    }

    @Test
    public void testSaveAdmin() throws ExecutionException, InterruptedException {
        Admin newAdmin = new Admin("New Admin", "test", "admin-new", "123");
        Firebase.saveAdmin(newAdmin);

        Thread.sleep(500);
        DocumentSnapshot doc = Tasks.await(getDb().collection("admins").document("admin-new").get());
        assertTrue("Document should exist", doc.exists());
        assertEquals("New Admin", doc.getString("name"));
    }

    @Test
    public void testDeleteEntrant() throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = Tasks.await(getDb().collection("entrants").document("test-entrant-1").get());
        assertTrue("Entrant should exist before delete", doc.exists());

        Entrant entrantToDelete = doc.toObject(Entrant.class);
        Firebase.deleteEntrant(entrantToDelete);

        Thread.sleep(500);
        DocumentSnapshot docAfter = Tasks.await(getDb().collection("entrants").document("test-entrant-1").get());
        assertFalse("Document should be deleted", docAfter.exists());
    }

    @Test
    public void testLoadEvents() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<List<Event>> result = new AtomicReference<>();

        Firebase.loadEvents(new EventLoadCallback() {
            @Override
            public void onEventsLoaded(List<Event> events) {
                result.set(events);
                latch.countDown();
            }
        });

        assertTrue("Callback was not invoked in time", latch.await(10, TimeUnit.SECONDS));
        assertNotNull("Events list should not be null", result.get());
        assertEquals("Should have loaded 2 events from seed", 2, result.get().size());
        assertEquals("event-id-1", result.get().get(0).getId());
        assertEquals("Test Event 1", result.get().get(0).getDescription().getTitle());
    }

}