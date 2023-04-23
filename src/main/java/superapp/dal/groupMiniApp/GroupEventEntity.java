package superapp.dal.groupMiniApp;

import java.util.Date;
import java.util.Map;

import superapp.logic.boundaries.Location;

public class GroupEventEntity {
	
	private Date date;
	private Location location;
	private String groupId;
	private Map<String, String> status;

	private String eventName;
	private String eventDescription;


	public GroupEventEntity() {
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Map<String, String> getStatus() {
		return status;
	}

	public void setStatus(Map<String, String> status) {
		this.status = status;
	}


	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventDescription() {
		return eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	@Override
	public String toString() {
		return "GroupEventEntity{" +
				"date=" + date +
				", location=" + location +
				", groupId='" + groupId + '\'' +
				", status=" + status +
				", eventName='" + eventName + '\'' +
				", eventDescription='" + eventDescription + '\'' +
				'}';
	}
}
