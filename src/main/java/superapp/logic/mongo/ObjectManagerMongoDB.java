package superapp.logic.mongo;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import superapp.data.ObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.ConvertHelp;
import superapp.logic.ObjectsServiceWithRelationshipSupport;
import superapp.logic.boundaries.CreatedBy;
import superapp.logic.boundaries.ObjectId;
import superapp.logic.boundaries.SuperAppObjectBoundary;

import java.util.*;

@Service
public class ObjectManagerMongoDB implements ObjectsServiceWithRelationshipSupport {
    private ObjectCrud objectCrudDB;
    private String springApplicationName;

    @Autowired
    public ObjectManagerMongoDB(ObjectCrud objectCrudDB) {
        this.objectCrudDB = objectCrudDB;
    }

    // this method injects a configuration value of spring
    @Value("${spring.application.name:defaultAppName}")
    public void setSpringApplicationName(String springApplicationName) {
        this.springApplicationName = springApplicationName;
    }

    // this method is invoked after values are injected to instance
    @PostConstruct
    public void init() {
        System.err.println("***** " + this.springApplicationName);
    }

    @Override
    public SuperAppObjectBoundary createObject(SuperAppObjectBoundary objectBoundary) {
        if (objectBoundary == null) throw new RuntimeException("null object cant be created");

        if (!this.isCreateByExist(objectBoundary.getCreatedBy()))
            throw new RuntimeException("Created By object cant be empty");

        if (objectBoundary.getObjectDetails() == null) objectBoundary.setObjectDetails(new HashMap<String, Object>());

        SuperAppObjectEntity entity = this.convertBoundaryToEntity(objectBoundary);

        String objectId = ConvertHelp.concatenateIds(new String[]{springApplicationName, UUID.randomUUID().toString()});
        entity.setObjectId(objectId);
        entity.setCreationTimestamp(new Date());

        this.objectCrudDB.save(entity);

        return this.convertEntityToBoundary(entity);
    }

    private SuperAppObjectEntity convertBoundaryToEntity(SuperAppObjectBoundary boundary) {

        SuperAppObjectEntity entity = new SuperAppObjectEntity();

        entity.setType(boundary.getType());
        entity.setAlias(boundary.getAlias());

        if (boundary.getActive() != null) entity.setActive(boundary.getActive());
        else entity.setActive(false);

        entity.setLocation(ConvertHelp.locationBoundaryToStr(boundary.getLocation()));
        entity.setCreatedBy(ConvertHelp.createByBoundaryToStr(boundary.getCreatedBy()));
        entity.setObjectDetails(boundary.getObjectDetails());

        return entity;
    }

    private boolean isCreateByExist(CreatedBy createdBy) {

        if (createdBy == null) return false;

        if (createdBy.getUserId() == null) return false;

        // todo: may add check if strings is empty
        if (createdBy.getUserId().getSuperapp() == null || createdBy.getUserId().getEmail() == null) return false;

        return true;
    }

    @Override
    public SuperAppObjectBoundary updateObject(String objectSuperApp, String internalObjectId, SuperAppObjectBoundary update) {
        String objectId = ConvertHelp.concatenateIds(new String[]{objectSuperApp, internalObjectId});

        SuperAppObjectEntity exists = this.objectCrudDB.findById(objectId).orElseThrow(() -> new NotFoundException("could not update object by id: " + objectId + " because it does not exist"));
        // TODO: for future (add to backlog in Trello as task): check user role and if user exists in database

        if (exists == null) throw new RuntimeException("Could not find object by id: " + objectId);

        boolean dirty_flag = false;
        if (update.getType() != null) {
            exists.setType(update.getType());
            dirty_flag = true;
        }

        if (update.getAlias() != null) {
            exists.setAlias(update.getAlias());
            dirty_flag = true;
        }

        if (update.getActive() != null) {
            exists.setActive(update.getActive());
            dirty_flag = true;
        }

        if (update.getLocation() != null) {
            exists.setLocation(ConvertHelp.locationBoundaryToStr(update.getLocation()));
            dirty_flag = true;
        }

        // TODO: need to check ObjectDetails attributes
        if (update.getObjectDetails() != null) {
            exists.setObjectDetails(update.getObjectDetails());
            dirty_flag = true;
        }

        if (dirty_flag) exists = this.objectCrudDB.save(exists);

        return this.convertEntityToBoundary(exists);
    }

