package superapp.logic.mongo;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import superapp.data.ObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.ConvertHelp;
import superapp.logic.ObjectsService;
import superapp.logic.ObjectsServiceWithRelationshipSupport;
import superapp.logic.boundaries.CreatedBy;
import superapp.logic.boundaries.SuperAppObjectBoundary;
import superapp.logic.excptions.BadRequestException;
import superapp.logic.excptions.NotFoundException;

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
    public void addChild(String originId, String childId) {
        // find origin object
        SuperAppObjectEntity origin = this.objectCrudDB.findById(originId).orElseThrow(() -> new NotFoundException("could not add response to object by id: " + originId + " because it does not exist"));

        // find child object
        SuperAppObjectEntity child = this.objectCrudDB.findById(childId).orElseThrow(() -> new NotFoundException("could not add response to object by id: " + childId + " because it does not exist"));

        // check if child already has a parent
        if (child.getParent() != null) {
            throw new BadRequestException("origin object already has a parent");
        }

        //check that origin and child are not the same object
        if (originId.equals(childId)) {
            throw new BadRequestException("origin object and child object are the same object");
        }


        // add child to origin
        origin.addChildren(child);
        // add origin to child
        child.setParent(origin);

        // save both objects
        this.objectCrudDB.save(origin);
        this.objectCrudDB.save(child);
    }

    @Override
    public List<SuperAppObjectBoundary> getChildren(String originId) {
        // find origin object
        SuperAppObjectEntity superAppObjectEntity = this.objectCrudDB.findById(originId)
                .orElseThrow(() -> new NotFoundException("could not find object by id: " + originId + " because it does not exist")
                );

        // get all children
        Set<SuperAppObjectEntity> children = superAppObjectEntity.getChildren();
        return children
                .stream()
                .map(this::convertEntityToBoundary)
                .toList();
    }

    @Override
    public Optional<SuperAppObjectBoundary> getOrigin(String childId) {
        // find child object
        SuperAppObjectEntity superAppObjectEntity = this.objectCrudDB.findById(childId)
                .orElseThrow(() -> new NotFoundException("could not find object by id: " + childId + " because it does not exist")
                );

        if (superAppObjectEntity.getParent() != null) {
            return Optional.of(superAppObjectEntity.getParent())
                    .map(this::convertEntityToBoundary);
        }

        return Optional.empty();
    }
}
