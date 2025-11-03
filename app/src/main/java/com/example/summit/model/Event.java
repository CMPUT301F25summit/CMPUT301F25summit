package com.example.summit.model;

public class Event {

    private String id;
    private EventDescription description;
    public WaitingList waitingList;

    public Event(EventDescription desc) {
        this.description = desc;
        this.waitingList = new WaitingList();
    }
    //will let us do signup.joinEvent(entrant, event.getWaitingList());


    public String getId() {return id; }
    public void setId(String id) {this.id = id; }
    public EventDescription getDescription() { return description; }
    public WaitingList getWaitingList() { return waitingList; }
}
