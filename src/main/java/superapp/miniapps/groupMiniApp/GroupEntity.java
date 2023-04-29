package superapp.miniapps.groupMiniApp;

import java.util.Map;

import superapp.logic.boundaries.NewUserBoundary;

public class GroupEntity {
	
	private String groupName; 
	private Map<String, NewUserBoundary> users;
	
	public GroupEntity() {
	}
	
	public GroupEntity(String _groupName) {
		this.groupName = _groupName; 
	}

	public String getGroupName() {
		return groupName;
	}

	public GroupEntity setGroupName(String groupName) {
		this.groupName = groupName;
		return this;
	}

	public Map<String, NewUserBoundary> getUsers() {
		return users;
	}

	public GroupEntity setUsers(Map<String, NewUserBoundary> users) {
		this.users = users;
		return this;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
