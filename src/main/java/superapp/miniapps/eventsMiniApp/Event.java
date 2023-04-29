package superapp.miniapps.eventsMiniApp;

import java.util.Date;
import java.util.Map;

import superapp.logic.boundaries.Location;
import superapp.logic.boundaries.UserID;

public class Event {
	
	private UserID userId;
	private Date date;
	private String theme;
	private Location location;
	private String description;
	private Map<String, String> status;
	
	public Event() {
		
	}

	public UserID getUserId() {
		return userId;
	}

	public Event setUserId(UserID userId) {
		this.userId = userId;
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

	public Location getLocation() {
		return location;
	}

	public Event setLocation(Location location) {
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

	public Map<String, String> getStatus() {
		return status;
	}

	public Event setStatus(Map<String, String> status) {
		this.status = status;
		return this;
	}

	@Override
	public String toString() {
		return "Event [userId=" + userId + ", date=" + date + ", theme=" + theme + ", location=" + location
				+ ", description=" + description + ", status=" + status + "]";
	}

}
