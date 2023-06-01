package superapp.miniapps.command.datingimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.ObjectsService;
import superapp.logic.boundaries.*;
import superapp.logic.mongo.NotFoundException;
import superapp.logic.utils.convertors.CommandConvertor;
import superapp.logic.utils.convertors.ConvertIdsHelper;
import superapp.logic.utils.convertors.ObjectConvertor;
import superapp.miniapps.command.MiniAppsCommand;
import superapp.miniapps.datingMiniApp.MatchBoundary;
import superapp.miniapps.datingMiniApp.MatchEntity;
import superapp.miniapps.datingMiniApp.PrivateDatingProfile;

import java.util.*;

@Component
public class DatingLikeProfileCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;
    private final CommandConvertor commandConvertor;
    private final ObjectConvertor objectConvertor;
    private final ObjectMapper jackson;
    private final ObjectsService objectsService;


    @Autowired
    public DatingLikeProfileCommand(ObjectCrud objectCrudDB,
                                    CommandConvertor commandConvertor,
                                    ObjectConvertor objectConvertor,
                                    ObjectsService objectsService) {

        this.objectCrudDB = objectCrudDB;
        this.commandConvertor =  commandConvertor;
        this.objectConvertor = objectConvertor;
        this.objectsService = objectsService;
        this.jackson = new ObjectMapper();
    }


    @Override
    public Object execute(MiniAppCommandBoundary command) {

        // command attributes required : <'myDatingProfileId', ObjectId> #  ObjectId Boundary
        // command as define in MiniAppCommand.command
        // targetObject = dating profile objectId (of other profile that my profile likes) #  ObjectId Boundary
        // invokedBy - userId of client user

        // return Map<String, boolean> : like_status : true, match_status : false, like_profile_id : dating profile objectId, match_id : ObjectId

        String myDatingProfileId, iLikeDatingProfileId;
        SuperAppObjectEntity myObjectEntity, iLikeObjectEntity;
        PrivateDatingProfile myDatingProfile, iLikeDatingProfile;

        Map<String, Object> likeResult;

        ////// parse all data needed to execute //////

        // extract ids
        iLikeDatingProfileId = this.commandConvertor.targetObjToEntity(command.getTargetObject());

        myDatingProfileId = this.objectConvertor
                .objectIdToEntity(this.jacksonHandle(command.getCommandAttributes().get("myDatingProfileId"),
                        ObjectId.class));

        // retrieve superApp objects of Dating Profiles

        myObjectEntity = this.objectCrudDB
                .findById(myDatingProfileId)
                .orElseThrow(() ->
                        new NotFoundException("Target Object with id " + myDatingProfileId + " not exist in data base")
                );

        iLikeObjectEntity = this.objectCrudDB
                .findById(iLikeDatingProfileId)
                .orElseThrow(() ->
                        new NotFoundException("Object with id " + iLikeDatingProfileId + " not exist in data base")
                );

        // check if other object is active:false
        if (!iLikeObjectEntity.isActive()) {
            return this.resultCreator(
                    false,
                    false,
                    this.objectConvertor.objectIdToBoundary(iLikeDatingProfileId),
                    null);
        }

        // read my dating profile
        myDatingProfile = this.jacksonHandle(myObjectEntity.getObjectDetails(), PrivateDatingProfile.class);

        // read I liked dating profile
        iLikeDatingProfile = this.jacksonHandle(iLikeObjectEntity.getObjectDetails(), PrivateDatingProfile.class);

        // do like by add iLikeDatingProfileId to likes list of myDatingProfile
        myDatingProfile.getLikes().add(iLikeDatingProfileId);

        // check if match occurs
        if (iLikeDatingProfile.getLikes().contains(myDatingProfileId)) {

            // match creator and store the match
            SuperAppObjectBoundary createdMatch
                    = createAndStoreMatch(myDatingProfileId, iLikeDatingProfileId, myObjectEntity);

            ObjectId matchObjectIdAsBoundary = createdMatch.getObjectId();
            String matchObjectId = this.objectConvertor.objectIdToEntity(matchObjectIdAsBoundary);

            // add match id to list of both dating profile
            myDatingProfile.getMatches().add(matchObjectId);
            iLikeDatingProfile.getMatches().add(matchObjectId);

            myObjectEntity.setObjectDetails(this.jacksonHandle(myDatingProfile, Map.class));
            iLikeObjectEntity.setObjectDetails(this.jacksonHandle(iLikeDatingProfile, Map.class));

            this.objectCrudDB.save(myObjectEntity);
            this.objectCrudDB.save(iLikeObjectEntity);
            return this.resultCreator(
                    true,
                    true,
                    this.objectConvertor.objectIdToBoundary(iLikeDatingProfileId),
                    matchObjectIdAsBoundary);

        } else {


            myObjectEntity.setObjectDetails(this.jacksonHandle(myDatingProfile, Map.class));
            this.objectCrudDB.save(myObjectEntity);

            return resultCreator(
                    true,
                    false,
                    this.objectConvertor.objectIdToBoundary(iLikeDatingProfileId),
                    null);
        }

    }

    private SuperAppObjectBoundary createAndStoreMatch(String myDatingProfileId,
                                                       String iLikeDatingProfileId,
                                                       SuperAppObjectEntity myObjectEntity) {


        // create match as entity using jackson
        Map<String, Object> match
                = this.jacksonHandle(new MatchEntity()
                        .setProfileDatingId1(myDatingProfileId)
                        .setProfileDatingId2(iLikeDatingProfileId),
                        Map.class);

        SuperAppObjectBoundary newMatch
                = new SuperAppObjectBoundary()
                .setActive(true)
                .setAlias("match between 2 dating profile")
                .setType("MATCH")
                .setCreatedBy(this.objectConvertor.createByToBoundary(myObjectEntity.getCreatedBy()))
                .setObjectDetails(match);

        SuperAppObjectBoundary createObjectRes
                = this.objectsService.createObject(newMatch);


        return createObjectRes
                .setObjectDetails(
                        this.jacksonHandle(
                                this.matchToBoundary(
                                        this.jacksonHandle(
                                                createObjectRes.getObjectDetails(),
                                                MatchEntity.class)),
                                Map.class)
                );

    }

    private <T> T jacksonHandle(Object toRead, Class<T> readAs) {

        try {
            String json = this.jackson.writeValueAsString(toRead);
            return this.jackson.readValue(json, readAs);

        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> resultCreator(boolean likeStatus, boolean matchStatus,
                                              ObjectId iLikeDatingProfileId, ObjectId matchObjectId) {

        Map<String, Object> likeResult = new HashMap<>();

        likeResult.put("like_status", likeStatus);
        likeResult.put("match_status", matchStatus);
        likeResult.put("like_profile_id", iLikeDatingProfileId);
        likeResult.put("match_id", matchObjectId);

        return likeResult;
    }

    public MatchBoundary matchToBoundary(MatchEntity match) {

        String [] objectId1 = ConvertIdsHelper.splitConcretedIds(match.getProfileDatingId1());
        String [] objectId2 = ConvertIdsHelper.splitConcretedIds(match.getProfileDatingId2());

        return new MatchBoundary()
                .setProfileDatingId1(new ObjectId(objectId1[0], objectId1[1]))
                .setProfileDatingId2(new ObjectId(objectId2[0], objectId2[1]));
    }

}
