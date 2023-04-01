package superapp.logic.boundaries;

public class CreatedBy {
	
	private UserID userId;

	public CreatedBy() {
	}

	public UserID getUserId() {
		return userId;
	}

	public void setUserId(UserID userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "CreatedBy [userId=" + userId + "]";
	}

}
