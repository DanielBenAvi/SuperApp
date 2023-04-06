package superapp.logic.boundaries;

public class TargetObject {
	private ObjectId objectId;
	
	public TargetObject() {
	}

	public TargetObject(ObjectId objectid) {
		this.objectId = objectid;
	}

	public ObjectId getObjectId() {
		return objectId;
	}

	public void setObjectId(ObjectId objectId) {
		this.objectId = objectId;
	}

	@Override
	public String toString() {
		return "TargetObject [objectid=" + objectId + "]";
	}
	
	
}
