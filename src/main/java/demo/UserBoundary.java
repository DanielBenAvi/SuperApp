package demo;


/**
 * Class UserBoundary Represent the User Boundary
 * @author Ido & Yosef
 */
public class UserBoundary extends User {
	
	private UserID userID;
	private UserDetails userDetails;
	
	public UserBoundary() {}
	
	public UserBoundary(UserID userID, String role, String userName, String avatar, UserDetails userDetails) {
		
		this.userID = userID;
		this.userDetails = userDetails;
		
		setRole(role);
		setUserName(userName);
		setAvatar(avatar);

	}
	
	public UserID getUserID() {
		return userID;
	}
	
	public void setUserID(UserID userID) {
		this.userID = userID;
	}

	public UserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(UserDetails userDetails) {
		this.userDetails = userDetails;
	}
	
}
