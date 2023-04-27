package superapp.logic.boundaries;

import java.util.Date;
import java.util.Map;

public class SuperAppObjectBoundary {

    private ObjectId objectId;
    private String type;
    private String alias;
    private Boolean active;
    private Date creationTimestamp;
    private Location location;
    private CreatedBy createdBy;
    private Map<String, Object> objectDetails;

    public SuperAppObjectBoundary() {
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public SuperAppObjectBoundary setObjectId(ObjectId objectId) {
        this.objectId = objectId;
        return this;
    }

    public String getType() {
        return type;
    }

    public SuperAppObjectBoundary setType(String type) {
        this.type = type;
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public SuperAppObjectBoundary setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public Boolean getActive() {
        return active;
    }

    public SuperAppObjectBoundary setActive(Boolean active) {
        this.active = active;
        return this;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public SuperAppObjectBoundary setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
        return this;
    }

    public Location getLocation() {
        return location;
    }

    public SuperAppObjectBoundary setLocation(Location location) {
        this.location = location;
        return this;
    }

    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public SuperAppObjectBoundary setCreatedBy(CreatedBy createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public Map<String, Object> getObjectDetails() {
        return objectDetails;
    }

    public SuperAppObjectBoundary setObjectDetails(Map<String, Object> objectDetails) {
        this.objectDetails = objectDetails;
        return this;
    }

    @Override
    public String toString() {
        return "SuperAppObjectBoundary [objectId=" + objectId + ", type=" + type + ", alias=" + alias + ", active=" + active
                + ", createTimeStamp=" + creationTimestamp + ", location=" + location + ", createdBy=" + createdBy
                + ", objectDetails=" + objectDetails + "]";
    }

}
