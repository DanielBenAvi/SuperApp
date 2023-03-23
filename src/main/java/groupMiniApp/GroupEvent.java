package groupMiniApp;

import java.util.Date;

import objectBoundary.Location;

public class GroupEvent {
	
	private Date date;
	private Location location;
	private String groupId;
	
	public GroupEvent() {
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

	@Override
	public String toString() {
		return "GroupEvent [date=" + date + ", location=" + location + ", groupId=" + groupId + "]";
	}
	
	
}
