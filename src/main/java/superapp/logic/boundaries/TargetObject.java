package superapp.logic.boundaries;

public class TargetObject {
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
		return "TargetObject [objectid=" + objectId + "]";
	}
	
	
}
