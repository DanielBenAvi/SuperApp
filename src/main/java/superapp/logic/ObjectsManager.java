package superapp.logic;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.boundaries.SuperAppObjectBoundary;

import java.util.*;

@Service
public class ObjectsManager implements ObjectsService {

    private Map<String, SuperAppObjectEntity> objectsDatabaseMockup;
    private String superappName;

    // injects a configuration value of spring
    @Value("${spring.application.name:defaultAppName}")
    public void setApplicationName(String springApplicationName) {
        this.superappName = springApplicationName;
    }

    // invoked after values are injected to instance
    @PostConstruct
    public void init() {
        this.objectsDatabaseMockup = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public SuperAppObjectBoundary createObject(SuperAppObjectBoundary objectBoundary) {

        if (objectBoundary == null)
            throw new RuntimeException("null object cant be created");

        if (objectBoundary.getType() == null)
            throw new RuntimeException("Object type missing");

        if (objectBoundary.getObjectDetails() == null)
            throw new RuntimeException("Object details cant be null");

        SuperAppObjectEntity entity = this.convertBoundaryToEntity(objectBoundary);

        String objectId = superappName + "_" + UUID.randomUUID().toString();

        entity.setObjectId(objectId);
        entity.setCreateTimeStamp(new Date());
        // TODO: verify if user exist
        entity.setCreatedBy(""); // TODO: ask eyal

        this.objectsDatabaseMockup.put(objectId, entity);

        return this.convertEntityToBoundary(entity);
    }

    @Override
    public SuperAppObjectBoundary updateObject(String objectSuperApp, String internalObjectId, SuperAppObjectBoundary update) {

        String objectId = objectSuperApp + "_" + internalObjectId;

        // todo: not sure if attributes : type, alias, active. can be changed by client
        // createdBy, createTimeStamp ,objectId : not changed by client.

        if (!objectsDatabaseMockup.containsKey(objectId)) {
            throw new RuntimeException("Could not find object by id: " + objectId);
        }

        SuperAppObjectEntity entity = this.objectsDatabaseMockup.get(objectId);

        SuperAppObjectEntity newEntity = this.convertBoundaryToEntity(update);

        boolean dirtyFlag = false;

        if (newEntity.getLocation() != null) {
            entity.setLocation(newEntity.getLocation());
            dirtyFlag = true;
        }

        if (newEntity.getObjectDetails() != null) {
            // TODO: need to check ObjectDetails attributes
            entity.setObjectDetails(newEntity.getObjectDetails());
            dirtyFlag = true;
        }

        // todo unknown if necessary
        if (dirtyFlag) {
            this.objectsDatabaseMockup.put(objectId, entity);
        }

        return this.convertEntityToBoundary(entity);
    }

    @Override
    public SuperAppObjectBoundary getSpecificObject(String objectSuperApp, String internalObjectId) {

        String objectId = objectSuperApp + "_" + internalObjectId;

        if (objectsDatabaseMockup.containsKey(objectId)) {
            return this.convertEntityToBoundary(objectsDatabaseMockup.get(objectId));
        }
        else {
            throw new RuntimeException("Could not find object by id: " + objectId);
        }

    }

    @Override
    public List<SuperAppObjectBoundary> getAllObjects() {

        // TODO: is necessary to check if map is empty?
        return this.objectsDatabaseMockup.values()
                .stream()
                .map(this::convertEntityToBoundary)
                .toList();
    }

    @Override
    public void deleteAllObjects() {
        objectsDatabaseMockup.clear();
    }

    private SuperAppObjectBoundary convertEntityToBoundary(SuperAppObjectEntity entity) {
        // TODO: complete methode
        return new SuperAppObjectBoundary();
    }

    private SuperAppObjectEntity convertBoundaryToEntity(SuperAppObjectBoundary boundary) {
        // TODO: complete methode
        return new SuperAppObjectEntity();
    }
}
