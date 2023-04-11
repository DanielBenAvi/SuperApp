package superapp.logic.boundaries;

import java.util.Date;
import java.util.Map;

public class SuperAppObjectBoundary {
	
	private ObjectId objectId;
	private String type;
	private String alias;
	private Boolean active;
	private Date createTimestamp;
	private Location location;
	private CreatedBy createdBy;
	private Map<String, Object> objectDetails;
	
	public SuperAppObjectBoundary() {
	}

	public ObjectId getObjectId() {
		return objectId;
	}

	public void setObjectId(ObjectId objectId) {
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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(Date createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public CreatedBy getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(CreatedBy createdBy) {
		this.createdBy = createdBy;
	}

	public Map<String, Object> getObjectDetails() {
		return objectDetails;
	}

	public void setObjectDetails(Map<String, Object> objectDetails) {
		this.objectDetails = objectDetails;
	}

	@Override
	public String toString() {
		return "SuperAppObjectBoundary [objectId=" + objectId + ", type=" + type + ", alias=" + alias + ", active=" + active
				+ ", createTimeStamp=" + createTimestamp + ", location=" + location + ", createdBy=" + createdBy
				+ ", objectDetails=" + objectDetails + "]";
	}

}
