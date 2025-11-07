package com.example.summit.model;

import java.time.LocalDate;

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

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
