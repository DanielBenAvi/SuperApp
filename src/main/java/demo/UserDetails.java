package demo;

import java.util.ArrayList;


/**
 * @author Ido & Yosef
 */

public class UserDetails {
	
	private String name;
	private String phoneNum;
	private ArrayList<String> intrests;
	private String gender;
	private ArrayList<String> sexPrefrences;
	// location ?? define in objectDetails (Eyal defenition)
	
	public UserDetails() {}
	
	public UserDetails(String name, String phoneNum, ArrayList<String> intrests, String gender,
			ArrayList<String> sexPrefrences) {
		super();
		this.name = name;
		this.phoneNum = phoneNum;
		this.intrests = intrests;
		this.gender = gender;
		this.sexPrefrences = sexPrefrences;
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
	
	public ArrayList<String> getIntrests() {
		return intrests;
	}
	
	public void setIntrests(ArrayList<String> intrests) {
		this.intrests = intrests;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public ArrayList<String> getSexPrefrences() {
		return sexPrefrences;
	}
	
	public void setSexPrefrences(ArrayList<String> sexPrefrences) {
		this.sexPrefrences = sexPrefrences;
	}

}
