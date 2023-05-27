package superapp.logic.boundaries;

import jakarta.validation.constraints.NotNull;

public class CreatedBy {

	@NotNull
	private UserId userId;

	public CreatedBy() {
	}


	public UserId getUserId() {
		return userId;
	}

	public CreatedBy setUserId(UserId userId) {
		this.userId = userId;
		return this;
	}

	@Override
	public String toString() {
		return "CreatedBy [userId=" + userId + "]";
	}

}
