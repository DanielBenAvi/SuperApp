package superapp.miniapps.datingMiniApp.command.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.ConvertHelp;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.boundaries.ObjectId;
import superapp.miniapps.chat.objects.Chat;
import superapp.miniapps.datingMiniApp.Gender;
import superapp.miniapps.datingMiniApp.command.DatingCommand;
import superapp.miniapps.datingMiniApp.objects.Address;
import superapp.miniapps.datingMiniApp.objects.Match;
import superapp.miniapps.datingMiniApp.objects.PrivateDatingProfile;
import superapp.miniapps.datingMiniApp.objects.PublicDatingProfile;

import java.util.*;

@Component
public class DatingLikeProfileCommand implements DatingCommand {

//    private final UserCrud usersCrudDB;
//    private final ObjectsServiceWithRelationshipSupport objectService;

    private final ObjectCrud objectCrudDB;


    @Autowired
    public DatingLikeProfileCommand(ObjectCrud objectCrudDB) {
        this.objectCrudDB = objectCrudDB;

        // this.objectService = objectService;
        // this.usersCrudDB = usersCrudDB;
    }


    @Override
    public Object execute(MiniAppCommandBoundary command) {

        /**
         * * האם יש קשר בין collections שונים? למשל יוזר ואובייקט
        UserId invokedByUserID = command.getInvokedBy().getUserId();
        String invokedByUserEntityID = invokedByUserID.getSuperapp() + ConvertHelp.DELIMITER_ID + invokedByUserID.getEmail();
        **/

        //

        ObjectId targetBoundaryID = command.getTargetObject().getObjectId();
        String targetEntityID = targetBoundaryID.getSuperapp() + ConvertHelp.DELIMITER_ID + targetBoundaryID.getInternalObjectId();

        SuperAppObjectEntity targetObject = this.objectCrudDB.findById(targetEntityID).get();


//        PrivateDatingProfile targetDatingProfile = (PrivateDatingProfile) targetObject.getObjectDetails().get("key");

        PrivateDatingProfile targetDatingProfile
                = datingProfileDto((LinkedHashMap<String, Object>) targetObject.getObjectDetails().get("key"));


        System.err.println(targetDatingProfile);


        // get my profile dating
        String myDatingProfileId = command.getCommandAttributes().get("myDatingProfileId").toString();
        SuperAppObjectEntity myObject = this.objectCrudDB.findById(myDatingProfileId).get();

        PrivateDatingProfile myDatingProfile
                = datingProfileDto((LinkedHashMap<String, Object>) myObject.getObjectDetails().get("key"));


        // check match occurs
        if (targetDatingProfile.getLikes().contains(myDatingProfileId)) {

            // TODO - validate that match already exist
            // create new match
            Match newMatch = new Match()
                    .setChat(new Chat())
                    .setUser1(targetDatingProfile.getPublicProfile())
                    .setUser2(myDatingProfile.getPublicProfile());

            // update 2 profile with the match
            myDatingProfile.getMatches().add(newMatch);
            targetDatingProfile.getMatches().add(newMatch);

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

        PrivateDatingProfile profile=  new PrivateDatingProfile();
        profile.setPublicProfile((createPublicDatingProfileFromMap((LinkedHashMap<String, Object>)map.get("publicProfile"))));
        profile.setAddress((createAddressFromMap((LinkedHashMap<String, Object>) map.get("address"))));
        profile.setDistanceRange((int) map.get("distanceRange"));
        profile.setAgeRange((int) map.get("ageRange"));
        profile.setGenderPreferences((ArrayList<Gender>) map.get("genderPreferences"));
        profile.setMatches((ArrayList<Match>) map.get("matches"));
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
        publicDatingProfile.setInterests((ArrayList<String>) map.get("interests"));
        publicDatingProfile.setPictures((ArrayList<String>) map.get("pictures"));

        return publicDatingProfile;
    }

    private Address createAddressFromMap(LinkedHashMap<String, Object> map) {
        Address address = new Address()
                .setStreet((String) map.get("street"))
                .setCity((String) map.get("city"))
                .setState((String) map.get("state"))
                .setHomeNum((int) map.get("homeNum"))
                .setZipCode((String) map.get("zipCode"));

        return address;
    }
}
