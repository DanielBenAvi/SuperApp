package eventsMiniApp;

import java.util.Date;
import java.util.Map;

import objectBoundary.Location;
import objectBoundary.UserID;
import superAppsObjects.User;

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

	public void setUserId(UserID userId) {
		this.userId = userId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, String> getStatus() {
		return status;
	}

	public void setStatus(Map<String, String> status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Event [userId=" + userId + ", date=" + date + ", theme=" + theme + ", location=" + location
				+ ", description=" + description + ", status=" + status + "]";
	}

}
