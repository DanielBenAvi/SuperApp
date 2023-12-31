package superapp.logic.boundaries;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ObjectId {

	@NotNull @NotEmpty
	private String superapp;
	@NotNull @NotEmpty
	private String internalObjectId;
	
	public ObjectId() {}

	public ObjectId(String superapp, String internalObjectId) {
		this.superapp = superapp;
		this.internalObjectId = internalObjectId;
	}

	public String getSuperapp() {
		return superapp;
	}

	public ObjectId setSuperapp(String superapp) {
		this.superapp = superapp;
		return this;
	}

	public String getInternalObjectId() {
		return internalObjectId;
	}

	public ObjectId setInternalObjectId(String internalObjectId) {
		this.internalObjectId = internalObjectId;
		return this;
	}

	@Override
	public String toString() {
		return "ObjectId [superApp=" + superapp + ", "
				+ "internalObjectId=" + internalObjectId + "]";
	}

}
