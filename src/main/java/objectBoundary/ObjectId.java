package objectBoundary;

public class ObjectId {
	
	private String superApp;
	private int internalObjectId;
	
	public ObjectId() {
	}

	public String getSuperApp() {
		return superApp;
	}

	public void setSuperApp(String superApp) {
		this.superApp = superApp;
	}

	public int getInternalObjectId() {
		return internalObjectId;
	}

	public void setInternalObjectId(int internalObjectId) {
		this.internalObjectId = internalObjectId;
	}

	@Override
	public String toString() {
		return "ObjectId [superApp=" + superApp + ", "
				+ "internalObjectId=" + internalObjectId + 
				"]";
	}
	
	
	
	

}
