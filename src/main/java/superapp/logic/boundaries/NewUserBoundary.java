package superapp.logic.boundaries;


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

	public String getEmail() {
		return email;
	}

	public NewUserBoundary setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getRole() {
		return role;
	}

	public NewUserBoundary setRole(String role) {
		this.role = role;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public NewUserBoundary setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getAvatar() {
		return avatar;
	}

	public NewUserBoundary setAvatar(String avatar) {
		this.avatar = avatar;
		return this;
	}

	@Override
	public String toString() {
		return "NewUserBoundary{" +
				"email='" + email + '\'' +
				", role='" + role + '\'' +
				", username='" + username + '\'' +
				", avatar='" + avatar + '\'' +
				'}';
	}
}
