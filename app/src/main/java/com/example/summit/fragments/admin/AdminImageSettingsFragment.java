package com.example.summit.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.summit.R;

/**
 * Fragment for managing image settings in the admin dashboard.
 * <p>
 * This is currently a placeholder fragment showing "Coming Soon" message.
 * Future functionality will include managing event posters, profile images, and QR codes.
 */
public class AdminImageSettingsFragment extends Fragment {

    /**
     * Creates and returns the view hierarchy for this fragment.
     *
     * @param inflater The LayoutInflater to inflate views
     * @param container The parent view
     * @param savedInstanceState Previous state data
     * @return The root view for the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_image_settings, container, false);
    }
}
