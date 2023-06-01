package superapp.miniapps.command.datingimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Map;

@Component
public class DatingUnmatchProfileCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;
    private final ObjectConvertor objectConvertor;
    private ObjectMapper jackson;

    @Autowired
    public DatingUnmatchProfileCommand(ObjectCrud objectCrudDB, ObjectConvertor objectConvertor) {
        this.objectCrudDB = objectCrudDB;
        this.objectConvertor = objectConvertor;
        this.jackson = new ObjectMapper();
    }


    @Override
    public Object execute(MiniAppCommandBoundary command) {

        // command attributes required : not necessary
        // command as define in MiniAppCommand.command
        // targetObject = match objectId
        // invokedBy - userId of client user

        // return Map<String, Object> : like_status : removed, match_status : canceled


        String matchId;
        SuperAppObjectEntity matchObject, datingObject_1, datingObject_2;
        MatchEntity matchEntity;
        PrivateDatingProfile datingProfile_1, datingProfile_2;

        Map<String, Object> commandRes = new HashMap<>();
        commandRes.put("like_status", "removed");
        commandRes.put("match_status", "canceled");


        try {

            matchId = this.objectConvertor
                    .objectIdToEntity(
                            UtilHelper
                                    .jacksonHandle(
                                            command.getTargetObject().getObjectId(), ObjectId.class, jackson));

            matchObject = objectCrudDB.findById(matchId).orElseThrow(() ->
                    new NotFoundException("Match Object with id " + matchId + " not exist in data base"));

            matchEntity = UtilHelper.jacksonHandle(matchObject.getObjectDetails(), MatchEntity.class, jackson);

            datingObject_1 = objectCrudDB.findById(matchEntity.getProfileDatingId1()).orElseThrow(() ->
                    new NotFoundException("Match Object with id " + matchId + " not exist in data base"));

            datingObject_2 = objectCrudDB.findById(matchEntity.getProfileDatingId2()).orElseThrow(() ->
                    new NotFoundException("Match Object with id " + matchId + " not exist in data base"));

            datingProfile_1 = UtilHelper.jacksonHandle(datingObject_1.getObjectDetails(), PrivateDatingProfile.class, jackson);
            datingProfile_2 = UtilHelper.jacksonHandle(datingObject_2.getObjectDetails(), PrivateDatingProfile.class, jackson);

        } catch (Exception e) {

            commandRes.put("like_status", "not-removed");
            commandRes.put("match_status", "not-canceled");

            return commandRes;
        }

        // TODO: validate that remove succeed
        // remove likes
        datingProfile_1.getMatches().remove(matchId);
        datingProfile_2.getMatches().remove(matchId);

        // remove matches
        datingProfile_1.getLikes().remove(datingObject_2.getObjectId());
        datingProfile_2.getLikes().remove(datingObject_1.getObjectId());

        matchObject.setActive(false);
        this.objectCrudDB.save(datingObject_1);
        this.objectCrudDB.save(datingObject_2);
        this.objectCrudDB.save(matchObject);

        return commandRes;
    }
}