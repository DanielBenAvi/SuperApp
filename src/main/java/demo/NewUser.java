package demo;


/**
 * Class NewUser Represent the NewUser Boundary
 * @author Ido & Yosef
 */
public class NewUser extends User {
	
	private String email;

	public NewUser () {}
	
	public NewUser(String email, String role, String userName, String avatar) {
		
		this.email = email;
		
		setRole(role);
		setUserName(userName);
		setAvatar(avatar);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
