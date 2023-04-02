package superapp.logic;

import superapp.data.entities.LocationEntity;
import superapp.logic.boundaries.CreatedBy;
import superapp.logic.boundaries.Location;
import superapp.logic.boundaries.ObjectId;
import superapp.logic.boundaries.UserID;

public class ConvertHelp {

    final public static String DELIMITER_ID = "_";


    /**
     * This methode convert CreatedBy to String,
     * CreatedBy String is UserId attr split by DELIMITER_ID
     *
     * @param createdByBoundary
     * @return createdBy String
     */
    public static String createByBoundaryToStr(CreatedBy createdByBoundary) {
        String createdBy = userIdBoundaryToStr(createdByBoundary.getUserId());
        return createdBy;
    }

    /**
     * This methode convert UserId to String,
     * UserId attr split by DELIMITER_ID
     *
     * @param userIdBoundary
     * @return userId String
     */
    public static String userIdBoundaryToStr(UserID userIdBoundary) {
        String userId = userIdBoundary.getSuperapp() + DELIMITER_ID + userIdBoundary.getEmail();
        return userId;
    }

    /**
     * This methode convert ObjectId to String,
     * ObjectId String is ObjectId attr split by DELIMITER_ID
     *
     * @param objectIdBoundary
     * @return objectId String
     */
    public static String objectIdBoundaryToStr(ObjectId objectIdBoundary) {
        String objectId = objectIdBoundary.getSuperApp() + DELIMITER_ID + objectIdBoundary.getInternalObjectId();
        return objectId;
    }

    /**
     * This methode convert LocationEntity to Location boundary
     *
     * @param locationEntity
     * @return locationBoundary Location
     */
    public static Location locationEntityToBoundary(LocationEntity locationEntity) {

        if (locationEntity == null)
            return null;

        Location locationBoundary = new Location();
        locationBoundary.setLat(locationEntity.getLat());
        locationBoundary.setLng(locationEntity.getLng());

        return locationBoundary;
    }


    /**
     * This methode convert String CreatedBy to CreatedBy object boundary
     *
     * @param createdByStr
     * @return createdBy Object
     */
    public static CreatedBy strCreateByToBoundary(String createdByStr) {

        CreatedBy createdBy = new CreatedBy();
        createdBy.setUserId(strUserIdToBoundary(createdByStr));
        return createdBy;
    }

    /**
     * This methode convert String UserID to UserID object boundary
     *
     * @param userId
     * @return UserID Object
     */
    public static UserID strUserIdToBoundary(String userId) {

        String[] attr = userId.split(DELIMITER_ID);
        return new UserID(attr[0], attr[1]);
    }

    /**
     * This methode convert String objectId to objectId object boundary
     *
     * @param objectId
     * @return ObjectId Object
     */
    public static ObjectId strObjectIdToBoundary(String objectId) {

        String[] attr = objectId.split(DELIMITER_ID);
        return new ObjectId(attr[0], attr[1]);
    }

    /**
     * This methode convert Location boundary to LocationEntity
     *
     * @param locationBoundary
     * @return
     */
    public static LocationEntity locationBoundaryToEntity(Location locationBoundary) {

        if (locationBoundary == null)
            return null;

        LocationEntity locationEntity = new LocationEntity();
        locationEntity.setLat(locationBoundary.getLat());
        locationEntity.setLng(locationBoundary.getLng());

        return locationEntity;
    }

    /**
     * This methode concatenate the array of ids to one id.
     * the new id split by DELIMITER_ID
     *
     * @param ids String[]
     * @return id String
     */
    public static String concatenateIds(String[] ids) {

        String id = String.join(DELIMITER_ID, ids);
        return id;
    }
}
