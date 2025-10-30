package com.example.summit.model;

public class Event {

    private EventDescription description;
    public WaitingList waitingList;

    public Event(EventDescription desc) {
        this.description = desc;
        this.waitingList = new WaitingList();
    }
    //will let us do signup.joinEvent(entrant, event.getWaitingList());

    public EventDescription getDescription() { return description; }
    public WaitingList getWaitingList() { return waitingList; }
}
