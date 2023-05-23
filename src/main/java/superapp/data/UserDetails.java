package superapp.data;

import java.util.ArrayList;

public class UserDetails {
	private String name;
	private String phoneNum;
	private ArrayList<String> preferences;

	
	public UserDetails() {}
	
	public UserDetails(String name, String phoneNum, ArrayList<String> preferences) {
		super();
		this.name = name;
		this.phoneNum = phoneNum;
		this.preferences = preferences;

	}

	public String getName() {
		return name;
	}

	public UserDetails setName(String name) {
		this.name = name;
		return this;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public UserDetails setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
		return this;
	}

	public ArrayList<String> getPreferences() {
		return preferences;
	}

	public UserDetails setPreferences(ArrayList<String> preferences) {
		this.preferences = preferences;
		return this;
	}


	@Override
	public String toString() {
		return "UserDetails{" +
				"name='" + name + '\'' +
				", phoneNum='" + phoneNum + '\'' +
				", interests=" + preferences +
				'}';
	}
}
