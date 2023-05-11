package superapp.logic;

import superapp.data.UserRole;
import superapp.logic.boundaries.CommandId;
import superapp.logic.boundaries.CreatedBy;
import superapp.logic.boundaries.InvokedBy;
import superapp.logic.boundaries.Location;
import superapp.logic.boundaries.ObjectId;
import superapp.logic.boundaries.TargetObject;
import superapp.logic.boundaries.UserId;

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
    public static String userIdBoundaryToStr(UserId userIdBoundary) {
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
        String objectId = objectIdBoundary.getSuperapp() + DELIMITER_ID + objectIdBoundary.getInternalObjectId();
        return objectId;
    }

    /**
     * This methode convert Location boundary to Location Entity as String
     *
     * @param locationBoundary Location
     * @return locationEntity String
     */
    public static String locationBoundaryToStr(Location locationBoundary) {

        if (locationBoundary == null)
            return null;

        String locationEntity = locationBoundary.getLat() + DELIMITER_ID + locationBoundary.getLng();

        return locationEntity;
    }

    /**
     * This methode convert LocationEntity as String to Location boundary
     *
     * @param locationEntity String
     * @return locationBoundary Location
     */
    public static Location strLocationEntityToBoundary(String locationEntity) {

        if (locationEntity == null)
            return null;

        String[] attr = locationEntity.split(DELIMITER_ID);

        return new Location(Double.parseDouble(attr[0]), Double.parseDouble(attr[1]));

    }


    /**
     * This methode convert String CreatedBy to CreatedBy object boundary
     *
     * @param createdByStr String
     * @return CreatedBy
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
    public static UserId strUserIdToBoundary(String userId) {
        System.err.println("LOG: strUserIdToBoundary: " + userId);
        String[] attr = userId.split(DELIMITER_ID);

        return new UserId(attr[0], attr[1]);
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
    
    public static CommandId strCommandIdToBoundary(String cmdId) {
    	String[] str = cmdId.split(DELIMITER_ID);
    	return new CommandId(str[0],str[1],str[2]);
    }

    
    public static String targetObjBoundaryToStr(TargetObject trgObj) {
    	String targetObject = objectIdBoundaryToStr(trgObj.getObjectId());
    	return targetObject;
    }
    
    public static InvokedBy strInvokedByToBoundary(String strInvoke) {
        System.err.println("LOG: strInvokedByToBoundary: " + strInvoke);
    	InvokedBy invokedBy = new InvokedBy();
        invokedBy.setUserId(strUserIdToBoundary(strInvoke));
    	return invokedBy;
    }
    
    public static String invokedByBoundaryToStr(InvokedBy invokedByBoundary) {
    	String invokedBy = userIdBoundaryToStr(invokedByBoundary.getUserId());

    	return invokedBy;
    }
    
    public static TargetObject strTargetObjectToBoundary(String strTarget) {
    	TargetObject targetObject = new TargetObject();
        targetObject.setObjectId(strObjectIdToBoundary(strTarget));

    	return targetObject;
    }

    /**
     * This methode convert UserRole enum to String.
     */
    public static String userRoleToStr(UserRole userRole) {
        	return userRole.toString();
    }

    /**
     * This methode convert String to UserRole enum.
     */
    public static UserRole strToUserRole(String strUserRole) {
        try {
            return UserRole.valueOf(strUserRole);
        }catch (Exception e){
            throw new RuntimeException("Role not found");
        }
    }

}
