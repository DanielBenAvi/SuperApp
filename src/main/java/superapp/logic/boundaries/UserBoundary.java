package superapp.logic.boundaries;

/**
 * Class UserBoundary Represent the User Boundary
 * @author Ido & Yosef
 */
public class UserBoundary {

	private UserID userId;
	private String role;
	private String username;
	private String avatar;

	public UserBoundary() {}

	public UserID getUserId() {
		return userId;
	}

	public void setUserId(UserID userId) {
		this.userId = userId;
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

	@Override
	public String toString() {
		return "UserBoundary [userID=" + userId.toString() + ", role=" + role + ", userName=" + username + ", avatar=" + avatar
				+ "]";
	}

}
