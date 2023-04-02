package superapp.data;

import java.util.ArrayList;


/**
 * @author Ido & Yosef
 */

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
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPhoneNum() {
		return phoneNum;
	}
	
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	
	public ArrayList<String> getInterests() {
		return interests;
	}
	
	public void setInterests(ArrayList<String> interests) {
		this.interests = interests;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public ArrayList<String> getSexPreferences() {
		return sexPreferences;
	}
	
	public void setSexPreferences(ArrayList<String> sexPreferences) {
		this.sexPreferences = sexPreferences;
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
