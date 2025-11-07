package com.example.summit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Main activity for the administrator role.
 * <p>
 * Provides the admin dashboard with bottom navigation to access:
 * - Events management (viewing and deleting events)
 * - User profiles management (placeholder)
 * - Image settings (placeholder)
 * - Organizers management (placeholder)
 * - Notifications (placeholder)
 * <p>
 * Uses Navigation Component with nav_graph_admin for fragment navigation.
 */
public class AdminActivity extends AppCompatActivity {

    /**
     * Initializes the admin dashboard activity.
     * <p>
     * Sets up:
     * - Content view with navigation host and bottom navigation
     * - Welcome toast with admin's name (if provided via Intent)
     * - Bottom navigation connected to NavController for fragment switching
     *
     * @param savedInstanceState Previous state data if activity is being recreated
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");

        if(name != null && !name.isEmpty()) {
            Toast.makeText(this, "Welcome Admin, " + name + "!", Toast.LENGTH_SHORT).show();
        }

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_admin);

        if(navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_admin);
            NavigationUI.setupWithNavController(bottomNav, navController);
        }
    }
}
