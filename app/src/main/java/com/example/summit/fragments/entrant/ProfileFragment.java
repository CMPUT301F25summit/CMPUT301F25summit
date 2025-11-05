package com.example.summit.fragments.entrant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.summit.R;

public class ProfileFragment extends Fragment {

    // Need to implement button listeners (edit, delete) as well as
    // array adapter for users previous events.
    // each item should be clickable. If user selects edit, and commits changes
    // firebase console should be updated at the same time. same for delete. Except user should be thrown
    // to the mainactivity(login screen)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

}
