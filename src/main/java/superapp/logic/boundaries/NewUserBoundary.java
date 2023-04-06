package superapp.logic.boundaries;


import superapp.data.UserRole;

/**
 * Class User Represent the NewUser Boundary
 * @author Ido & Yosef
 */
public class NewUserBoundary {
	
	private String email;
	private String role;
	private String username;
	private String avatar;
	
	public NewUserBoundary() {}
	
	public NewUserBoundary(String email, String role, String userName, String avatar) {
		this.email = email;
		this.role = role;
		this.username = userName;
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
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getAvatar() {
		return avatar;
	}
	
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
}
