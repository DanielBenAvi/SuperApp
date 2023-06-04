package superapp.miniapps.command.datingimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.boundaries.ObjectId;
import superapp.logic.boundaries.SuperAppObjectBoundary;
import superapp.logic.mongo.NotFoundException;
import superapp.logic.utils.UtilHelper;
import superapp.logic.utils.convertors.ObjectConvertor;
import superapp.miniapps.command.MiniAppsCommand;
import superapp.miniapps.datingMiniApp.MatchEntity;
import superapp.miniapps.datingMiniApp.PrivateDatingProfile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DatingGetMatchesCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;
    private final ObjectConvertor objectConvertor;


    @Autowired
    public DatingGetMatchesCommand(ObjectCrud objectCrudDB, ObjectConvertor objectConvertor) {
        this.objectCrudDB = objectCrudDB;
        this.objectConvertor = objectConvertor;
    }


    /**
     * This method retrieve all dating profile of my matches as
     * Map<"MatchID", SuperAppObjectBoundary with objectDetails PrivateDatingProfile>.
     *
     * command attributes required : page, size
     * command as define in MiniAppCommand. command
     * targetObject = private dating profile object - ObjectId
     * invokedBy - userId of client user
     *
     * @param command MiniAppCommandBoundary
     * @return Map<String, SuperAppObjectBoundary>
     *     key : internalObjectId of MATCH object, value : SuperAppObjectBoundary with objectDetails PrivateDatingProfile
     */
    @Override
    public Object execute(MiniAppCommandBoundary command) {

        String type = "MATCH";

        int page = 0, size = 15;
        if (command.getCommandAttributes() != null) {

            if (command.getCommandAttributes().get("size") != null)
                size = UtilHelper.getSizeAsInt(command.getCommandAttributes().get("size").toString() , size);

            if (command.getCommandAttributes().get("page") != null)
                page = UtilHelper.getPageAsInt(command.getCommandAttributes().get("page").toString(), page);
        }

        String targetObjectId = this.objectConvertor
                .objectIdToEntity(command
                        .getTargetObject()
                        .getObjectId());

        SuperAppObjectEntity targetObject = this.objectCrudDB
                .findById(targetObjectId)
                .orElseThrow(() -> new NotFoundException("Target Object with id " + targetObjectId + " not exist in data base"));


        String[] ids = UtilHelper
                .jacksonHandle(
                        targetObject.getObjectDetails(),
                        PrivateDatingProfile.class)
                .getMatches()
                .toArray(new String[0]);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC,"creationTimestamp", "objectId");



        List<SuperAppObjectBoundary> matches = this.objectCrudDB
                                                                .findAllByObjectIdInAndTypeAndActiveIsTrue(ids, type, pageRequest)
                                                                .stream()
                                                                .map(this.objectConvertor::toBoundary)
                                                                .toList();

        Map<String, SuperAppObjectBoundary> res = new HashMap<>();

        for (SuperAppObjectBoundary match: matches) {
            ObjectId matchId = match.getObjectId();

            MatchEntity matchEntity = UtilHelper.jacksonHandle(match.getObjectDetails(), MatchEntity.class);

            String id = matchEntity.getProfileDatingId1().equals(targetObjectId) ?
                                                matchEntity.getProfileDatingId2() : matchEntity.getProfileDatingId1();
            if (this.objectCrudDB.existsById(id)) {

                SuperAppObjectBoundary matchDatingProfile
                        = this.objectCrudDB
                        .findById(id)
                        .map(this.objectConvertor::toBoundary).get();

                if (matchDatingProfile.getActive())
                    res.put( matchId.getInternalObjectId(), matchDatingProfile);
            }
        }

        return res;

    }
}
