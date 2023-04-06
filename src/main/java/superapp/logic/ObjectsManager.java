package superapp.logic;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import superapp.data.entities.SuperAppObjectEntity;
import superapp.logic.boundaries.SuperAppObjectBoundary;

import java.util.*;

/**
 * @author Ido & Yosef
 */
@Service
public class ObjectsManager implements ObjectsService {

    private Map<String, SuperAppObjectEntity> objectsDatabaseMockup;
    private String superappName;


    /**
     * This methode injects a configuration value of spring.
     *
     * @param springApplicationName String
     */
    @Value("${spring.application.name:defaultAppName}")
    public void setApplicationName(String springApplicationName) {
        this.superappName = springApplicationName;
    }


    /**
     * This methode invoked after values are injected to instance.
     * the methode init database mockup as thread saf Map
     *
     */
    @PostConstruct
    public void init() {
        this.objectsDatabaseMockup = Collections.synchronizedMap(new HashMap<>());
    }

    /**
     * This methode create new SuperAppObjectEntity, init ObjectId,
     * init CreatedBy, init CreateTimeStamp and save the SuperAppObjectEntity to database.
     *
     * @param objectBoundary SuperAppObjectBoundary
     * @return SuperAppObjectBoundary
     */
    @Override
    public SuperAppObjectBoundary createObject(SuperAppObjectBoundary objectBoundary) {

        if (objectBoundary == null)
            throw new RuntimeException("null object cant be created");

        // TODO: verify if user exist in next phase

        SuperAppObjectEntity entity = this.convertBoundaryToEntity(objectBoundary);

        String objectId = ConvertHelp.concatenateIds(new String [] {superappName, UUID.randomUUID().toString()});
        entity.setObjectId(objectId);
        entity.setCreateTimeStamp(new Date());

        entity.setCreatedBy("superappDefault_yo@gmail.com");

        this.objectsDatabaseMockup.put(objectId, entity);

        return this.convertEntityToBoundary(entity);
    }

    /**
     * This methode update not null attr of SuperAppObjectEntity in database.
     * all attr can be updated except : createdBy, createTimeStamp ,objectId.
     *
     * @param objectSuperApp String
     * @param internalObjectId String
     * @param update SuperAppObjectBoundary
     * @return SuperAppObjectBoundary
     */
    @Override
    public SuperAppObjectBoundary updateObject(String objectSuperApp,
                                               String internalObjectId,
                                               SuperAppObjectBoundary update) {

        String objectId = ConvertHelp.concatenateIds(new String [] {objectSuperApp, internalObjectId});

        if (!objectsDatabaseMockup.containsKey(objectId))
            throw new RuntimeException("Could not find object by id: " + objectId);

        SuperAppObjectEntity existingEntity = this.objectsDatabaseMockup.get(objectId);

        if (update.getType() != null)
            existingEntity.setType(update.getType());

        if (update.getAlias() != null)
            existingEntity.setAlias(update.getAlias());

        if (update.getActive() != null)
            existingEntity.setActive(update.getActive());

        if (update.getLocation() != null)
            existingEntity.setLocation(ConvertHelp.locationBoundaryToEntity(update.getLocation()));

        // TODO: need to check ObjectDetails attributes
        if (update.getObjectDetails() != null)
            existingEntity.setObjectDetails(update.getObjectDetails());

        return this.convertEntityToBoundary(existingEntity);
    }

    /**
     * This methode return a specific object according to id.
     * id represent by @params.
     *
     * @param objectSuperApp String
     * @param internalObjectId String
     * @return SuperAppObjectBoundary
     */
    @Override
    public SuperAppObjectBoundary getSpecificObject(String objectSuperApp, String internalObjectId) {

        String objectId = ConvertHelp.concatenateIds(new String [] {objectSuperApp, internalObjectId});

        if (objectsDatabaseMockup.containsKey(objectId))
            return this.convertEntityToBoundary(objectsDatabaseMockup.get(objectId));
        else
            throw new RuntimeException("Could not find object by id: " + objectId);

    }

    /**
     * This methode return all existing objects in database.
     * @return
     */
    @Override
    public List<SuperAppObjectBoundary> getAllObjects() {

        return this.objectsDatabaseMockup.values()
                                                .stream()
                                                .map(this::convertEntityToBoundary)
                                                .toList();
    }

    /**
     * This methode delete all objects in database.
     */
    @Override
    public void deleteAllObjects() {
        objectsDatabaseMockup.clear();
    }

    /**
     * This methode convert SuperAppObjectEntity to SuperAppObjectBoundary.
     *
     * @param entity SuperAppObjectEntity
     * @return boundary SuperAppObjectBoundary
     */
    private SuperAppObjectBoundary convertEntityToBoundary(SuperAppObjectEntity entity) {

        SuperAppObjectBoundary boundary = new SuperAppObjectBoundary();

        boundary.setObjectId(ConvertHelp.strObjectIdToBoundary(entity.getObjectId()));
        boundary.setType(entity.getType());
        boundary.setAlias(entity.getAlias());
        boundary.setActive(entity.getActive());
        boundary.setCreateTimestamp(entity.getCreateTimeStamp());
        boundary.setLocation(ConvertHelp.locationEntityToBoundary(entity.getLocation()));
        boundary.setCreatedBy(ConvertHelp.strCreateByToBoundary(entity.getCreatedBy()));
        boundary.setObjectDetails(entity.getObjectDetails());

        return boundary;
    }


    /**
     * This methode convert SuperAppObjectBoundary to SuperAppObjectEntity.
     *
     * @param boundary SuperAppObjectBoundary
     * @return entity SuperAppObjectEntity
     */
    private SuperAppObjectEntity convertBoundaryToEntity(SuperAppObjectBoundary boundary) {
        SuperAppObjectEntity entity = new SuperAppObjectEntity();

        entity.setType(boundary.getType());
        entity.setAlias(boundary.getAlias());

        if (boundary.getActive() != null)
            entity.setActive(boundary.getActive());
        else
            entity.setActive(false);

        entity.setLocation(ConvertHelp.locationBoundaryToEntity(boundary.getLocation()));
        entity.setCreatedBy(ConvertHelp.createByBoundaryToStr(boundary.getCreatedBy()));
        entity.setObjectDetails(boundary.getObjectDetails());

        return entity;
    }

}
