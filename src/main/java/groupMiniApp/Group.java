package groupMiniApp;

import java.util.ArrayList;

import demo.User;

public class Group {
	
	private String groupName; 
	private ArrayList<User> users;
	
	
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
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
