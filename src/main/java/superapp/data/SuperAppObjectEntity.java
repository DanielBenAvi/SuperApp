package superapp.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "OBJECTS")
public class SuperAppObjectEntity {

    @Id
    private String objectId; // objectId is a objectId boundary concatenate
    private String type;
    private String alias;
    private boolean active;
    private Date creationTimestamp;
    private String location;
    private String createdBy;
    private Map<String, Object> objectDetails;

    @DBRef
    private Set<SuperAppObjectEntity> children;
    @DBRef
    private SuperAppObjectEntity parent;

    public SuperAppObjectEntity() {
        this.children = new HashSet<>();
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

    public boolean isActive() {
        return active;
    }

    public Set<SuperAppObjectEntity> getChildren() {
        return children;
    }

    public void addChildren(SuperAppObjectEntity child) {
        this.children.add(child);
    }

    public void setChildren(Set<SuperAppObjectEntity> children) {
        this.children = children;
    }

    public SuperAppObjectEntity getParent() {
        return parent;
    }

    public void setParent(SuperAppObjectEntity parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuperAppObjectEntity that = (SuperAppObjectEntity) o;
        return active == that.active && Objects.equals(objectId, that.objectId) && Objects.equals(type, that.type) && Objects.equals(alias, that.alias) && Objects.equals(creationTimestamp, that.creationTimestamp) && Objects.equals(location, that.location) && Objects.equals(createdBy, that.createdBy) && Objects.equals(objectDetails, that.objectDetails) && Objects.equals(children, that.children) && Objects.equals(parent, that.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectId, type, alias, active, creationTimestamp, location, createdBy, objectDetails, children, parent);
    }

    @Override
    public String toString() {
        return "SuperAppObjectEntity{" +
                "objectId='" + objectId + '\'' +
                ", type='" + type + '\'' +
                ", alias='" + alias + '\'' +
                ", active=" + active +
                ", creationTimestamp=" + creationTimestamp +
                ", location='" + location + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", objectDetails=" + objectDetails +
                ", children=" + children +
                ", parent=" + parent +
                '}';
    }
}
