package superapp.logic.mockup;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.ObjectsService;
import superapp.logic.boundaries.CreatedBy;
import superapp.logic.boundaries.SuperAppObjectBoundary;
import superapp.logic.utils.convertors.ConvertIdsHelper;
import superapp.logic.utils.convertors.ObjectConvertor;

import java.util.*;


//@Service
public class ObjectsManagerMockup implements ObjectsService {

    private Map<String, SuperAppObjectEntity> objectsDatabaseMockup;
    private String superappName;

    private final ObjectConvertor objectConvertor;

    @Autowired
    public ObjectsManagerMockup (ObjectConvertor objectConvertor) {
        this.objectConvertor = objectConvertor;
    }
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

        if (help_object_validate(objectBoundary)) {

            SuperAppObjectEntity entity = this.objectConvertor.toEntity(objectBoundary);

            String objectId = ConvertIdsHelper.concatenateIds(new String[]{superappName, UUID.randomUUID().toString()});
            entity.setObjectId(objectId);
            entity.setCreationTimestamp(new Date());

            this.objectsDatabaseMockup.put(objectId, entity);

            return this.objectConvertor.toBoundary(entity);
        }
        else throw new RuntimeException("object must contain all fields");
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

        String objectId = ConvertIdsHelper.concatenateIds(new String [] {objectSuperApp, internalObjectId});

        if (!objectsDatabaseMockup.containsKey(objectId))
            throw new RuntimeException("Could not find object by id: " + objectId);

        SuperAppObjectEntity existingEntity = this.objectsDatabaseMockup.get(objectId);

        if (update.getType() != null && !update.getType().equals(""))
            existingEntity.setType(update.getType());
        else throw new RuntimeException("invalid type");

        if (update.getAlias() != null && !update.getAlias().equals(""))
            existingEntity.setAlias(update.getAlias());
        else throw new RuntimeException("invalid alias");

        if (update.getActive() != null)
            existingEntity.setActive(update.getActive());
        else throw new RuntimeException("invalid activity");

        if (update.getLocation() != null)
            existingEntity.setLocation(this.objectConvertor.locationToEntity(update.getLocation()));
        else throw new RuntimeException("invalid location");

        if (update.getObjectDetails() != null)
            existingEntity.setObjectDetails(update.getObjectDetails());
        else throw new RuntimeException("invalid object details");

        return this.objectConvertor.toBoundary(existingEntity);
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

        String objectId = ConvertIdsHelper.concatenateIds(new String [] {objectSuperApp, internalObjectId});

        if (objectsDatabaseMockup.containsKey(objectId)) {
            SuperAppObjectBoundary boundary = this.objectConvertor.toBoundary(objectsDatabaseMockup.get(objectId));
            return Optional.of(boundary);
        }
        else {
            return Optional.empty();
        }
    }


    /**
     * This methode return all existing objects in database.
     * @return List<SuperAppObjectBoundary>
     */
    @Override
    public List<SuperAppObjectBoundary> getAllObjects() {

        return this.objectsDatabaseMockup.values()
                                                .stream()
                                                .map(this.objectConvertor::toBoundary)
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
     * This method check if attributes of CreatedBy object (boundary)
     * are not null
     *
     * @return boolean
     */
    private boolean isCreateByExist(CreatedBy createdBy) {

        if (createdBy == null)
            return false;

        if (createdBy.getUserId() == null)
            return false;

        return createdBy.getUserId().getSuperapp() != null && createdBy.getUserId().getEmail() != null;
    }
    private  boolean help_object_validate(SuperAppObjectBoundary objectBoundary){
        if (objectBoundary.getType() == null && objectBoundary.getType().equals(""))
          return false;

        if (objectBoundary.getAlias() == null && objectBoundary.getAlias().equals(""))
            return false;

        if (objectBoundary.getActive() == null)
            return false;

        if (objectBoundary.getLocation() == null)
            return false;

        if (objectBoundary.getObjectDetails() == null)
            return false;
        return this.isCreateByExist(objectBoundary.getCreatedBy());
    }

}
