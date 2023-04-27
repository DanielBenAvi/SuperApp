package superapp.logic;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import superapp.dal.entities.SuperAppObjectEntity;
import superapp.logic.boundaries.CreatedBy;
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
     * This method create new SuperAppObjectEntity, init ObjectId,
     * init CreatedBy, init CreateTimeStamp and save the SuperAppObjectEntity to database.
     *
     * @param objectBoundary SuperAppObjectBoundary
     * @return SuperAppObjectBoundary
     */
    @Override
    public SuperAppObjectBoundary createObject(SuperAppObjectBoundary objectBoundary) {


        if (objectBoundary == null)
            throw new RuntimeException("null object cant be created");

        if (!this.isCreateByExist(objectBoundary.getCreatedBy()))
            throw new RuntimeException("Created By object cant be empty");

        // TODO: for future (add to backlog in Trello as task): check user role and if user exists in database

        SuperAppObjectEntity entity = this.convertBoundaryToEntity(objectBoundary);

        String objectId = ConvertHelp.concatenateIds(new String [] {superappName, UUID.randomUUID().toString()});
        entity.setObjectId(objectId);
        entity.setCreationTimestamp(new Date());

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

        // TODO: for future (add to backlog in Trello as task): check user role and if user exists in database

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
            existingEntity.setLocation(ConvertHelp.locationBoundaryToStr(update.getLocation()));

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
    public Optional<SuperAppObjectBoundary> getSpecificObject(String objectSuperApp, String internalObjectId) {

        String objectId = ConvertHelp.concatenateIds(new String [] {objectSuperApp, internalObjectId});

        if (objectsDatabaseMockup.containsKey(objectId)) {
            SuperAppObjectBoundary boundary = this.convertEntityToBoundary(objectsDatabaseMockup.get(objectId));
            return Optional.of(boundary);
        }
        else {
            return Optional.empty();
        }
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
        boundary.setCreationTimestamp(entity.getCreationTimestamp());
        boundary.setLocation(ConvertHelp.strLocationEntityToBoundary(entity.getLocation()));
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

        entity.setLocation(ConvertHelp.locationBoundaryToStr(boundary.getLocation()));
        entity.setCreatedBy(ConvertHelp.createByBoundaryToStr(boundary.getCreatedBy()));
        entity.setObjectDetails(boundary.getObjectDetails());

        return entity;
    }


    /**
     * This method check if attributes of CreatedBy object (boundary)
     * are not null
     *
     * @param createdBy
     * @return boolean
     */
    private boolean isCreateByExist(CreatedBy createdBy) {

        if (createdBy == null)
            return false;

        if (createdBy.getUserId() == null)
            return false;

        // todo: may add check if strings is empty
        if (createdBy.getUserId().getSuperapp() == null || createdBy.getUserId().getEmail() == null)
            return false;

        return true;
    }

}
