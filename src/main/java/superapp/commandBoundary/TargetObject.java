package superapp.commandBoundary;

import objectBoundary.ObjectId;

/**
 * @author Ido & Yosef
 */

public class TargetObject {
	private ObjectId objectid;
	
	public TargetObject() {
	}

	public TargetObject(ObjectId objectid) {
		this.objectid = objectid;
	}

	public ObjectId getObjectid() {
		return objectid;
	}

	public void setObjectid(ObjectId objectid) {
		this.objectid = objectid;
	}

	@Override
	public String toString() {
		return "TargetObject [objectid=" + objectid + "]";
	}
	
	
}
