package superapp.data;

import java.util.ArrayList;

public class UserDetails {
	private String name;
	private String phoneNum;
	private ArrayList<String> interests;

	
	public UserDetails() {}
	
	public UserDetails(String name, String phoneNum, ArrayList<String> interests) {
		super();
		this.name = name;
		this.phoneNum = phoneNum;
		this.interests = interests;

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

	public ArrayList<String> getInterests() {
		return interests;
	}

	public UserDetails setInterests(ArrayList<String> interests) {
		this.interests = interests;
		return this;
	}


	@Override
	public String toString() {
		return "UserDetails{" +
				"name='" + name + '\'' +
				", phoneNum='" + phoneNum + '\'' +
				", interests=" + interests +
				'}';
	}
}
