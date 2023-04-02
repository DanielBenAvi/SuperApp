package superapp.logic;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import superapp.data.entities.SuperAppObjectEntity;
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

        String objectId = ConvertHelp.concatenateIds(new String [] {superappName, UUID.randomUUID().toString()});

        entity.setObjectId(objectId);
        entity.setCreateTimeStamp(new Date());
        // TODO: verify if user exist
        entity.setCreatedBy(""); // TODO: ask eyal

        this.objectsDatabaseMockup.put(objectId, entity);

        return this.convertEntityToBoundary(entity);
    }

    @Override
    public SuperAppObjectBoundary updateObject(String objectSuperApp,
                                               String internalObjectId,
                                               SuperAppObjectBoundary update) {

        String objectId = ConvertHelp.concatenateIds(new String [] {objectSuperApp, internalObjectId});

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

        String objectId = ConvertHelp.concatenateIds(new String [] {objectSuperApp, internalObjectId});

        if (objectsDatabaseMockup.containsKey(objectId)) {
            return this.convertEntityToBoundary(objectsDatabaseMockup.get(objectId));
        }
        else {
            throw new RuntimeException("Could not find object by id: " + objectId);
        }

    }

    @Override
    public List<SuperAppObjectBoundary> getAllObjects() {

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

        SuperAppObjectBoundary boundary = new SuperAppObjectBoundary();

        boundary.setObjectId(ConvertHelp.strObjectIdToBoundary(entity.getObjectId()));
        boundary.setType(entity.getType());
        boundary.setAlias(entity.getAlias());
        boundary.setActive(entity.getActive());
        boundary.setCreateTimeStamp(entity.getCreateTimeStamp());
        boundary.setLocation(ConvertHelp.locationEntityToBoundary(entity.getLocation()));
        boundary.setCreatedBy(ConvertHelp.strCreateByToBoundary(entity.getCreatedBy()));
        boundary.setObjectDetails(entity.getObjectDetails());

        return boundary;
    }

    private SuperAppObjectEntity convertBoundaryToEntity(SuperAppObjectBoundary boundary) {
        SuperAppObjectEntity entity = new SuperAppObjectEntity();

        entity.setType(boundary.getType());
        entity.setAlias(boundary.getAlias());

        if (boundary.getActive() != null)
            entity.setActive(boundary.getActive());
        else
            entity.setActive(false); // TODO what value shall be

        entity.setLocation(ConvertHelp.locationBoundaryToEntity(boundary.getLocation()));
        entity.setCreatedBy(ConvertHelp.createByBoundaryToStr(boundary.getCreatedBy()));
        entity.setObjectDetails(boundary.getObjectDetails());

        return entity;
    }

}
