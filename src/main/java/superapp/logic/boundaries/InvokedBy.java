package superapp.logic.boundaries;

/**
 * @author Ido & Yosef
 */

public class InvokedBy {
	private UserId userId;

	public InvokedBy() {
	}

	public UserId getUserId() {
		return userId;
	}

	public InvokedBy setUserId(UserId userId) {
		this.userId = userId;
		return this;
	}

	@Override
	public String toString() {
		return "InvokedBy [userId=" + userId + "]";
	}


}
