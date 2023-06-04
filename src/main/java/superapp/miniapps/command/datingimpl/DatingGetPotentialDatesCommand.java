package superapp.miniapps.command.datingimpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.data.UserDetails;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.boundaries.ObjectId;
import superapp.logic.mongo.NotFoundException;
import superapp.logic.utils.UtilHelper;
import superapp.logic.utils.convertors.ObjectConvertor;
import superapp.miniapps.Gender;
import superapp.miniapps.command.MiniAppsCommand;
import superapp.miniapps.datingMiniApp.PrivateDatingProfile;

import java.util.Map;

@Component
public class DatingGetPotentialDatesCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;
    private final ObjectConvertor objectConvertor;
    @Autowired
    public DatingGetPotentialDatesCommand(ObjectCrud objectCrudDB,
                                          ObjectConvertor objectConvertor) {

        this.objectCrudDB = objectCrudDB;
        this.objectConvertor = objectConvertor;
    }


    /**
     * This method retrieve dating profile that is a potential date according dating profile preferences.
     *
     * preferences are:
     * 1. sexPreferences
     * 2. ageRange
     * 3. common interests
     * in addition : dating profile shall be active-true, not in my likes or matches list
     *
     * command attributes required :
     * key: 'userDetailsId' value: ObjectId of UserDetails,
     * key: 'page' and page value,
     * key: 'size' and size value
     *
     * command as define in MiniAppCommand. command
     * targetObject = private dating profile object - ObjectId
     * invokedBy - userId of client user
     *
     * @param command - MiniAppCommandBoundary
     * @return List<SuperAppObjectBoundary> with objectDetails : private dating Profiles
     */
    @Override
    public Object execute(MiniAppCommandBoundary command) {

        String type = "PRIVATE_DATING_PROFILE";
        int page = 0, size = 15;


        // parse all data needed to execute
        Map<String, Object> commandAttr = UtilHelper.jacksonHandle(command.getCommandAttributes(), Map.class);

        if (command.getCommandAttributes() != null) {

            if (commandAttr.get("size") != null)
                size = UtilHelper.getSizeAsInt(commandAttr.get("size").toString() , size);

            if (commandAttr.get("page") != null)
                page = UtilHelper.getPageAsInt(commandAttr.get("page").toString(), page);
        }

        if (commandAttr.get("userDetailsId") == null)
            throw new RuntimeException();

        // handle user details
        String userDetailsObjectId = this.objectConvertor
                .objectIdToEntity(
                        UtilHelper
                                .jacksonHandle(commandAttr.get("userDetailsId"), ObjectId.class));

        SuperAppObjectEntity userDetailsEntity = this.objectCrudDB
                .findById(userDetailsObjectId)
                .orElseThrow(() -> new NotFoundException("User details object id " + userDetailsObjectId + "not exist"));

        String[] interests = UtilHelper
                .jacksonHandle(userDetailsEntity.getObjectDetails(), UserDetails.class)
                .getPreferences()
                .toArray(new String[0]);

        // handle dating profile
        String datingProfileObjectId
                = this.objectConvertor
                .objectIdToEntity(
                        UtilHelper
                                .jacksonHandle(command.getTargetObject().getObjectId(), ObjectId.class));

        PrivateDatingProfile datingProfile
                = UtilHelper.jacksonHandle(
                this.objectCrudDB
                        .findById(datingProfileObjectId)
                        .orElseThrow(() ->
                                new NotFoundException("Dating Profile object id " + datingProfileObjectId + "not exist"))
                        .getObjectDetails(), PrivateDatingProfile.class);

        String[] sexPreferences = datingProfile.getGenderPreferences().stream().map(Gender::toString).toArray(String[]::new);
        int maxAge = datingProfile.getMaxAge();
        int minAge = datingProfile.getMinAge();
        String[] likesIds = datingProfile.getLikes().toArray(new String[0]);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC,"creationTimestamp", "objectId");

        return this.objectCrudDB
                .findAllMyPotentialDates(type, likesIds, sexPreferences, interests, minAge, maxAge, pageRequest)
                .stream()
                .filter(ob -> !ob.getObjectId().equals(datingProfileObjectId)) // filter the profile request
                .map(this.objectConvertor::toBoundary)
                .toList();
    }

}
