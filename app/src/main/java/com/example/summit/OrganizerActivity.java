package com.example.summit;

import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Main {@link AppCompatActivity} for the "Organizer" section of the application.
 *
 * This activity hosts the organizer's navigation graph (via a {@link NavHostFragment}),
 * sets up the {@link BottomNavigationView} for top-level navigation,
 * and manages a {@link FloatingActionButton} for creating new events.
 * It also retrieves and provides the unique device ID.
 */

public class OrganizerActivity extends AppCompatActivity {

    private NavController navController;
    private String deviceId;

    /**
     * Initializes the activity, sets the content view, and configures navigation.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in {@link #onSaveInstanceState(Bundle)}.
     * Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_organizer);
        navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_organizer);
        NavigationUI.setupWithNavController(bottomNav, navController);

        FloatingActionButton fab = findViewById(R.id.fab_add_event);

        fab.setOnClickListener(v ->
                navController.navigate(R.id.action_manageEvents_to_createEvent)
        );

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.ManageEventsFragment) {
                fab.show();
            } else {
                fab.hide();
            }
        });
    }

    public String getDeviceID() {
        return deviceId;
    }
}



