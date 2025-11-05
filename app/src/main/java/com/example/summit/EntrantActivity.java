package com.example.summit;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EntrantActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");

        if(name != null && !name.isEmpty()) {
            Toast.makeText(this, "Welcome, " + name + "!", Toast.LENGTH_SHORT).show();
        }

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_entrant);

        if(navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
            NavigationUI.setupWithNavController(bottomNav, navController);
            
            if (intent != null && "event_details".equals(intent.getStringExtra("fragment"))) {
                String eventId = intent.getStringExtra("eventId");
                if (eventId != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("eventId", eventId);
                    navController.navigate(R.id.eventDetailsFragment, bundle);
                }
            }
        }
    }
}
