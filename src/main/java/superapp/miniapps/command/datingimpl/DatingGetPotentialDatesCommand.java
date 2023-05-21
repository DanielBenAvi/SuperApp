package superapp.miniapps.command.datingimpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.UserCrud;
import superapp.data.UserDetails;
import superapp.logic.ConvertHelp;
import superapp.logic.boundaries.*;
import superapp.logic.mongo.NotFoundException;
import superapp.miniapps.Gender;
import superapp.miniapps.command.MiniAppsCommand;
import superapp.miniapps.datingMiniApp.PrivateDatingProfile;

import java.util.List;

@Component
public class DatingGetPotentialDatesCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;
    private final UserCrud usersCrudDB;

    @Autowired
    public DatingGetPotentialDatesCommand(ObjectCrud objectCrudDB, UserCrud usersCrudDB) {
        this.objectCrudDB = objectCrudDB;
        this.usersCrudDB = usersCrudDB;
    }


    @Override
    public Object execute(MiniAppCommandBoundary commandBoundary) {

        // get according distance, intrests, sexPrefrnces, ageRange
        // active is true, not in my likes or matches list



        // Map<String,Object> commandAttributes; // key="DatingProfileId", value=ObjectId (as boundary)


        ObjectId dpObjectId = (ObjectId) commandBoundary.getCommandAttributes().get("DatingProfileId");
        ObjectId udObjectId = (ObjectId) commandBoundary.getCommandAttributes().get("UserDetailsId");

        String datingProfileId = ConvertHelp.concatenateIds(new String[]{dpObjectId.getSuperapp(), dpObjectId.getInternalObjectId()});
        String userDetailsId = ConvertHelp.concatenateIds(new String[]{udObjectId.getSuperapp(), udObjectId.getInternalObjectId()});


        UserDetails userDetails = ((UserDetails) this.objectCrudDB
                .findById(userDetailsId).orElseThrow(() -> new NotFoundException("user details not found"))
                .getObjectDetails()
                .get("UserDetails"));

        PrivateDatingProfile datingProfile = ((PrivateDatingProfile) this.objectCrudDB
                .findById(userDetailsId).orElseThrow(() -> new NotFoundException("user details not found"))
                .getObjectDetails()
                .get("PrivateDatingProfile"));


        List<String> interests = userDetails.getInterests();
        double distance = datingProfile.getDistanceRange();
        List<Gender> sexPreferences = datingProfile.getGenderPreferences();
        int maxAge = datingProfile.getMaxAge();
        int minAge = datingProfile.getMinAge();

        return null;
//        return this.objectCrudDB.findPotentialDate(interests, distance, sexPreferences, maxAge, minAge); // todo : pageable?
    }
}
