package superapp.logic.boundaries;


/**
 * 
 * @author Ido & Yosef
 */

public class UserId {
	private String superapp;
	private String email;
	
	public UserId() {}

	public UserId(String superapp, String email) {
		this.superapp = superapp;
		this.email = email;
	}

	public String getSuperapp() {
		return superapp;
	}

	public UserId setSuperapp(String superapp) {
		this.superapp = superapp;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public UserId setEmail(String email) {
		this.email = email;
		return this;
	}

	@Override
	public String toString() {
		return "[superapp=" + superapp + ", email=" + email + "]";
	}
	
}
