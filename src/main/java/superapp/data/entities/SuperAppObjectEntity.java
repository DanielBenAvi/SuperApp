package superapp.data.entities;

import java.util.Date;

public class SuperAppObjectEntity {

	private String objectId; // objectId is a objectId boundary concatenate
	private String type;
	private String alias;
	private boolean active;
	private Date createTimeStamp;
	private LocationEntity location;
	private String createdBy; // createdBy is a userId boundary concatenate
	private Object objectDetails;

	public SuperAppObjectEntity() {
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getCreateTimeStamp() {
		return createTimeStamp;
	}

	public void setCreateTimeStamp(Date createTimeStamp) {
		this.createTimeStamp = createTimeStamp;
	}

	public LocationEntity getLocation() {
		return location;
	}

	public void setLocation(LocationEntity location) {
		this.location = location;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Object getObjectDetails() {
		return objectDetails;
	}

	public void setObjectDetails(Object objectDetails) {
		this.objectDetails = objectDetails;
	}

	@Override
	public String toString() {
		return "SuperAppObjectEntity{" +
				"objectId='" + objectId + '\'' +
				", type='" + type + '\'' +
				", alias='" + alias + '\'' +
				", active=" + active +
				", createTimeStamp=" + createTimeStamp +
				", location=" + location +
				", createdBy='" + createdBy + '\'' +
				", objectDetails=" + objectDetails +
				'}';
	}
}
