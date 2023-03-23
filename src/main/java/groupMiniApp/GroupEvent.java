package groupMiniApp;

import java.util.Date;
import java.util.Map;

import objectBoundary.Location;

public class GroupEvent {
	
	private Date date;
	private Location location;
	private String groupId;
	private Map<String, String> status;
	
	

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

	public Map<String, String> getStatus() {
		return status;
	}

	public void setStatus(Map<String, String> status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "GroupEvent [date=" + date + ", location=" + location + ", groupId=" + groupId + ", status=" + status
				+ "]";
	}

	
	
}
