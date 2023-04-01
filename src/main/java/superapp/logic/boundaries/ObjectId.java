package superapp.logic.boundaries;

public class ObjectId {
	
	private String superApp;
	private String internalObjectId;
	
	public ObjectId() {
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
				+ "internalObjectId=" + internalObjectId + "]";
	}

}
