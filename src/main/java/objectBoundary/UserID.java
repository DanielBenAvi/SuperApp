package objectBoundary;


/**
 * 
 * @author Ido & Yosef
 */

public class UserID {
	
	private String superapp;
	private String email;
	
	public UserID() {
		
	}
	
	public UserID(String superapp, String email) {
		setSuperapp(superapp);
		setEmail(email);
	}
	
	
	public String getSuperapp() {
		return superapp;
	}
	
	public void setSuperapp(String superapp) {
		this.superapp = superapp;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
}
