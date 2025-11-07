package com.example.summit.model;

import java.time.LocalDate;
import java.util.List;

public class EventDescription {
    private String id;
    private String organizerId;
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private String registrationStart;
    private String registrationEnd;
    private int maxAttendees; //for organizer limits;
    private String posterUrl;   //for event poster image

    private List<String> waitingList;
    private List<String> selectedList;
    private List<String> acceptedList;
    private List<String> declinedList;
    private String location;
    private Long capacity;
    private String eventStart;
    private String eventEnd;

    // Getters and setters for these (can stay empty if unused)
    public List<String> getWaitingList() { return waitingList; }
    public void setWaitingList(List<String> waitingList) { this.waitingList = waitingList; }

    public List<String> getSelectedList() { return selectedList; }
    public void setSelectedList(List<String> selectedList) { this.selectedList = selectedList; }

    public List<String> getAcceptedList() { return acceptedList; }
    public void setAcceptedList(List<String> acceptedList) { this.acceptedList = acceptedList; }

    public List<String> getDeclinedList() { return declinedList; }
    public void setDeclinedList(List<String> declinedList) { this.declinedList = declinedList; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Long getCapacity() { return capacity; }
    public void setCapacity(Long capacity) { this.capacity = capacity; }

    public String getEventStart() { return eventStart; }
    public void setEventStart(String eventStart) { this.eventStart = eventStart; }

    public String getEventEnd() { return eventEnd; }
    public void setEventEnd(String eventEnd) { this.eventEnd = eventEnd; }

    public EventDescription(String title, String description,
                            String startDate, String endDate,
                            String registrationStart, String registrationEnd,
                            int maxAttendees, String posterUrl,
                            String organizerId) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.registrationStart = registrationStart;
        this.registrationEnd = registrationEnd;
        this.maxAttendees = maxAttendees;
        this.posterUrl = posterUrl;
        this.organizerId = organizerId;
    }

    public EventDescription() {} //for firebase deserialization

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getRegistrationStart() { return registrationStart; }
    public void setRegistrationStart(String registrationStart) { this.registrationStart = registrationStart; }

    public String getRegistrationEnd() { return registrationEnd; }
    public void setRegistrationEnd(String registrationEnd) { this.registrationEnd = registrationEnd; }
    
    public int getMaxAttendees() { return maxAttendees; }
    public void setMaxAttendees(int maxAttendees) { this.maxAttendees = maxAttendees; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
