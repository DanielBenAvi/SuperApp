package superapp.logic.boundaries;


/**
 * 
 * @author Ido & Yosef
 */

public class UserID {
	private String superapp;
	private String email;
	
	public UserID() {}

	public UserID(String superapp, String email) {
		this.superapp = superapp;
		this.email = email;
	}

	public String getSuperapp() {
		return superapp;
	}

	public UserID setSuperapp(String superapp) {
		this.superapp = superapp;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public UserID setEmail(String email) {
		this.email = email;
		return this;
	}

	@Override
	public String toString() {
		return "[superapp=" + superapp + ", email=" + email + "]";
	}
	
}
