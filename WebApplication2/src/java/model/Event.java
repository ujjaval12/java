package model; // Should match your existing package

import java.util.Date; // Using java.util.Date for compatibility with SQL Date/Timestamp

public class Event {
    private int eventId;
    private String title;
    private Date eventDate; // Can hold java.sql.Date
    private String location; // Added field
    private String description;
    private String imagePath;
    private Date createdAt; // Can hold java.sql.Timestamp

    // Default constructor
    public Event() {
    }

    // Getters and Setters for all fields
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // Optional: toString for debugging
    @Override
    public String toString() {
        return "Event [eventId=" + eventId + ", title=" + title + ", eventDate=" + eventDate + ", location=" + location + "]";
    }
}