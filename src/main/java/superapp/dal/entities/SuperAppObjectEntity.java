package superapp.dal.entities;

import java.util.Date;
import java.util.Map;

public class SuperAppObjectEntity {

	private String objectId; // objectId is a objectId boundary concatenate
	private String type;
	private String alias;
	private boolean active;
	private Date creationTimestamp;
	private String location;
	private String createdBy; // createdBy is a userId boundary concatenate
	private Map<String, Object> objectDetails;

	public SuperAppObjectEntity() {
	}

	public String getObjectId() {
		return objectId;
	}

	public SuperAppObjectEntity setObjectId(String objectId) {
		this.objectId = objectId;
		return this;
	}

	public String getType() {
		return type;
	}

	public SuperAppObjectEntity setType(String type) {
		this.type = type;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public SuperAppObjectEntity setAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public boolean getActive() {
		return active;
	}

	public SuperAppObjectEntity setActive(boolean active) {
		this.active = active;
		return this;
	}

	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public SuperAppObjectEntity setCreationTimestamp(Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
		return this;
	}

	public String getLocation() {
		return location;
	}

	public SuperAppObjectEntity setLocation(String location) {
		this.location = location;
		return this;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public SuperAppObjectEntity setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public Map<String, Object> getObjectDetails() {
		return objectDetails;
	}

	public SuperAppObjectEntity setObjectDetails(Map<String, Object> objectDetails) {
		this.objectDetails = objectDetails;
		return this;
	}

	@Override
	public String toString() {
		return "SuperAppObjectEntity{" +
				"objectId='" + objectId + '\'' +
				", type='" + type + '\'' +
				", alias='" + alias + '\'' +
				", active=" + active +
				", createTimeStamp=" + creationTimestamp +
				", location=" + location +
				", createdBy='" + createdBy + '\'' +
				", objectDetails=" + objectDetails +
				'}';
	}
}
