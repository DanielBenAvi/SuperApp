package superapp.logic.boundaries;

import java.util.UUID;

public class ObjectId {
	
	private String superApp;
	private String internalObjectId;
	
	public ObjectId() {
		UUID uuid = UUID.randomUUID();
		internalObjectId = uuid.toString();
	}

	public String getSuperApp() {
		return superApp;
	}

	public void setSuperApp(String superApp) {
		this.superApp = superApp;
	}

	public String getInternalObjectId() {
		return internalObjectId;
	}

	public void setInternalObjectId(String internalObjectId) {
		this.internalObjectId = internalObjectId;
	}

	@Override
	public String toString() {
		return "ObjectId [superApp=" + superApp + ", "
				+ "internalObjectId=" + internalObjectId + 
				"]";
	}
	
	
	
	

}
