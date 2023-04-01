package superapp.logic.boundaries;

/**
 * @author Ido & Yosef
 */

public class InvokedBy {
	private UserID userId;

	public InvokedBy() {
	}

	public InvokedBy(UserID userId) {
		this.userId = userId;
	}


	public UserID getUserId() {
		return userId;
	}

	public void setUserId(UserID userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "InvokedBy [userId=" + userId + "]";
	}


}
