package superapp.logic.boundaries;

import jakarta.validation.constraints.NotNull;

public class TargetObject {

	@NotNull
	private ObjectId objectId;
	
	public TargetObject() {
	}

	public ObjectId getObjectId() {
		return objectId;
	}

	public TargetObject setObjectId(ObjectId objectId) {
		this.objectId = objectId;
		return this;
	}

	@Override
	public String toString() {
		return "TargetObject{" +
				"objectId=" + objectId +
				'}';
	}
}
