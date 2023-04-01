package superapp.logic.boundaries;

/**
 * Class UserBoundary Represent the User Boundary
 * @author Ido & Yosef
 */
public class UserBoundary {

	private UserID userID;
	private String role;
	private String userName;
	private String avatar;

	public UserBoundary() {}

	public UserBoundary(UserID userID, String role, String userName, String avatar) {
		this.userID = userID;
		this.role = role;
		this.userName = userName;
		this.avatar = avatar;
	}
	
	public UserID getUserID() {
		return userID;
	}

	public void setUserID(UserID userID) {
		this.userID = userID;
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
	
	@Override
	public String toString() {
		return "UserBoundary [userID=" + userID.toString() + ", role=" + role + ", userName=" + userName + ", avatar=" + avatar
				+ "]";
	}

}
