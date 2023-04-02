package superapp.logic;

import superapp.data.entities.LocationEntity;
import superapp.logic.boundaries.CreatedBy;
import superapp.logic.boundaries.Location;
import superapp.logic.boundaries.ObjectId;
import superapp.logic.boundaries.UserID;

public class ConvertHelp {

    final public static String DELIMITER = "_";

    /*
        Boundary To Entity methods
     */
    public static String createByBoundaryToStr(CreatedBy createdByBoundary, String delimiter) {
        return userIdBoundaryToStr(createdByBoundary.getUserId(), delimiter);
    }

    public static String userIdBoundaryToStr(UserID userIdBoundary, String delimiter) {
        return userIdBoundary.getSuperapp() + delimiter + userIdBoundary.getEmail();
    }

    public static String objectIdBoundaryToStr(ObjectId objectIdBoundary, String delimiter) {
        return objectIdBoundary.getSuperApp() + delimiter + objectIdBoundary.getInternalObjectId();
    }

    public static Location locationEntityToBoundary(LocationEntity locationEntity) {

        if (locationEntity == null)
            return null;

        Location locationBoundary = new Location();
        locationBoundary.setLat(locationEntity.getLat());
        locationBoundary.setLng(locationEntity.getLng());

        return locationBoundary;
    }


    /*
        Entity to boundary methods
     */
    public static CreatedBy strCreateByToBoundary(String createdByStr, String delimiter) {

        CreatedBy createdBy = new CreatedBy();
        createdBy.setUserId(strUserIdToBoundary(createdByStr, delimiter));
        return createdBy;
    }

    public static UserID strUserIdToBoundary(String userId, String delimiter) {

        String[] attr = userId.split(delimiter);
        return new UserID(attr[0], attr[1]);
    }

    public static ObjectId strObjectIdToBoundary(String objectId, String delimiter) {

        String[] attr = objectId.split(delimiter);
        return new ObjectId(attr[0], attr[1]);
    }

    public static LocationEntity locationBoundaryToEntity(Location locationBoundary) {

        if (locationBoundary == null)
            return null;

        LocationEntity locationEntity = new LocationEntity();
        locationEntity.setLat(locationBoundary.getLat());
        locationEntity.setLng(locationBoundary.getLng());

        return locationEntity;
    }

    public static String concatenateIds(String[] ids, String delimiter) {

        return String.join(delimiter, ids);
    }
}
