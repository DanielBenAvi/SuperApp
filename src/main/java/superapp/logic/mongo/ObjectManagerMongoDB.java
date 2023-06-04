package superapp.logic.mongo;

import org.springframework.data.geo.Point;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.stereotype.Service;
import superapp.data.*;
import superapp.logic.ObjectsServiceWithPaging;
import superapp.logic.boundaries.Location;
import superapp.logic.boundaries.ObjectId;
import superapp.logic.boundaries.SuperAppObjectBoundary;
import superapp.logic.boundaries.UserId;
import superapp.logic.utils.convertors.ObjectConvertor;
import superapp.logic.utils.validators.BoundaryValidator;
import superapp.logic.utils.validators.EntitiesValidator;

import java.util.*;

@Service
public class ObjectManagerMongoDB implements ObjectsServiceWithPaging {

    private final ObjectCrud objectCrudDB;
    private String springApplicationName;
    private final RBAC accessControl;
    private final ObjectConvertor objectConvertor;
    private final BoundaryValidator boundaryValidator;
    private final EntitiesValidator entitiesValidator;


    @Autowired
    public ObjectManagerMongoDB(ObjectCrud objectCrudDB,
                                RBAC accessControl,
                                ObjectConvertor objectConvertor,
                                BoundaryValidator boundaryValidator,
                                EntitiesValidator entitiesValidator) {
        this.objectCrudDB = objectCrudDB;
        this.accessControl = accessControl;
        this.objectConvertor = objectConvertor;
        this.boundaryValidator = boundaryValidator;
        this.entitiesValidator = entitiesValidator;

    }

    // this method injects a configuration value of spring
    @Value("${spring.application.name:defaultAppName}")
    public void setSpringApplicationName(String springApplicationName) {
        this.springApplicationName = springApplicationName;
    }

    @PostConstruct
    public void init() {
        System.err.println("****** " + this.getClass().getName() + " service initiated");
    }


    /**
     * this method create object and save in database
     *
     * @param objectBoundary SuperAppObjectBoundary
     * @return SuperAppObjectBoundary
     */
    @Override
    public SuperAppObjectBoundary createObject(SuperAppObjectBoundary objectBoundary) {

        // init commandId
        objectBoundary
                .setObjectId(new ObjectId(this.springApplicationName, UUID.randomUUID().toString()))
                .setCreationTimestamp(new Date());

        // validation
        Location location = this.boundaryValidator.validateLocation(objectBoundary.getLocation());
        objectBoundary.setLocation(location);
        this.boundaryValidator.validateObjectBoundary(objectBoundary);
        this.boundaryValidator.validateCreatedBy(objectBoundary.getCreatedBy());

        // validate that user exist and retrieve the user from database
        UserId userId = objectBoundary.getCreatedBy().getUserId();
        UserEntity userEntity = this.entitiesValidator.validateExistingUser(userId.getSuperapp(), userId.getEmail());

        this.checkPermission(userEntity.getUserID(), "createObject");

        // save the object
        SuperAppObjectEntity entity = this.objectCrudDB
                .save(this.objectConvertor.toEntity(objectBoundary));

        return this.objectConvertor
                .toBoundary(entity);
    }

    @Override
    public SuperAppObjectBoundary updateObject(String objectSuperApp, String internalObjectId,
                                               SuperAppObjectBoundary update,
                                               String userSuperapp, String userEmail) {

        // validate that user exist and retrieve the user from database
        UserEntity userEntity = this.entitiesValidator.validateExistingUser(userSuperapp, userEmail);

        this.checkPermission(userEntity.getUserID(), "updateObject");

        // validate that object exists and retrieve the object from database
        SuperAppObjectEntity exists = this.entitiesValidator.validateExistingObject(objectSuperApp, internalObjectId);

        SuperAppObjectBoundary existsBoundary = this.objectConvertor.toBoundary(exists);

        if (update.getType() != null && !update.getType().isEmpty())
            exists.setType(update.getType());

        if (update.getAlias() != null && !update.getAlias().isEmpty())
            exists.setAlias(update.getAlias());

        if (update.getActive() != null)
            exists.setActive(update.getActive());


        if (update.getLocation() != null) {
            Double lng = existsBoundary.getLocation().getLng();
            Double lat = existsBoundary.getLocation().getLat();

            if (update.getLocation().getLng() != null)
                lng = update.getLocation().getLng();

            if (update.getLocation().getLat() != null)
                lat = update.getLocation().getLat();

            Location updateLocation = new Location(lng, lat);
            this.boundaryValidator.validateLocation(updateLocation);
            exists.setLocation(this.objectConvertor.locationToEntity(updateLocation));
        }


        if (update.getObjectDetails() != null)
            exists.setObjectDetails(update.getObjectDetails());

        exists = this.objectCrudDB.save(exists);

        return this.objectConvertor.toBoundary(exists);
    }

