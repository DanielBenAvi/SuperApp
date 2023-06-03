package superapp.miniapps.command.datingimpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.mongo.NotFoundException;
import superapp.logic.utils.UtilHelper;
import superapp.logic.utils.convertors.ObjectConvertor;
import superapp.miniapps.command.MiniAppsCommand;
import superapp.miniapps.datingMiniApp.PrivateDatingProfile;

@Component
public class DatingGetLikesCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;
    private final ObjectConvertor objectConvertor;

    @Autowired
    public DatingGetLikesCommand(ObjectCrud objectCrudDB, ObjectConvertor objectConvertor) {

        this.objectCrudDB = objectCrudDB;
        this.objectConvertor = objectConvertor;
    }


    /**
     * This methode retrieve all dating profile that user already like
     *
     * command attributes required : page, size
     * command as define in MiniAppCommand.command
     * targetObject = private dating profile object - ObjectId
     * invokedBy - userId of client user
     *
     * @param command MiniAppCommandBoundary
     * @return List<SuperAppObjectBoundary></SuperAppObjectBoundary> with objectDetails : private dating Profile
     */
    @Override
    public Object execute(MiniAppCommandBoundary command) {

        String objectType = "PRIVATE_DATING_PROFILE";
        int page = 0, size = 15;

        if (command.getCommandAttributes() != null) {

            if (command.getCommandAttributes().get("size") != null)
                size = UtilHelper.getSizeAsInt(command.getCommandAttributes().get("size").toString() , size);

            if (command.getCommandAttributes().get("page") != null)
                page = UtilHelper.getPageAsInt(command.getCommandAttributes().get("page").toString(), page);
        }

        String[] ids = UtilHelper
                .jacksonHandle(
                        this.objectCrudDB
                                .findById(
                                        this.objectConvertor
                                                .objectIdToEntity(command
                                                        .getTargetObject()
                                                        .getObjectId()))
                                .orElseThrow(() -> new NotFoundException())
                                .getObjectDetails(),
                        PrivateDatingProfile.class)
                .getLikes()
                .toArray(new String[0]);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC,"creationTimestamp", "objectId");
        return this.objectCrudDB
                .findAllByObjectIdInAndTypeAndActiveIsTrue(ids, objectType, pageRequest)
                .stream()
                .map(this.objectConvertor::toBoundary)
                .toList();
    }
}
