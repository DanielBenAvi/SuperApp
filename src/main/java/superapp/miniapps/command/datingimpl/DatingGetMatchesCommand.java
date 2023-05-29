package superapp.miniapps.command.datingimpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.mongo.NotFoundException;
import superapp.logic.utils.convertors.ObjectConvertor;
import superapp.miniapps.command.MiniAppsCommand;
import superapp.miniapps.datingMiniApp.PrivateDatingProfile;

@Component
public class DatingGetMatchesCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;
    private final ObjectConvertor objectConvertor;
    private final ObjectMapper jackson;


    @Autowired
    public DatingGetMatchesCommand(ObjectCrud objectCrudDB, ObjectConvertor objectConvertor) {
        this.objectCrudDB = objectCrudDB;
        this.objectConvertor = objectConvertor;
        this.jackson = new ObjectMapper();
    }


    @Override
    public Object execute(MiniAppCommandBoundary commandBoundary) {

        // command attributes required : page, size
        // command as define in MiniAppCommand.command
        // targetObject = private dating profile object - ObjectId
        // invokedBy - userId of client user

        // return SuperAppObjectBoundary[] with objectDetails : match,
        // this is matches (of ObjectId) list.

        // TODO: add validation for pagination values
        String targetObjectId = this.objectConvertor
                .objectIdToEntity(commandBoundary
                        .getTargetObject()
                        .getObjectId());

        SuperAppObjectEntity targetObject = this.objectCrudDB
                .findById(targetObjectId)
                .orElseThrow(() -> new NotFoundException("Target Object with id " + targetObjectId + " not exist in data base"));

        if (!targetObject.isActive())
            throw new NotFoundException("Target Object with id " + targetObjectId + " not exist , active : false");

        String type = "MATCH";

        int page = commandBoundary
                .getCommandAttributes()
                .get("page") == null ? 0 : Integer
                .parseInt(commandBoundary.getCommandAttributes()
                        .get("page")
                        .toString());
        int size = commandBoundary
                .getCommandAttributes()
                .get("size") == null ? 20 : Integer
                .parseInt(commandBoundary.getCommandAttributes()
                        .get("size")
                        .toString());
        String[] ids;

        try {
            String json = this.jackson.writeValueAsString(targetObject.getObjectDetails());
            PrivateDatingProfile datingProfile = this.jackson.readValue(json, PrivateDatingProfile.class);
            ids = datingProfile
                    .getMatches()
                    .toArray(new String[0]);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC,"creationTimestamp", "objectId");
        return this.objectCrudDB
                .findAllByObjectIdInAndTypeAndActiveIsTrue(ids, type, pageRequest)
                .stream()
                .map(this.objectConvertor::toBoundary)
                .toList();
    }
}
