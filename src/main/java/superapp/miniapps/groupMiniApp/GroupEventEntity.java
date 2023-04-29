package superapp.miniapps.groupMiniApp;

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

	public GroupEventEntity setDate(Date date) {
		this.date = date;
		return this;
	}

	public Location getLocation() {
		return location;
	}

	public GroupEventEntity setLocation(Location location) {
		this.location = location;
		return this;
	}

	public String getGroupId() {
		return groupId;
	}

	public GroupEventEntity setGroupId(String groupId) {
		this.groupId = groupId;
		return this;
	}

	public Map<String, String> getStatus() {
		return status;
	}

	public GroupEventEntity setStatus(Map<String, String> status) {
		this.status = status;
		return this;
	}

	public String getEventName() {
		return eventName;
	}

	public GroupEventEntity setEventName(String eventName) {
		this.eventName = eventName;
		return this;
	}

	public String getEventDescription() {
		return eventDescription;
	}

	public GroupEventEntity setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
		return this;
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
