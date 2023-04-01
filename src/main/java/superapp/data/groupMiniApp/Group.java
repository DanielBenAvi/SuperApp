package superapp.data.groupMiniApp;

import java.util.Map;

import superapp.logic.boundaries.NewUserBoundary;

public class Group {
	
	private String groupName; 
	private Map<String, NewUserBoundary> users;
	
	
	public Group() {
	}
	
	public Group(String _groupName) {
		super();
		this.groupName = _groupName; 
	}
	
	
	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	

	public Map<String, NewUserBoundary> getUsers() {
		return users;
	}

	public void setUsers(Map<String, NewUserBoundary> users) {
		this.users = users;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
