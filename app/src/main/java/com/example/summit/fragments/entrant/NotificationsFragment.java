package com.example.summit.fragments.entrant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.summit.R;

public class NotificationsFragment extends Fragment {

    // Need to implement logic here for a settings drop down dialogue
    // may also need to be able to delete and star certain notifs
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications_entrant, container, false);
    }
}
