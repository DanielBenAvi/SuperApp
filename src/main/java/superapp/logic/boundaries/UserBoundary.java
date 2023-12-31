package superapp.logic.boundaries;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class UserBoundary {

	@NotNull
	private UserId userId;

	@NotNull @NotEmpty
	private String role;
	@NotNull @NotEmpty
	private String username;
	@NotNull @NotEmpty
	private String avatar;

	public UserBoundary() {}

	public UserId getUserId() {
		return userId;
	}

	public UserBoundary setUserId(UserId userId) {
		this.userId = userId;
		return this;
	}

	public String getRole() {
		return role;
	}

	public UserBoundary setRole(String role) {
		this.role = role;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public UserBoundary setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getAvatar() {
		return avatar;
	}

	public UserBoundary setAvatar(String avatar) {
		this.avatar = avatar;
		return this;
	}

	@Override
	public String toString() {
		return "UserBoundary [userID=" + userId.toString() + ", role=" + role + ", userName=" + username + ", avatar=" + avatar
				+ "]";
	}

}
