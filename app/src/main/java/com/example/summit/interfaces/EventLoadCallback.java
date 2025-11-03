package com.example.summit.interfaces;

import com.example.summit.model.EventDescription;

import java.util.List;

public interface EventLoadCallback {
    void onEventsLoaded(List<EventDescription> events);
}

