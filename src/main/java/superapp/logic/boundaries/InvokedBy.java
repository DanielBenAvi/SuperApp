package superapp.logic.boundaries;

import jakarta.validation.constraints.NotNull;

public class InvokedBy {
	@NotNull
	private UserId userId;

	public InvokedBy() {}

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
