package com.example.summit.model;

import java.util.ArrayList;
import java.util.List;

public class Event {

    private String id;
    private EventDescription description;

    private List<String> registeredEntrants;
    private List<String> waitingList;
    private List<String> declinedEntrants;

    public Event() {
        this.registeredEntrants = new ArrayList<>();
        this.waitingList = new ArrayList<>();
        this.declinedEntrants = new ArrayList<>();
    }

    public Event(EventDescription desc) {
        this.description = desc;
        this.registeredEntrants = new ArrayList<>();
        this.waitingList = new ArrayList<>();
        this.declinedEntrants = new ArrayList<>();

    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public EventDescription getDescription() { return description; }
    public EventDescription getEventDescription() { return description; }
    public void setDescription(EventDescription description) { this.description = description; }

    public List<String> getRegisteredEntrants() { return registeredEntrants; }
    public void setRegisteredEntrants(List<String> list) { this.registeredEntrants = list; }

    public List<String> getWaitingList() { return waitingList; }
    public void setWaitingList(List<String> list) { this.waitingList = list; }

    public List<String> getDeclinedEntrants() { return declinedEntrants; }
    public void setDeclinedEntrants(List<String> list) { this.declinedEntrants = list; }
}

