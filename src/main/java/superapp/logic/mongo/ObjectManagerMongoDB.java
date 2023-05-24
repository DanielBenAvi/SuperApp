package superapp.logic.mongo;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import superapp.data.*;
import superapp.logic.ConvertHelp;
import superapp.logic.ObjectsServiceWithPaging;
import superapp.logic.boundaries.CreatedBy;
import superapp.logic.boundaries.Location;
import superapp.logic.boundaries.ObjectId;
import superapp.logic.boundaries.SuperAppObjectBoundary;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class ObjectManagerMongoDB implements ObjectsServiceWithPaging {

    private ObjectCrud objectCrudDB;
    private String springApplicationName;
    private final UserCrud userCrud;
    private RBAC accessControl;

    @Autowired
    public ObjectManagerMongoDB(ObjectCrud objectCrudDB,UserCrud userCrud, RBAC accessControl) {
        this.objectCrudDB = objectCrudDB;
        this.userCrud = userCrud;
        this.accessControl = accessControl;
    }

    // this method injects a configuration value of spring
    @Value("${spring.application.name:defaultAppName}")
    public void setSpringApplicationName(String springApplicationName) {
        this.springApplicationName = springApplicationName;
    }

    // this method is invoked after values are injected to instance
    @PostConstruct
    public void init() {
        System.err.println("****** " + this.getClass().getName() + " service initiated");
    }

    @Override
    public SuperAppObjectBoundary createObject(SuperAppObjectBoundary objectBoundary) {

        validateEntireObjectBoundary(objectBoundary);


        String userId = ConvertHelp.concatenateIds(
                new String[]{ objectBoundary.getCreatedBy().getUserId().getSuperapp(),
                              objectBoundary.getCreatedBy().getUserId().getEmail()});

        if(!userCrud.existsById(userId))
            throw new NotFoundException("user " + userId + " not found in database");

        if (!accessControl.hasPermission(userId, "createObject"))
            throw new UnauthorizedRequestException("user " + userId + " has no permission to createObject");


        SuperAppObjectEntity entity = this.convertBoundaryToEntity(objectBoundary);

        String objectId = ConvertHelp.concatenateIds(new String[]{springApplicationName, UUID.randomUUID().toString()});

        entity
                .setObjectId(objectId)
                .setCreationTimestamp(new Date());

        this.objectCrudDB.save(entity);

        return this.convertEntityToBoundary(entity);

    }

    @Override
    public SuperAppObjectBoundary updateObject(String objectSuperApp, String internalObjectId,
                                               SuperAppObjectBoundary update, String userSuperapp, String userEmail) {

        String userId = ConvertHelp.concatenateIds(new String[]{userSuperapp, userEmail});
        if(!userCrud.existsById(userId))
            throw new NotFoundException("user " + userId + " not found in database");

        if (!accessControl.hasPermission(userId, "updateObject"))
            throw new UnauthorizedRequestException("user " + userId + " has no permission to updateObject");

        validateSuperappNameAndInternalObjectId(objectSuperApp, internalObjectId);

        String objectId = ConvertHelp.concatenateIds(new String[]{objectSuperApp, internalObjectId});

        SuperAppObjectEntity exists = this.objectCrudDB.findById(objectId).
                orElseThrow(() -> new NotFoundException("could not update object by id: " + objectId + " because it does not exist"));
        SuperAppObjectBoundary existsBoundary = convertEntityToBoundary(exists);

        if (update.getType() != null && !update.getType().isEmpty()) {
            exists.setType(update.getType());
        }

        if (update.getAlias() != null && !update.getAlias().isEmpty()) {
            exists.setAlias(update.getAlias());
        }

        if (update.getActive() != null) {
            exists.setActive(update.getActive());
        }

        // TODO - maybe to validate the lng and lat degrees (lng: -180 to 180, lat: -90 to 90)
        if (update.getLocation() != null) {
            double lng = existsBoundary.getLocation().getLng();
            double lat = existsBoundary.getLocation().getLat();

            if (update.getLocation().getLng() != null) {
                lng = update.getLocation().getLng();
            }

            if (update.getLocation().getLat() != null) {
                lat = update.getLocation().getLat();
            }

            exists.setLocation(ConvertHelp.locationBoundaryToEntity(new Location(lng, lat)));
        }

        if (update.getObjectDetails() != null) {
            exists.getObjectDetails().clear();
            for (Map.Entry<String, Object> entry : update.getObjectDetails().entrySet()) {
                if (entry.getValue() != null) {
                    exists.getObjectDetails().put(entry.getKey(), entry.getValue());
                }
            }
        }

        exists = this.objectCrudDB.save(exists);

        return this.convertEntityToBoundary(exists);
    }

    @Override
    public Optional<SuperAppObjectBoundary> getSpecificObject(String objectSuperApp, String internalObjectId,
                                                              String userSuperapp, String userEmail) {

        String userId = ConvertHelp.concatenateIds(new String[]{userSuperapp, userEmail});

        UserEntity user = this.userCrud
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("user " + userId + " not found in database"));

        if (!accessControl.hasPermission(userId, "getSpecificObject"))
            throw new UnauthorizedRequestException("user " + userId + " has no permission to getSpecificObject");

        validateSuperappNameAndInternalObjectId(objectSuperApp, internalObjectId);
        String objectId = ConvertHelp.concatenateIds(new String[]{objectSuperApp, internalObjectId});
        if (!this.objectCrudDB.existsById(objectId))
            throw new NotFoundException("object " + objectId + " not found in database");

        // UserRole.MINIAPP_USER has permission just for object with active is true
        if (user.getRole().equals(UserRole.MINIAPP_USER))
            return  this.objectCrudDB
                    .findByObjectIdAndActiveIsTrue(objectId)
                    .map(this::convertEntityToBoundary);

        // this is return for User Role SUPERAPP_USER
        return this.objectCrudDB
                    .findById(objectId)
                    .map(this::convertEntityToBoundary);
    }


    @Override
    public List<SuperAppObjectBoundary> getAllObjects(String userSuperapp, String userEmail, int size, int page) {

        String userId = ConvertHelp.concatenateIds(new String[]{userSuperapp, userEmail});

        UserEntity user = this.userCrud
                        .findById(userId)
                        .orElseThrow(() -> new NotFoundException("user " + userId + " not found in database"));


        if (!accessControl.hasPermission(userId, "getAllObjects"))
            throw new UnauthorizedRequestException("user " + userId + " has no permission to getAllObjects");


        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "creationTimestamp", "type", "objectId");

        // UserRole.MINIAPP_USER has permission just for object with active is true
        if (user.getRole().equals(UserRole.MINIAPP_USER))
            return this.objectCrudDB
                    .findAllByActiveIsTrue(pageRequest)
                    .stream()
                    .map(this::convertEntityToBoundary)
                    .toList();

        // this is return for User Role SUPERAPP_USER
        return this.objectCrudDB
                .findAll(pageRequest)
                .stream()
                .map(this::convertEntityToBoundary)
                .toList();
    }

    @Override
    public void addChild(String superapp, String parentId, ObjectId childId, String userSuperapp, String userEmail) {

        String userId = ConvertHelp.concatenateIds(new String[]{userSuperapp, userEmail});
        if(!userCrud.existsById(userId))
            throw new NotFoundException("user " + userId + "not found in database");

        if (!accessControl.hasPermission(userId, "addChild"))
            throw new UnauthorizedRequestException("user " + userId + " has no permission to addChild");


        validateSuperappNameAndInternalObjectId(superapp, parentId);
        validateSuperappNameAndInternalObjectId(childId.getSuperapp(), childId.getInternalObjectId());

        if (parentId.equals(childId.getInternalObjectId()))
            throw new ConflictRequestException("origin and child are the same object");

        SuperAppObjectEntity parent = this.objectCrudDB
                .findById(ConvertHelp.concatenateIds(new String[]{superapp, parentId}))
                .orElseThrow(() ->
                        new NotFoundException("could not add child to object by id: " + parentId + " because it does not exist"));

        SuperAppObjectEntity child = this.objectCrudDB
                .findById(ConvertHelp.objectIdBoundaryToStr(childId))
                .orElseThrow(() ->
                        new NotFoundException("could not add child to object by id: " + childId + " because it does not exist"));

        if (child.getParent() != null)
            throw new BadRequestException("child already has a parent");

        parent.addChildren(child);
        child.setParent(parent);

        this.objectCrudDB.save(parent);
        this.objectCrudDB.save(child);

    }

    @Override
    public List<SuperAppObjectBoundary> getChildren(String superapp, String parentInternalObjectId,
                                                    String userSuperapp, String userEmail, int size, int page) {


        String userId = ConvertHelp.concatenateIds(new String[]{userSuperapp, userEmail});

        UserEntity user = this.userCrud
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("user " + userId + " not found in database"));

        if (!accessControl.hasPermission(userId, "getChildren"))
            throw new UnauthorizedRequestException("user " + userId + " has no permission to getChildren");

        validateSuperappNameAndInternalObjectId(superapp, parentInternalObjectId);

        String parentObjectId = ConvertHelp.concatenateIds(new String[]{superapp, parentInternalObjectId});
        if (!this.objectCrudDB.existsById(parentObjectId))
            throw new NotFoundException("object " + parentObjectId + " not found in database");

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC,"creationTimestamp", "type", "objectId");

        // UserRole.MINIAPP_USER has permission to retrieve children of parent object with active is true
        if (user.getRole().equals(UserRole.MINIAPP_USER))
            return this.objectCrudDB
                    .findAllByParent_objectIdAndActiveIsTrue(parentObjectId, pageRequest)
                    .stream()
                    .map(this::convertEntityToBoundary)
                    .toList();

        return this.objectCrudDB
                .findAllByParent_objectId(parentObjectId, pageRequest)
                .stream()
                .map(this::convertEntityToBoundary)
                .toList();
    }

    @Override
    public List<SuperAppObjectBoundary> getParent(String superapp, String childInternalObjectId,
                                                  String userSuperapp, String userEmail, int size, int page) {

        String userId = ConvertHelp.concatenateIds(new String[]{userSuperapp, userEmail});
        UserEntity user = this.userCrud
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("user " + userId + " not found in database"));

        if (!accessControl.hasPermission(userId, "getParent"))
            throw new UnauthorizedRequestException("user " + userId + " has no permission to getParent");

        validateSuperappNameAndInternalObjectId(superapp, childInternalObjectId);

        String childObjectId = ConvertHelp.concatenateIds(new String[]{superapp, childInternalObjectId});

        if (!this.objectCrudDB.existsById(childObjectId))
            throw new NotFoundException("object " + childObjectId + " not found in database");

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC,"creationTimestamp", "type", "objectId");

        // UserRole.MINIAPP_USER has permission to retrieve parent of child object with active is true
        if (user.getRole().equals(UserRole.MINIAPP_USER))
            return this.objectCrudDB
                    .findAllByChildren_objectIdAndActiveIsTrue(childObjectId, pageRequest)
                    .stream()
                    .map(this::convertEntityToBoundary)
                    .toList();

        // this is return for User Role SUPERAPP_USER
        return this.objectCrudDB
                .findAllByChildren_objectId(childObjectId, pageRequest)
                .stream()
                .map(this::convertEntityToBoundary)
                .toList();
    }
    @Override
    public void deleteAllObjects(String userSuperapp,String userEmail) {

        String userId = ConvertHelp.concatenateIds(new String[]{userSuperapp, userEmail});
        if (!accessControl.hasPermission(userId, "deleteAllObjects"))
            throw new UnauthorizedRequestException("user " + userId + " has no permission to deleteAllObjects");

        this.objectCrudDB.deleteAll();
    }

    @Override
    public List<SuperAppObjectBoundary> getAllObjectsByType(String type, String userSuperapp, String userEmail, int size, int page) {

        String userId = ConvertHelp.concatenateIds(new String[]{userSuperapp, userEmail});
        UserEntity user = this.userCrud
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("user " + userId + " not found in database"));

        if (!accessControl.hasPermission(userId, "getAllObjectsByType"))
            throw new UnauthorizedRequestException("user " + userId + " has no permission to getAllObjectsByType");


        if (type == null || type.isEmpty())
            throw new BadRequestException("type must include some word");

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC,"creationTimestamp", "objectId");

        // UserRole.MINIAPP_USER has permission to retrieve parent of child object with active is true
        if (user.getRole().equals(UserRole.MINIAPP_USER))
            return this.objectCrudDB
                    .findAllByTypeAndActiveIsTrue(type, pageRequest)
                    .stream()
                    .map(this::convertEntityToBoundary)
                    .toList();

        // this is return for User Role SUPERAPP_USER
        return this.objectCrudDB
                .findAllByType(type, pageRequest)
                .stream()
                .map(this::convertEntityToBoundary)
                .toList();
    }

    @Override
    public List<SuperAppObjectBoundary> getAllObjectsByAlias(String alias, String userSuperapp, String userEmail, int size, int page) {

        String userId = ConvertHelp.concatenateIds(new String[]{userSuperapp, userEmail});
        UserEntity user = this.userCrud
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("user " + userId + " not found in database"));

        if (!accessControl.hasPermission(userId, "getAllObjectsByAlias"))
            throw new UnauthorizedRequestException("user " + userId + " has no permission to getAllObjectsByAlias");


        if (alias == null || alias.isEmpty())
            throw new BadRequestException("alias must include some word");

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC,"creationTimestamp", "objectId");

        // UserRole.MINIAPP_USER has permission to retrieve parent of child object with active is true
        if (user.getRole().equals(UserRole.MINIAPP_USER))
            return this.objectCrudDB
                    .findAllByAliasAndActiveIsTrue(alias, pageRequest)
                    .stream()
                    .map(this::convertEntityToBoundary)
                    .toList();

        // this is return for User Role SUPERAPP_USER
        return this.objectCrudDB
                .findAllByAlias(alias, pageRequest)
                .stream()
                .map(this::convertEntityToBoundary)
                .toList();
    }

    @Override
    public List<SuperAppObjectBoundary> getAllObjectsByLocation(String lat, String lng, String distance, String distanceUnits,
                                                                String userSuperapp, String userEmail, int size, int page) {


        double latitude, longitude, distanceRange;

        try {
            latitude = Double.parseDouble(lat);
            longitude = Double.parseDouble(lng);
            distanceRange = Double.parseDouble(distance);
        }catch (Exception e) {
            throw new BadRequestException("lat, lng, distance values must be a numbers");
        }

        if (distanceRange < 0)
            throw new BadRequestException("distance must be positive number");


        String userId = ConvertHelp.concatenateIds(new String[]{userSuperapp, userEmail});
        UserEntity user = this.userCrud
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("user " + userId + " not found in database"));

        if (!accessControl.hasPermission(userId, "getAllObjectsByAlias"))
            throw new UnauthorizedRequestException("user " + userId + " has no permission to getAllObjectsByAlias");


        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC,"creationTimestamp", "objectId");

