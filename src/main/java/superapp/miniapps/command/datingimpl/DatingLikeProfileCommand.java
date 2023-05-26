package superapp.miniapps.command.datingimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.boundaries.ObjectId;
import superapp.logic.utils.convertors.CommandConvertor;
import superapp.miniapps.Gender;
import superapp.miniapps.command.MiniAppsCommand;
import superapp.miniapps.datingMiniApp.PrivateDatingProfile;
import superapp.miniapps.datingMiniApp.PublicDatingProfile;

import java.util.*;

@Component
public class DatingLikeProfileCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;
    private final CommandConvertor commandConvertor;

    @Autowired
    public DatingLikeProfileCommand(ObjectCrud objectCrudDB, CommandConvertor commandConvertor) {
        this.objectCrudDB = objectCrudDB;
        this.commandConvertor =  commandConvertor;
    }


    @Override
    public Object execute(MiniAppCommandBoundary command) {

        // command attributes required : myDatingProfileId
        // command as define in MiniAppCommand.command
        // targetObject = dating profile objectId (of other profile that my profile likes)
        // invokedBy - userId of client user

        // Note : instead  command attributes required : myDatingProfileId, we can use invokedBy.


        // return Map<String, boolean> : like_status : true, match_status : false

        /**
         * * האם יש קשר בין collections שונים? למשל יוזר ואובייקט
        UserId invokedByUserID = command.getInvokedBy().getUserId();
        String invokedByUserEntityID = invokedByUserID.getSuperapp() + ConvertHelp.DELIMITER_ID + invokedByUserID.getEmail();
        **/

        //

        ObjectId targetBoundaryID = command.getTargetObject().getObjectId();

        String targetEntityID = this.commandConvertor.targetObjToEntity(command.getTargetObject());

//        /////////////////////
//        ObjectMapper objectMapper = new ObjectMapper();
//        String t = objectMapper.writeValueAsString(PrivateDatingProfile);
//        PrivateDatingProfile car = objectMapper.readValue(json, PrivateDatingProfile.class);
//        ///////////////////


        // get my profile dating
        String myDatingProfileId = command.getCommandAttributes().get("myDatingProfileId").toString();
        SuperAppObjectEntity myObject = this.objectCrudDB.findById(myDatingProfileId).get();

        PrivateDatingProfile myDatingProfile
                = datingProfileDto((LinkedHashMap<String, Object>) myObject.getObjectDetails().get("key"));



        SuperAppObjectEntity targetObject = this.objectCrudDB.findById(targetEntityID).get();
        Object objectDetailsValue = targetObject.getObjectDetails().get("key");
        PrivateDatingProfile targetDatingProfile = new PrivateDatingProfile();

        if (objectDetailsValue instanceof LinkedHashMap) {

            LinkedHashMap<String, Object> objectDetailsMap = (LinkedHashMap<String, Object>) objectDetailsValue;
            targetDatingProfile = datingProfileDto(objectDetailsMap);

        }
        else if (objectDetailsValue instanceof PrivateDatingProfile){

            targetDatingProfile = (PrivateDatingProfile) objectDetailsValue;
        }
        else {
            // TODO
            System.err.println(objectDetailsValue);
        }


        // check match occurs
        if (targetDatingProfile.getLikes().contains(myDatingProfileId)) {

            // TODO - validate that match already exist
            // create new match
//            MatchEntity newMatchEntity = new MatchEntity()
//                    .setChat(new Chat())
//                    .setUser1(targetDatingProfile.getPublicProfile())
//                    .setUser2(myDatingProfile.getPublicProfile());

            // update 2 profile with the match
//            myDatingProfile.getMatches().add(newMatch);
//            targetDatingProfile.getMatches().add(newMatch);

            // update the targetObject and save to database
            targetObject.getObjectDetails().put("key", targetDatingProfile);
            this.objectCrudDB.save(targetObject);
        }

        // update the myDatingProfile and save to database
        myDatingProfile.getLikes().add(targetEntityID);
        myObject.getObjectDetails().put("key", myDatingProfile);
        this.objectCrudDB.save(myObject);


        // create the result
        Map<String, Object> likesAndMatches = new HashMap<>();
        PrivateDatingProfile myDatingProfileAfterUpdate = (PrivateDatingProfile) this.objectCrudDB
                .findById(myDatingProfileId).get().getObjectDetails().get("key");

        likesAndMatches.put("likes", myDatingProfileAfterUpdate.getLikes());
        likesAndMatches.put("matches", myDatingProfileAfterUpdate.getMatches());


        // TODO Match between A & B will be saved SuperAppObjectEntity (Created BY - default user or last like User id )

        return likesAndMatches;
    }

    private PrivateDatingProfile datingProfileDto(LinkedHashMap<String, Object> map) {

        PrivateDatingProfile profile = new PrivateDatingProfile();

        profile.setPublicProfile(createPublicDatingProfileFromMap((LinkedHashMap<String, Object>) map.get("publicProfile")));
        profile.setDistanceRange((int) map.get("distanceRange"));
        profile.setGenderPreferences((ArrayList<Gender>) map.get("genderPreferences"));
        profile.setMatches((ArrayList<String>) map.get("matches"));
        profile.setLikes((ArrayList<String>) map.get("likes"));
        return profile;
    }

    private PublicDatingProfile createPublicDatingProfileFromMap(LinkedHashMap<String, Object> map) {

        PublicDatingProfile publicDatingProfile = new PublicDatingProfile();

        publicDatingProfile.setNickName((String) map.get("nickName"));
        publicDatingProfile.setGender((Gender.valueOf((String) map.get("gender"))));
        publicDatingProfile.setAge((int) map.get("age"));
        publicDatingProfile.setBio((String) map.get("bio"));
        publicDatingProfile.setSexOrientation((ArrayList<Gender>) map.get("sexOrientation"));
        publicDatingProfile.setPictures((ArrayList<String>) map.get("pictures"));

        return publicDatingProfile;
    }

}
