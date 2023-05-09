package superapp.data;

import java.util.ArrayList;

public class UserDetails {
	private String name;
	private String phoneNum;
	private ArrayList<String> interests;
	private String gender;
	private ArrayList<String> sexPreferences;
	
	public UserDetails() {}
	
	public UserDetails(String name, String phoneNum, ArrayList<String> interests, String gender,
			ArrayList<String> sexPreferences) {
		super();
		this.name = name;
		this.phoneNum = phoneNum;
		this.interests = interests;
		this.gender = gender;
		this.sexPreferences = sexPreferences;
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

	public String getGender() {
		return gender;
	}

	public UserDetails setGender(String gender) {
		this.gender = gender;
		return this;
	}

	public ArrayList<String> getSexPreferences() {
		return sexPreferences;
	}

	public UserDetails setSexPreferences(ArrayList<String> sexPreferences) {
		this.sexPreferences = sexPreferences;
		return this;
	}

	@Override
	public String toString() {
		return "UserDetails{" +
				"name='" + name + '\'' +
				", phoneNum='" + phoneNum + '\'' +
				", interests=" + interests +
				", gender='" + gender + '\'' +
				", sexPreferences=" + sexPreferences +
				'}';
	}
}
