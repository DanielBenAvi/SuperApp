package superapp.logic.boundaries;

/**
 * @author Ido & Yosef
 */

public class InvokedBy {
	private UserID userId;

	public InvokedBy() {
	}

	public UserID getUserId() {
		return userId;
	}

	public InvokedBy setUserId(UserID userId) {
		this.userId = userId;
		return this;
	}

	@Override
	public String toString() {
		return "InvokedBy [userId=" + userId + "]";
	}


}
