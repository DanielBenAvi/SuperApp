package superapp.logic.boundaries;

public class CreatedBy {
	
	private UserID userId;

	public CreatedBy() {
	}


	public UserID getUserId() {
		return userId;
	}

	public CreatedBy setUserId(UserID userId) {
		this.userId = userId;
		return this;
	}

	@Override
	public String toString() {
		return "CreatedBy [userId=" + userId + "]";
	}

}