//        Distance distance1 = new Distance(distanceRange, Metrics.KILOMETERS);

        double distanceIncludeUnits = convertDistance(distanceRange, distanceUnits);

        if (user.getRole().equals(UserRole.MINIAPP_USER))
            return this.objectCrudDB
                    .findAllByLocationNearAndActiveIsTrue(latitude, longitude , distanceIncludeUnits, pageRequest)
                    .stream()
                    .map(this::convertEntityToBoundary)
                    .toList();

        // this is return for User Role SUPERAPP_USER
        return this.objectCrudDB
                .findAllByLocationNear(latitude, longitude , distanceIncludeUnits, pageRequest)
                .stream()
                .map(this::convertEntityToBoundary)
                .toList();
    }

    private double convertDistance(double distance, String units) {
        return switch (units.toLowerCase()) {
            case "kilometers" -> distance * 1000;
            case "miles" -> distance * 1609.34;
            case "neutral" -> distance;
            default -> throw new BadRequestException("Invalid units: " + units);
        };
    }
    private void validateSuperappNameAndInternalObjectId(String superapp, String internalObjectId) {
        if (!checkValidSuperApp(superapp))
            throw new BadRequestException("superApp must be in format: " + springApplicationName);

        if (!checkValidInternalObjectId(internalObjectId))
            throw new BadRequestException
                    ("parentId must be in format: " + springApplicationName + "_" + UUID.randomUUID());
    }

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

    private boolean isCreateByExist(CreatedBy createdBy) {

        if (createdBy == null) return false;

        if (createdBy.getUserId() == null) return false;

        return createdBy.getUserId().getSuperapp() != null
                && !createdBy.getUserId().getSuperapp().isEmpty()
                && createdBy.getUserId().getEmail() != null
                && !createdBy.getUserId().getEmail().isEmpty();
    }

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

    private SuperAppObjectBoundary convertEntityToBoundary(SuperAppObjectEntity entity) {

        SuperAppObjectBoundary boundary = new SuperAppObjectBoundary();

        boundary.setObjectId(ConvertHelp.strObjectIdToBoundary(entity.getObjectId()));
        boundary.setType(entity.getType());
        boundary.setAlias(entity.getAlias());
        boundary.setActive(entity.getActive());
        boundary.setCreationTimestamp(entity.getCreationTimestamp());
        boundary.setLocation(ConvertHelp.locationEntityToBoundary(entity.getLocation()));
        boundary.setCreatedBy(ConvertHelp.strCreateByToBoundary(entity.getCreatedBy()));

        boundary.setObjectDetails(entity.getObjectDetails());

        return boundary;
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

        return superApp.equals(springApplicationName);
    }

    private boolean checkValidLocation(Location location) {
        return location != null && location.getLng() != null && location.getLat() != null;
    }

    private void validateEntireObjectBoundary(SuperAppObjectBoundary objectBoundary) {
        if (objectBoundary.getType() == null || objectBoundary.getType().isEmpty())
            throw new BadRequestException("object must contain all fields");

        if (objectBoundary.getAlias() == null || objectBoundary.getAlias().isEmpty())
            throw new BadRequestException("object must contain all fields");

        if (!checkValidLocation(objectBoundary.getLocation()))
            throw new BadRequestException("object must contain all fields");

        if (objectBoundary.getObjectDetails() == null)
            throw new BadRequestException("object must contain all fields");

        if (objectBoundary.getCreatedBy()== null)
            throw new BadRequestException("object must contain all fields");

        if (objectBoundary.getCreatedBy().getUserId()== null)
            throw new BadRequestException("object must contain all fields");

        if (!isCreateByExist(objectBoundary.getCreatedBy()))
            throw new BadRequestException("object must contain all fields");

        if (!isValidEmail(objectBoundary.getCreatedBy().getUserId().getEmail()))
            throw new BadRequestException("object must contain all fields");

        if (!checkValidSuperApp(objectBoundary.getCreatedBy().getUserId().getSuperapp()))
            throw new BadRequestException("object must contain all fields");

    }

    private boolean isValidEmail(String email) {

        if (email == null)
            return false;

        String emailRegex = "^[a-zA-Z0-9+&*-]+(?:\\."
                + "[a-zA-Z0-9+&*-]+)*@"
                + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
                + "A-Z]{2,7}$";

        Pattern emailPattern = Pattern.compile(emailRegex);

        // check email format
        return emailPattern.matcher(email).matches();
    }

}
