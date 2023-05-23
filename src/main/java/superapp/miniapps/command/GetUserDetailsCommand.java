package superapp.miniapps.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.ConvertHelp;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.boundaries.SuperAppObjectBoundary;

import java.util.Map;

@Component
public class GetUserDetailsCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;

    @Autowired
    public GetUserDetailsCommand(ObjectCrud objectCrudDB) {
        this.objectCrudDB = objectCrudDB;
    }

    @Override
    public SuperAppObjectBoundary execute(MiniAppCommandBoundary commandBoundary) {

        // TODO: Add validation
        String email = commandBoundary.getInvokedBy().getUserId().getEmail();
        String superapp = commandBoundary.getInvokedBy().getUserId().getSuperapp();

        String createdBy = superapp + "_" + email;
        String type = "USER_DETAILS";

        SuperAppObjectEntity result = this.objectCrudDB.findByCreatedByAndType(createdBy, type);


        return convertEntityToBoundary(result);
    }

    private SuperAppObjectBoundary convertEntityToBoundary(SuperAppObjectEntity entity) {

        SuperAppObjectBoundary boundary = new SuperAppObjectBoundary();

        boundary.setObjectId(ConvertHelp.strObjectIdToBoundary(entity.getObjectId()));
        boundary.setType(entity.getType());
        boundary.setAlias(entity.getAlias());
        boundary.setActive(entity.getActive());
        boundary.setCreationTimestamp(entity.getCreationTimestamp());
        boundary.setLocation(ConvertHelp.locationEntityToBoundary(entity.getLocation()));
        boundary.setCreatedBy(ConvertHelp.strCreateByToBoundary(entity.getCreatedBy()));

        boundary.setObjectDetails(entity.getObjectDetails());

        return boundary;
    }
}
