package superapp.miniapps.eventsMiniApp;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import superapp.logic.boundaries.Location;
import superapp.logic.boundaries.UserId;

public class Event {

    private String name;
    private Date date;
    private String theme;
    private String location;
    private String description;
    private String image;
    private String contact;
    private Set<String> attendees;
    private Map<String, String> preferences;

    public Event() {

    }

    public String getName() {
        return name;
    }

    public Event setName(String name) {
        this.name = name;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public Event setDate(Date date) {
        this.date = date;
        return this;
    }

    public String getTheme() {
        return theme;
    }

    public Event setTheme(String theme) {
        this.theme = theme;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public Event setLocation(String location) {
        this.location = location;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Event setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getContact() {
        return contact;
    }

    public Event setContact(String contact) {
        this.contact = contact;
        return this;
    }

    public Set<String> getAttendees() {
        return attendees;
    }

    public Event setAttendees(Set<String> attendees) {
        this.attendees = attendees;
        return this;
    }

    public Map<String, String> getPreferences() {
        return preferences;
    }

    public Event setPreferences(Map<String, String> preferences) {
        this.preferences = preferences;
        return this;
    }

    public String getImage() {
        return image;
    }

    public Event setImage(String image) {
        this.image = image;
        return this;
    }

    @Override
    public String toString() {
        return "Event{" + "name='" + name + '\'' + ", date=" + date + ", theme='" + theme + '\'' + ", location='" + location + '\'' + ", description='" + description + '\'' + ", image='" + image + '\'' + ", contact='" + contact + '\'' + ", attendees=" + attendees + ", preferences=" + preferences + '}';
    }
}
