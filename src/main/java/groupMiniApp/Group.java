package groupMiniApp;

import java.util.ArrayList;
import java.util.Map;

import superAppsObjects.User;

public class Group {
	
	private String groupName; 
	private Map<String, User> users;
	
	
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
	

	public Map<String, User> getUsers() {
		return users;
	}

	public void setUsers(Map<String, User> users) {
		this.users = users;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
