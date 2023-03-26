package superAppsObjects;


/**
 * Class User Represent the NewUser Boundary
 * @author Ido & Yosef
 */
public class User {
	
	private String email;
	private String role;
	private String userName;
	private String avatar;
	
	public User () {}
	
	public User(String email, String role, String userName, String avatar) {
		this.email = email;
		this.role = role;
		this.userName = userName;
		this.avatar = avatar;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getAvatar() {
		return avatar;
	}
	
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
}
