package demo;


/**
 * 
 * @author Ido & Yosef
 */
public abstract class User {

	protected String role;
	protected String userName;
	protected String avatar;
	
	public User () {}
	
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
