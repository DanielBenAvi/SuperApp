package superapp.miniapps.command.datingimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.boundaries.ObjectId;
import superapp.logic.mongo.NotFoundException;
import superapp.logic.utils.convertors.ObjectConvertor;
import superapp.logic.utils.UtilHelper;
import superapp.miniapps.command.MiniAppsCommand;
import superapp.miniapps.datingMiniApp.MatchEntity;
import superapp.miniapps.datingMiniApp.PrivateDatingProfile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DatingUnmatchProfileCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;
    private final ObjectConvertor objectConvertor;

    @Autowired
    public DatingUnmatchProfileCommand(ObjectCrud objectCrudDB, ObjectConvertor objectConvertor) {
        this.objectCrudDB = objectCrudDB;
        this.objectConvertor = objectConvertor;
    }


    /**
     * This method is for cancel match.
     * Match object active:false, and remove likes ids from both of dating profiles.
     *
     * command attributes required : not necessary
     * command as define in MiniAppCommand. command
     * targetObject = match objectId
     * invokedBy - userId of client user
     *
     * @param command MiniAppCommandBoundary
     * @return Map<String, Object> : like_status : removed, match_status : canceled
     */
    @Override
    public Object execute(MiniAppCommandBoundary command) {


        String matchId;
        SuperAppObjectEntity matchObject, datingObject_1, datingObject_2;
        MatchEntity matchEntity;
        PrivateDatingProfile datingProfile_1, datingProfile_2;

        Map<String, Object> commandRes = new HashMap<>();

        try {

            matchId = this.objectConvertor
                    .objectIdToEntity(
                            UtilHelper
                                    .jacksonHandle(
                                            command.getTargetObject().getObjectId(), ObjectId.class));

            matchObject = objectCrudDB.findById(matchId).orElseThrow(() ->
                    new NotFoundException("Match Object with id " + matchId + " not exist in data base"));

            matchEntity = UtilHelper.jacksonHandle(matchObject.getObjectDetails(), MatchEntity.class);

            datingObject_1 = objectCrudDB.findById(matchEntity.getProfileDatingId1()).orElseThrow(() ->
                    new NotFoundException("Match Object with id " + matchId + " not exist in data base"));

            datingObject_2 = objectCrudDB.findById(matchEntity.getProfileDatingId2()).orElseThrow(() ->
                    new NotFoundException("Match Object with id " + matchId + " not exist in data base"));

            datingProfile_1 = UtilHelper.jacksonHandle(datingObject_1.getObjectDetails(), PrivateDatingProfile.class);
            datingProfile_2 = UtilHelper.jacksonHandle(datingObject_2.getObjectDetails(), PrivateDatingProfile.class);

        } catch (Exception e) {

            commandRes.put("like_status", "not-removed");
            commandRes.put("match_status", "not-canceled");

            return commandRes;
        }

        // remove matches
        datingProfile_1.getMatches().remove(matchId);
        datingProfile_2.getMatches().remove(matchId);


        // remove likes
        List<String> likes_1 = datingProfile_1.getLikes();
        likes_1.remove(likes_1.contains(datingObject_1.getObjectId()) ? datingObject_1.getObjectId(): datingObject_2.getObjectId());
        datingProfile_1.setLikes(likes_1);

        List<String> likes_2 = datingProfile_2.getLikes();
        likes_2.remove(likes_2.contains(datingObject_2.getObjectId()) ? datingObject_2.getObjectId(): datingObject_1.getObjectId());
        datingProfile_2.setLikes(likes_2);

        datingObject_1.setObjectDetails(UtilHelper.jacksonHandle(datingProfile_1, Map.class));
        datingObject_2.setObjectDetails(UtilHelper.jacksonHandle(datingProfile_2, Map.class));

        matchObject.setActive(false);
        this.objectCrudDB.save(datingObject_1);
        this.objectCrudDB.save(datingObject_2);
        this.objectCrudDB.save(matchObject);


        commandRes.put("like_status", "removed");
        commandRes.put("match_status", "canceled");

        return commandRes;
    }
}