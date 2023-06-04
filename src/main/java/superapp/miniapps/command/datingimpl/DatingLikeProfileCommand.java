package superapp.miniapps.command.datingimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.boundaries.*;
import superapp.logic.mongo.NotFoundException;
import superapp.logic.utils.UtilHelper;
import superapp.logic.utils.convertors.CommandConvertor;
import superapp.logic.utils.convertors.ObjectConvertor;
import superapp.miniapps.command.MiniAppsCommand;
import superapp.miniapps.datingMiniApp.Match;
import superapp.miniapps.datingMiniApp.PrivateDatingProfile;

import java.util.*;

@Component
public class DatingLikeProfileCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;
    private final CommandConvertor commandConvertor;
    private final ObjectConvertor objectConvertor;



    @Autowired
    public DatingLikeProfileCommand(ObjectCrud objectCrudDB,
                                    CommandConvertor commandConvertor,
                                    ObjectConvertor objectConvertor) {

        this.objectCrudDB = objectCrudDB;
        this.commandConvertor =  commandConvertor;
        this.objectConvertor = objectConvertor;

    }


    /**
     * This method do like and if the other profile already like the profile --> match occurs
     *
     * command attributes required :
     * key 'myDatingProfileId', value: ObjectId
     * command as define in MiniAppCommand. command
     * targetObject = dating profile objectId (of other profile that my profile likes)
     * invokedBy - userId of client user
     *
     * if like is valid the method add the object id to likes list
     * if match occur the method create Match Object and add the id to both of dating profiles objects
     *
     * @param command MiniAppCommandBoundary
     * @return Map<String, Object>
     *     like_status : true, match_status : false, like_profile_id : dating profile objectId, match_id : ObjectId
     */
    @Override
    public Object execute(MiniAppCommandBoundary command) {


        if (command.getCommandAttributes() == null ||
                command.getCommandAttributes().get("myDatingProfileId") == null)
            throw new RuntimeException();

        ////// parse all data needed to execute //////

        // extract ids
        String iLikeDatingProfileId = this.commandConvertor.targetObjToEntity(command.getTargetObject());

        String myDatingProfileId = this.objectConvertor
                .objectIdToEntity(UtilHelper.jacksonHandle(command.getCommandAttributes().get("myDatingProfileId"),
                        ObjectId.class));

        // retrieve superApp objects of Dating Profiles

        SuperAppObjectEntity myObjectEntity = this.objectCrudDB
                .findById(myDatingProfileId)
                .orElseThrow(() ->
                        new NotFoundException("Target Object with id " + myDatingProfileId + " not exist in data base")
                );

        SuperAppObjectEntity iLikeObjectEntity = this.objectCrudDB
                .findById(iLikeDatingProfileId)
                .orElseThrow(() ->
                        new NotFoundException("Object with id " + iLikeDatingProfileId + " not exist in data base")
                );

        // check if other object is active:false
        if (iLikeObjectEntity.isActive()) {
            return this.resultCreator(
                    false,
                    false,
                    this.objectConvertor.objectIdToBoundary(iLikeDatingProfileId),
                    null);
        }

        // read my dating profile
        PrivateDatingProfile myDatingProfile = UtilHelper
                .jacksonHandle(myObjectEntity.getObjectDetails(), PrivateDatingProfile.class);

        // read I liked dating profile
        PrivateDatingProfile iLikeDatingProfile = UtilHelper
                .jacksonHandle(iLikeObjectEntity.getObjectDetails(), PrivateDatingProfile.class);


        // do like by add iLikeDatingProfileId to likes list of myDatingProfile
        if (!myDatingProfile.getLikes().contains(iLikeDatingProfileId))
            myDatingProfile.getLikes().add(iLikeDatingProfileId);

        // TODO: check if match already occur
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

            myObjectEntity.setObjectDetails(UtilHelper.jacksonHandle(myDatingProfile, Map.class));
            iLikeObjectEntity.setObjectDetails(UtilHelper.jacksonHandle(iLikeDatingProfile, Map.class));

            this.objectCrudDB.save(myObjectEntity);
            this.objectCrudDB.save(iLikeObjectEntity);

            return this.resultCreator(
                    true,
                    true,
                    this.objectConvertor.objectIdToBoundary(iLikeDatingProfileId),
                    matchObjectIdAsBoundary);

        }

        myObjectEntity.setObjectDetails(UtilHelper.jacksonHandle(myDatingProfile, Map.class));

        this.objectCrudDB.save(myObjectEntity);

        return resultCreator(
                true,
                false,
                this.objectConvertor.objectIdToBoundary(iLikeDatingProfileId),
                null);

    }

    private SuperAppObjectBoundary createAndStoreMatch(String myDatingProfileId,
                                                       String iLikeDatingProfileId,
                                                       SuperAppObjectEntity myObjectEntity) {


        // create match as entity using jackson
        Map<String, Object> match = UtilHelper
                .jacksonHandle(new Match()
                        .setProfileDatingId1(myDatingProfileId)
                        .setProfileDatingId2(iLikeDatingProfileId),
                Map.class);

        SuperAppObjectBoundary newMatch
                = new SuperAppObjectBoundary()
                .setObjectId(new ObjectId(
                        this.objectConvertor.objectIdToBoundary(myDatingProfileId).getSuperapp(),
                         UUID.randomUUID().toString()))
                .setCreationTimestamp(new Date())
                .setLocation(new Location(0,0))
                .setActive(true)
                .setAlias("match between 2 dating profile")
                .setType("MATCH")
                .setCreatedBy(this.objectConvertor.createByToBoundary(myObjectEntity.getCreatedBy()))
                .setObjectDetails(match);

        SuperAppObjectEntity createObjectRes = this.objectCrudDB.save(this.objectConvertor.toEntity(newMatch));

        return this.objectConvertor.toBoundary(createObjectRes);

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


}
