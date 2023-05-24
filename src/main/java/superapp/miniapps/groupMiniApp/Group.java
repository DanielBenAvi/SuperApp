package superapp.miniapps.groupMiniApp;

import java.util.Map;

import superapp.logic.boundaries.NewUserBoundary;

public class Group {
	
	private String groupName; 
	private Map<String, NewUserBoundary> users;
	
	public Group() {
	}
	
	public Group(String _groupName) {
		this.groupName = _groupName; 
	}

	public String getGroupName() {
		return groupName;
	}

	public Group setGroupName(String groupName) {
		this.groupName = groupName;
		return this;
	}

	public Map<String, NewUserBoundary> getUsers() {
		return users;
	}

	public Group setUsers(Map<String, NewUserBoundary> users) {
		this.users = users;
		return this;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
