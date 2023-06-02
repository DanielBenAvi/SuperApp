package superapp.miniapps.command.datingimpl;


import com.fasterxml.jackson.databind.ObjectMapper;
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
import superapp.logic.utils.convertors.ObjectConvertor;
import superapp.miniapps.Gender;
import superapp.logic.utils.UtilHelper;
import superapp.miniapps.command.MiniAppsCommand;
import superapp.miniapps.datingMiniApp.PrivateDatingProfile;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Component
public class DatingGetPotentialDatesCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;
    private final ObjectConvertor objectConvertor;
    private final ObjectMapper jackson;
    @Autowired
    public DatingGetPotentialDatesCommand(ObjectCrud objectCrudDB,
                                          ObjectConvertor objectConvertor) {

        this.objectCrudDB = objectCrudDB;
        this.objectConvertor = objectConvertor;
        this.jackson = new ObjectMapper();
    }


    /**
     * this public dating Profiles retrieved according
     * 2. sexPreferences
     * 3. ageRange
     * 4. common interests
     * in addition : active is true, not in my likes or matches list
     * command attributes required : userDetailsId as ObjectId, page, size
     * command as define in MiniAppCommand. command
     * targetObject = private dating profile object - ObjectId
     * invokedBy - userId of client user
     * return SuperAppObjectBoundary List with objectDetails : public dating Profiles
     * this public dating Profiles retrieved according common interests, sexPreferences, ageRange
     * active is true, not in my likes or matches list
     * Map<String,Object> commandAttributes;
     * key="UserDetailsId", value=ObjectId (as boundary)
     *
     * @param command - MiniAppCommandBoundary
     * @return
     */
    @Override
    public Object execute(MiniAppCommandBoundary command) {

        String type = "PRIVATE_DATING_PROFILE";
        int page = 0, size = 15;



        // search potential date by this variables
        String[] matchesIds, likesIds, interests;
        String[] sexPreferences;
        int minAge, maxAge;

        // parse all data needed to execute
        try {

            Map<String, Object> commandAttr = UtilHelper.jacksonHandle(command.getCommandAttributes(), Map.class, jackson);

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
                                    .jacksonHandle(commandAttr.get("userDetailsId"), ObjectId.class, jackson));

            SuperAppObjectEntity userDetailsEntity = this.objectCrudDB
                    .findById(userDetailsObjectId)
                    .orElseThrow(() -> new NotFoundException("User details object id " + userDetailsObjectId + "not exist"));

            interests = UtilHelper
                    .jacksonHandle(userDetailsEntity.getObjectDetails(), UserDetails.class, jackson)
                    .getPreferences()
                    .toArray(new String[0]);

            // handle dating profile
            String datingProfileObjectId
                    = this.objectConvertor
                    .objectIdToEntity(
                            UtilHelper
                                    .jacksonHandle(command.getTargetObject().getObjectId(), ObjectId.class, jackson));

            PrivateDatingProfile datingProfile
                    = UtilHelper.jacksonHandle(
                            this.objectCrudDB
                                    .findById(datingProfileObjectId)
                                    .orElseThrow(() ->
                                            new NotFoundException("Dating Profile object id " + datingProfileObjectId + "not exist"))
                                    .getObjectDetails(), PrivateDatingProfile.class, jackson);

            sexPreferences = datingProfile.getGenderPreferences().stream().map(Gender::toString).toArray(String[]::new);
            maxAge = datingProfile.getMaxAge();
            minAge = datingProfile.getMinAge();
            likesIds = datingProfile.getLikes().toArray(new String[0]);
            matchesIds = datingProfile.getMatches().toArray(new String[0]);

        } catch (Exception e) {

            throw new RuntimeException(e);
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC,"creationTimestamp", "objectId");

        return this.objectCrudDB
                .findAllMyPotentialDates(type, likesIds, matchesIds, sexPreferences, interests, minAge, maxAge, pageRequest)
                .stream()
                .map(this.objectConvertor::toBoundary)
                .toList();
    }

}
