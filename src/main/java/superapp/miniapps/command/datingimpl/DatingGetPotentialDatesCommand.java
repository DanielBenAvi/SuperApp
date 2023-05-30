package superapp.miniapps.command.datingimpl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.data.UserDetails;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.boundaries.ObjectId;
import superapp.logic.mongo.NotFoundException;
import superapp.logic.utils.convertors.ObjectConvertor;
import superapp.miniapps.Gender;
import superapp.miniapps.command.MiniAppsCommand;
import superapp.miniapps.datingMiniApp.PrivateDatingProfile;

import java.util.Arrays;

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


    @Override
    public Object execute(MiniAppCommandBoundary command) {



        // command attributes required : UserDetailsId, page, size
        // command as define in MiniAppCommand.command
        // targetObject = private dating profile object - ObjectId
        // invokedBy - userId of client user

        // return SuperAppObjectBoundary List with objectDetails : public dating Profiles

        // this public dating Profiles retrieved according distance, interests, sexPreferences, ageRange
        // active is true, not in my likes or matches list


        // Map<String,Object> commandAttributes;
        // key="UserDetailsId", value=ObjectId (as boundary)
        String type = "PRIVATE_DATING_PROFILE";
        int page = command
                .getCommandAttributes()
                .get("page") == null ? 0 : Integer
                .parseInt(command.getCommandAttributes()
                        .get("page")
                        .toString());
        int size = command
                .getCommandAttributes()
                .get("size") == null ? 20 : Integer
                .parseInt(command.getCommandAttributes()
                        .get("size")
                        .toString());

        ObjectId userDetailsObjectId, datingProfileObjectId;
        UserDetails userDetails;

        PrivateDatingProfile datingProfile;

        String json;
        SuperAppObjectEntity userDetailsEntity, datingProfileEntity;
        // parse all data needed to execute
        try {



            // handle user details
            json = this.jackson.writeValueAsString(command.getCommandAttributes());
            userDetailsObjectId = this.jackson.readValue(json, ObjectId.class);


            userDetailsEntity = this.objectCrudDB
                    .findById(this.objectConvertor.objectIdToEntity(userDetailsObjectId))
                    .orElseThrow(() -> new NotFoundException("User details object id " + userDetailsObjectId + "not exist"));

            if (!userDetailsEntity.isActive())
                throw new NotFoundException("User details object id " + userDetailsObjectId + "not exist, active:false");

            json = this.jackson.writeValueAsString(userDetailsEntity.getObjectDetails());
            userDetails = this.jackson.readValue(json, UserDetails.class);

            // handle dating profile
            json = this.jackson.writeValueAsString(command.getTargetObject().getObjectId());
            datingProfileObjectId = this.jackson.readValue(json, ObjectId.class);

            datingProfileEntity = this.objectCrudDB
                    .findById(this.objectConvertor.objectIdToEntity(datingProfileObjectId))
                    .orElseThrow(() -> new NotFoundException("Dating Profile object id " + datingProfileObjectId + "not exist"));

            if (!datingProfileEntity.isActive())
                throw new NotFoundException("Dating Profile object id " + datingProfileObjectId + "not exist, active:false");

            json = this.jackson.writeValueAsString(datingProfileEntity.getObjectDetails());
            datingProfile = this.jackson.readValue(json, PrivateDatingProfile.class);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // this public dating Profiles retrieved according
        // 1. distance
        // 2. sexPreferences
        // 3. ageRange
        // 4. common interests
        //
        // in addition : active is true, not in my likes or matches list




        String[] interests = userDetails
                .getPreferences()
                .toArray(new String[0]);

        Gender[] sexPreferences = datingProfile
                .getGenderPreferences().toArray(new Gender[0]);

        double distance = datingProfile.getDistanceRange();
        int maxAge = datingProfile.getMaxAge();
        int minAge = datingProfile.getMinAge();

        String[] likesIds = datingProfile
                .getLikes()
                .toArray(new String[0]);

        String[] matchesIds = datingProfile
                .getMatches()
                .toArray(new String[0]);
        Point point = datingProfileEntity.getLocation();
        Distance maxDistance = new Distance(distance, Metrics.KILOMETERS);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC,"creationTimestamp", "objectId");

        return this.objectCrudDB
                .findAllMyPotentialDates(type, likesIds, matchesIds, sexPreferences, interests, minAge, maxAge, point, maxDistance, pageRequest)
                .stream()
                .map(this.objectConvertor::toBoundary)
                .toList();
    }
}
