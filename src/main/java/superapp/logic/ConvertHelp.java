package superapp.logic;

import superapp.logic.boundaries.CommandId;
import superapp.logic.boundaries.CreatedBy;
import superapp.logic.boundaries.InvokedBy;
import superapp.logic.boundaries.Location;
import superapp.logic.boundaries.ObjectId;
import superapp.logic.boundaries.TargetObject;
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
    
    public static CommandId convertStrToCmd(String cmdId) {
    	String[] str = cmdId.split(DELIMITER_ID);
    	return new CommandId(str[0],str[1],str[2]);
    }
    
    public static String convertCmdIDtoStr(CommandId cmdId) {
    	String str = cmdId.getSuperapp()+DELIMITER_ID+cmdId.getMiniapp()+DELIMITER_ID+cmdId.getInternalCommandId();
    	return str;
    }
    
    public static String convertTargetObjToStr(TargetObject trgObj) {
    	String str = objectIdBoundaryToStr(trgObj.getObjectId());
    	return str;
    }
    
    public static InvokedBy convertStrToInvokedBy(String strInvoke) {
    	InvokedBy ib = new InvokedBy();
    	ib.setUserId(strUserIdToBoundary(strInvoke));
    	return ib;
    }
    
    public static String convertInvokedByToStr(InvokedBy invokedBy) {
    	String str = userIdBoundaryToStr(invokedBy.getUserId());
    	return str;
    }
    
    public static TargetObject convertStrToTargetObject(String strTarget) {
    	TargetObject to = new TargetObject();
    	to.setObjectId(strObjectIdToBoundary(strTarget));
    	return to;
    }
    
}