    @Override
    public Optional<SuperAppObjectBoundary> getSpecificObject(String objectSuperApp, String internalObjectId) {
        String objectId = ConvertHelp.concatenateIds(new String[]{objectSuperApp, internalObjectId});
        return this.objectCrudDB.findById(objectId).map(this::convertEntityToBoundary);
    }

    @Override
    public List<SuperAppObjectBoundary> getAllObjects() {
        return this.objectCrudDB.findAll().stream().map(this::convertEntityToBoundary).toList();
    }

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

    @Override
    public void deleteAllObjects() {
        this.objectCrudDB.deleteAll();
    }

    @Override
    public void addChild(String superApp, String parentId, ObjectId childId) {

        if (!checkValidSuperApp(superApp))
            throw new BadRequestException("superApp must be in format: " + springApplicationName);

        if (!checkValidSuperApp(childId.getSuperapp()))
            throw new BadRequestException("childId must be in format: " + springApplicationName + "#" + UUID.randomUUID().toString());

        if (!checkValidInternalObjectId(parentId))
            throw new BadRequestException("parentId must be in format: " + springApplicationName + "#" + UUID.randomUUID().toString());

        if (!checkValidInternalObjectId(childId.getInternalObjectId()))
            throw new BadRequestException("childId must be in format: " + springApplicationName + "#" + UUID.randomUUID().toString());

        if (parentId.equals(childId.getInternalObjectId()))
            throw new ConflictRequestException("origin and child are the same object");

        SuperAppObjectEntity parent = this.objectCrudDB.findById(ConvertHelp.concatenateIds(new String[]{superApp, parentId})).orElseThrow(() -> new NotFoundException("could not add child to object by id: " + parentId + " because it does not exist"));
        SuperAppObjectEntity child = this.objectCrudDB.findById(ConvertHelp.objectIdBoundaryToStr(childId)).orElseThrow(() -> new NotFoundException("could not add child to object by id: " + childId.toString() + " because it does not exist"));

        if (child.getParent() != null) throw new BadRequestException("child already has a parent");

        parent.addChildren(child);
        child.setParent(parent);

        this.objectCrudDB.save(parent);
        this.objectCrudDB.save(child);

    }

    @Override
    public List<SuperAppObjectBoundary> getChildren(String superapp, String parentInternalObjectId) {

        if (!checkValidSuperApp(superapp))
            throw new BadRequestException("superApp must be in format: " + springApplicationName);

        if (!checkValidInternalObjectId(parentInternalObjectId))
            throw new BadRequestException("parentId must be in format: " + springApplicationName + "#" + UUID.randomUUID().toString());

        SuperAppObjectEntity origin = this.objectCrudDB.findById(ConvertHelp.concatenateIds(new String[]{superapp, parentInternalObjectId})).orElseThrow(() -> new NotFoundException("could not get children of object by id: " + parentInternalObjectId.toString() + " because it does not exist"));
        Set<SuperAppObjectEntity> children = origin.getChildren();
        return children.stream().map(this::convertEntityToBoundary).toList();
    }

    @Override
    public List<SuperAppObjectBoundary> getParent(String superapp, String childInternalObjectId) {
        if (!checkValidSuperApp(superapp))
            throw new BadRequestException("superApp must be in format: " + springApplicationName);

        if (!checkValidInternalObjectId(childInternalObjectId))
            throw new BadRequestException("parentId must be in format: " + springApplicationName + "#" + UUID.randomUUID().toString());

        SuperAppObjectEntity child = this.objectCrudDB.findById(ConvertHelp.concatenateIds(new String[]{superapp, childInternalObjectId})).orElseThrow(() -> new NotFoundException("could not get origin of object by id: " + childInternalObjectId.toString() + " because it does not exist"));

        if (child.getParent() != null) {
            return Optional.of(child.getParent())
                    .map(this::convertEntityToBoundary).stream().toList();
        }

        return List.of();
    }


    private boolean checkValidInternalObjectId(String internalObjectId) {
        if (internalObjectId == null)
            return false;

        if (internalObjectId.isEmpty())
            return false;

        try {
            UUID.fromString(internalObjectId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean checkValidSuperApp(String superApp) {
        if (superApp == null)
            return false;

        if (superApp.isEmpty())
            return false;

        if (!superApp.equals(springApplicationName))
            return false;

        return true;
    }

}