    @Override
    public Optional<SuperAppObjectBoundary> getSpecificObject(String objectSuperApp, String internalObjectId,
                                                              String userSuperapp, String userEmail) {

        // validate that user exist and retrieve the user from database
        UserEntity userEntity = this.entitiesValidator.validateExistingUser(userSuperapp, userEmail);

        this.checkPermission(userEntity.getUserID(), "getSpecificObject");

        // validate that object exist in database
        SuperAppObjectEntity objectEntity = this.entitiesValidator.validateExistingObject(objectSuperApp, internalObjectId);

        // UserRole.MINIAPP_USER has permission just for object with active is true
        if (userEntity.getRole().equals(UserRole.MINIAPP_USER))
            return this.objectCrudDB
                    .findByObjectIdAndActiveIsTrue(objectEntity.getObjectId())
                    .map(this.objectConvertor::toBoundary);

        // this is return for User Role SUPERAPP_USER
        return this.objectCrudDB
                .findById(objectEntity.getObjectId())
                .map(this.objectConvertor::toBoundary);

    }


    @Override
    public List<SuperAppObjectBoundary> getAllObjects(String userSuperapp, String userEmail, int size, int page) {

        // validate that user exist and retrieve the user from database
        UserEntity userEntity = this.entitiesValidator.validateExistingUser(userSuperapp, userEmail);

        this.checkPermission(userEntity.getUserID(), "getAllObjects");

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "creationTimestamp", "type", "objectId");
        // UserRole.MINIAPP_USER has permission just for object with active is true
        if (userEntity.getRole().equals(UserRole.MINIAPP_USER))
            return this.objectCrudDB
                    .findAllByActiveIsTrue(pageRequest)
                    .stream()
                    .map(this.objectConvertor::toBoundary)
                    .toList();

