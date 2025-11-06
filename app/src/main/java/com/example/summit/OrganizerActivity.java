package com.example.summit;

import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class OrganizerActivity extends AppCompatActivity {

    private NavController navController;
    private String deviceId;

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