        // this is return for User Role SUPERAPP_USER
        return this.objectCrudDB
                .findAll(pageRequest)
                .stream()
                .map(this.objectConvertor::toBoundary)
                .toList();
    }

    @Override
    public void addChild(String superapp, String parentId, ObjectId childId, String userSuperapp, String userEmail) {

        // validate that user exist and retrieve the user from database
        UserEntity userEntity = this.entitiesValidator.validateExistingUser(userSuperapp, userEmail);

        this.checkPermission(userEntity.getUserID(), "addChild");

        // validate that parent and child objects exist in database
        SuperAppObjectEntity parent = this.entitiesValidator.validateExistingObject(superapp, parentId);
        SuperAppObjectEntity child = this.entitiesValidator.validateExistingObject(childId.getSuperapp(),
                childId.getInternalObjectId());

        if (parentId.equals(childId.getInternalObjectId()))
            throw new ConflictRequestException("Parent and child are the same object");

        if (child.getParent() != null)
            throw new BadRequestException("Child already has a parent");

        parent.addChildren(child);
        child.setParent(parent);

        this.objectCrudDB.save(parent);
        this.objectCrudDB.save(child);
    }

    @Override
    public List<SuperAppObjectBoundary> getChildren(String superapp, String parentInternalObjectId,
                                                    String userSuperapp, String userEmail, int size, int page) {

        // validate that user exist and retrieve the user from database
        UserEntity user = this.entitiesValidator.validateExistingUser(userSuperapp, userEmail);

        this.checkPermission(user.getUserID(), "getChildren");

        // validate that parent object exist in database
        SuperAppObjectEntity parent = this.entitiesValidator.validateExistingObject(superapp, parentInternalObjectId);


        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "creationTimestamp", "type", "objectId");
        // UserRole.MINIAPP_USER has permission to retrieve children of parent object
        // when active of parent is true
        if (user.getRole().equals(UserRole.MINIAPP_USER))
            return this.objectCrudDB
                    .findAllByParent_objectIdAndActiveIsTrue(parent.getObjectId(), pageRequest)
                    .stream()
                    .map(this.objectConvertor::toBoundary)
                    .toList();

        return this.objectCrudDB
                .findAllByParent_objectId(parent.getObjectId(), pageRequest)
                .stream()
                .map(this.objectConvertor::toBoundary)
                .toList();
    }

    @Override
    public List<SuperAppObjectBoundary> getParent(String superapp, String childInternalObjectId,
                                                  String userSuperapp, String userEmail, int size, int page) {

        // validate that user exist and retrieve the user from database
        UserEntity user = this.entitiesValidator.validateExistingUser(userSuperapp, userEmail);

        this.checkPermission(user.getUserID(), "getParent");

        // validate that child object exist in database
        SuperAppObjectEntity child = this.entitiesValidator.validateExistingObject(superapp, childInternalObjectId);


        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "creationTimestamp", "type", "objectId");
        // UserRole.MINIAPP_USER has permission to retrieve parent of child object
        // when active of child is true
        if (user.getRole().equals(UserRole.MINIAPP_USER))
            return this.objectCrudDB
                    .findAllByChildren_objectIdAndActiveIsTrue(child.getObjectId(), pageRequest)
                    .stream()
                    .map(this.objectConvertor::toBoundary)
                    .toList();

        // this is return for User Role SUPERAPP_USER
        return this.objectCrudDB
                .findAllByChildren_objectId(child.getObjectId(), pageRequest)
                .stream()
                .map(this.objectConvertor::toBoundary)
                .toList();
    }

    @Override
    public void deleteAllObjects(String userSuperapp, String userEmail) {

        // validate that user exist and retrieve the user from database
        UserEntity userEntity = this.entitiesValidator.validateExistingUser(userSuperapp, userEmail);

        this.checkPermission(userEntity.getUserID(), "deleteAllObjects");

        this.objectCrudDB.deleteAll();
    }

    @Override
    public List<SuperAppObjectBoundary> getAllObjectsByType(String type, String userSuperapp, String userEmail, int size, int page) {

        // validate that user exist and retrieve the user from database
        UserEntity user = this.entitiesValidator.validateExistingUser(userSuperapp, userEmail);

        this.checkPermission(user.getUserID(), "getAllObjectsByType");

        // TODO is necessary?
//        if (type == null || type.isEmpty())
//            throw new BadRequestException("type must include some word");

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "creationTimestamp", "objectId");

        // UserRole.MINIAPP_USER has permission to retrieve objects with active is true
        if (user.getRole().equals(UserRole.MINIAPP_USER))
            return this.objectCrudDB
                    .findAllByTypeAndActiveIsTrue(type, pageRequest)
                    .stream()
                    .map(this.objectConvertor::toBoundary)
                    .toList();

        // this is return for User Role SUPERAPP_USER
        return this.objectCrudDB
                .findAllByType(type, pageRequest)
                .stream()
                .map(this.objectConvertor::toBoundary)
                .toList();
    }

    @Override
    public List<SuperAppObjectBoundary> getAllObjectsByAlias(String alias, String userSuperapp, String userEmail, int size, int page) {

        // validate that user exist and retrieve the user from database
        UserEntity user = this.entitiesValidator.validateExistingUser(userSuperapp, userEmail);

        this.checkPermission(user.getUserID(), "getAllObjectsByAlias");

        // TODO is necessary?
//        if (alias == null || alias.isEmpty())
//            throw new BadRequestException("alias must include some content");

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "creationTimestamp", "objectId");
        // UserRole.MINIAPP_USER has permission to retrieve parent of child object with active is true
        if (user.getRole().equals(UserRole.MINIAPP_USER))
            return this.objectCrudDB
                    .findAllByAliasAndActiveIsTrue(alias, pageRequest)
                    .stream()
                    .map(this.objectConvertor::toBoundary)
                    .toList();

        // this is return for User Role SUPERAPP_USER
        return this.objectCrudDB
                .findAllByAlias(alias, pageRequest)
                .stream()
                .map(this.objectConvertor::toBoundary)
                .toList();
    }

    @Override
    public List<SuperAppObjectBoundary> getAllObjectsByLocation(String lat, String lng, String distance, String distanceUnits,
                                                                String userSuperapp, String userEmail, int size, int page) {

        // validate that user exist and retrieve the user from database
        UserEntity userEntity = this.entitiesValidator.validateExistingUser(userSuperapp, userEmail);

        this.checkPermission(userEntity.getUserID(), "getAllObjectsByLocation");

        // parse lat, lng and distance
        double latitude, longitude, distanceRange;
        try {
            latitude = Double.parseDouble(lat);
            longitude = Double.parseDouble(lng);
            distanceRange = Double.parseDouble(distance);
            if (distanceRange < 0)
                throw new BadRequestException("distance must be positive number");
        } catch (Exception e) {
            throw new BadRequestException("lat, lng, distance values must be a numbers");
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "creationTimestamp", "objectId");
        Point point = new Point(latitude, longitude);
        Distance maxDistance = new Distance(distanceRange, convertToMetrics(distanceUnits));

        // UserRole.MINIAPP_USER has permission to retrieve objects with active is true
        if (userEntity.getRole().equals(UserRole.MINIAPP_USER))
            return this.objectCrudDB
                    .findAllByActiveIsTrueAndLocationNear(point, maxDistance, pageRequest)
                    .stream()
                    .map(this.objectConvertor::toBoundary)
                    .toList();

        // this is return for User Role SUPERAPP_USER
        return this.objectCrudDB
                .findAllByLocationNear(point, maxDistance, pageRequest)
                .stream()
                .map(this.objectConvertor::toBoundary)
                .toList();
    }

    private Metrics convertToMetrics(String units) {
        return switch (units.toLowerCase()) {
            case "kilometers" -> Metrics.KILOMETERS;
            case "miles" -> Metrics.MILES;
            case "neutral" -> Metrics.NEUTRAL;
            default -> throw new BadRequestException("Invalid units: " + units);
        };
    }

    private void checkPermission(String userId, String operationName) {
        // check role permission
        if (accessControl.hasPermission(userId, operationName))
            throw new UnauthorizedRequestException("User " + userId + " has no permission to " + operationName);
    }

    /**** Deprecated methods *****/
    @Override
    @Deprecated
    public SuperAppObjectBoundary updateObject(String objectSuperApp, String internalObjectId, SuperAppObjectBoundary update) {
        throw new DeprecatedRequestException("cannot enter a deprecated function");

        //        String objectId = ConvertHelp.concatenateIds(new String[]{objectSuperApp, internalObjectId});
        //
        //        SuperAppObjectEntity exists = this.objectCrudDB.findById(objectId).
        //                orElseThrow(() -> new NotFoundException("could not update object by id: " + objectId + " because it does not exist"));
        //        SuperAppObjectBoundary existsBoundary = convertEntityToBoundary(exists);
        //
        //        if (exists == null) throw new BadRequestException("Could not find object by id: " + objectId);
        //
        //        boolean dirty_flag = false;
        //        if (update.getType() != null) {
        //            throw new BadRequestException();
        //        }
        //
        //        if (update.getAlias() != null ) {
        //            if (update.getAlias().isEmpty())
        //                throw new BadRequestException();
        //
        //            exists.setAlias(update.getAlias());
        //            dirty_flag = true;
        //        }
        //
        //        if (update.getActive() != null) {
        //            exists.setActive(update.getActive());
        //            dirty_flag = true;
        //        }
        //
        //        if (update.getLocation() != null) {
        //            if (update.getLocation().getLng() == 0.0 && update.getLocation().getLat() != 0.0) {
        //                exists.setLocation(ConvertHelp.locationBoundaryToStr(update.getLocation()
        //                        .setLng(existsBoundary.getLocation().getLng())));
        //                dirty_flag = true;
        //            }
        //            else if (update.getLocation().getLng() != 0.0 && update.getLocation().getLat() == 0.0) {
        //                exists.setLocation(ConvertHelp.locationBoundaryToStr(update.getLocation()
        //                        .setLat(existsBoundary.getLocation().getLat())));
        //                dirty_flag = true;
        //            }
        //            else if (update.getLocation().getLng() != 0.0 && update.getLocation().getLat() != 0.0){
        //                exists.setLocation(ConvertHelp.locationBoundaryToStr(update.getLocation()));
        //                dirty_flag = true;
        //            }
        //
        //        }
        //
        //        if (update.getObjectDetails() != null) {
        //            for (Map.Entry<String, Object> entry :update.getObjectDetails().entrySet()) {
        //                if (entry.getKey() != null && entry.getValue() != null &&
        //                        exists.getObjectDetails().containsKey(entry.getKey())) {
        //                    exists.getObjectDetails().put(entry.getKey(), entry.getValue());
        //                    dirty_flag = true;
        //                }
        //            }
        //        }
        //
        //        if (dirty_flag) exists = this.objectCrudDB.save(exists);
        //
        //        return this.convertEntityToBoundary(exists);
    }

    @Override
    @Deprecated
    public Optional<SuperAppObjectBoundary> getSpecificObject(String objectSuperApp, String internalObjectId) {
        throw new DeprecatedRequestException("cannot enter a deprecated function");
        //        String objectId = ConvertHelp.concatenateIds(new String[]{objectSuperApp, internalObjectId});
        //        if (!this.objectCrudDB.existsById(objectId))
        //            throw new NotFoundException();
        //
        //        return this.objectCrudDB.findById(objectId).map(this::convertEntityToBoundary);
    }

    @Override
    @Deprecated
    public List<SuperAppObjectBoundary> getAllObjects() {
        throw new DeprecatedRequestException("cannot enter a deprecated function");
//        return this.objectCrudDB.findAll().stream().map(this::convertEntityToBoundary).toList();
    }

    @Override
    @Deprecated
    public void deleteAllObjects() {
        throw new DeprecatedRequestException("cannot enter a deprecated function");
        //this.objectCrudDB.deleteAll();
    }

    @Override
    @Deprecated
    public void addChild(String superApp, String parentId, ObjectId childId) {
        throw new DeprecatedRequestException("cannot enter a deprecated function");
        //        if (!checkValidSuperApp(superApp))
        //            throw new BadRequestException("superApp must be in format: " + springApplicationName);
        //
        //        if (!checkValidSuperApp(childId.getSuperapp()))
        //            throw new BadRequestException("childId must be in format: " + springApplicationName + "#" + UUID.randomUUID().toString());
        //
        //        if (!checkValidInternalObjectId(parentId))
        //            throw new BadRequestException("parentId must be in format: " + springApplicationName + "#" + UUID.randomUUID().toString());
        //
        //        if (!checkValidInternalObjectId(childId.getInternalObjectId()))
        //            throw new BadRequestException("childId must be in format: " + springApplicationName + "#" + UUID.randomUUID().toString());
        //
        //        if (parentId.equals(childId.getInternalObjectId()))
        //            throw new ConflictRequestException("origin and child are the same object");
        //
        //        SuperAppObjectEntity parent = this.objectCrudDB.findById(ConvertHelp.concatenateIds(new String[]{superApp, parentId})).orElseThrow(() -> new NotFoundException("could not add child to object by id: " + parentId + " because it does not exist"));
        //        SuperAppObjectEntity child = this.objectCrudDB.findById(ConvertHelp.objectIdBoundaryToStr(childId)).orElseThrow(() -> new NotFoundException("could not add child to object by id: " + childId.toString() + " because it does not exist"));
        //
        //        if (child.getParent() != null) throw new BadRequestException("child already has a parent");
        //
        //        parent.addChildren(child);
        //        child.setParent(parent);
        //
        //        this.objectCrudDB.save(parent);
        //        this.objectCrudDB.save(child);

    }

    @Override
    @Deprecated
    public List<SuperAppObjectBoundary> getChildren(String superapp, String parentInternalObjectId) {

        throw new DeprecatedRequestException("cannot enter a deprecated function");

        //        if (!checkValidSuperApp(superapp))
        //            throw new BadRequestException("superApp must be in format: " + springApplicationName);
        //
        //        if (!checkValidInternalObjectId(parentInternalObjectId))
        //            throw new BadRequestException("parentId must be in format: " + springApplicationName + "_" + UUID.randomUUID().toString());
        //
        //        SuperAppObjectEntity origin = this.objectCrudDB.findById(ConvertHelp.concatenateIds(new String[]{superapp, parentInternalObjectId})).orElseThrow(() -> new NotFoundException("could not get children of object by id: " + parentInternalObjectId.toString() + " because it does not exist"));
        //        Set<SuperAppObjectEntity> children = origin.getChildren();
        //        return children.stream().map(this::convertEntityToBoundary).toList();
    }

    @Override
    @Deprecated
    public List<SuperAppObjectBoundary> getParent(String superapp, String childInternalObjectId) {

        throw new DeprecatedRequestException("cannot enter a deprecated function");

        //        if (!checkValidSuperApp(superapp))
        //            throw new BadRequestException("superApp must be in format: " + springApplicationName);
        //
        //        if (!checkValidInternalObjectId(childInternalObjectId))
        //            throw new BadRequestException("parentId must be in format: " + springApplicationName + "#" + UUID.randomUUID().toString());
        //
        //        SuperAppObjectEntity child = this.objectCrudDB.findById(ConvertHelp.concatenateIds(new String[]{superapp, childInternalObjectId})).orElseThrow(() -> new NotFoundException("could not get origin of object by id: " + childInternalObjectId.toString() + " because it does not exist"));
        //
        //        if (child.getParent() != null) {
        //            return Optional.of(child.getParent())
        //                    .map(this::convertEntityToBoundary).stream().toList();
        //        }
        //
        //        return List.of();
    }

}
