package superapp.miniapps.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.boundaries.CreatedBy;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.boundaries.SuperAppObjectBoundary;
import superapp.logic.boundaries.UserId;
import superapp.logic.utils.convertors.ObjectConvertor;

@Component
public class GetUserDetailsCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;

    private final ObjectConvertor objectConvertor;

    @Autowired
    public GetUserDetailsCommand(ObjectCrud objectCrudDB, ObjectConvertor objectConvertor) {
        this.objectCrudDB = objectCrudDB;
        this.objectConvertor = objectConvertor;
    }
    @Override
    public SuperAppObjectBoundary execute(MiniAppCommandBoundary commandBoundary) {

        // TODO: Add validation
        String email = commandBoundary.getInvokedBy().getUserId().getEmail();
        String superapp = commandBoundary.getInvokedBy().getUserId().getSuperapp();

        String createdBy = this.objectConvertor
                .createByToEntity(new CreatedBy()
                        .setUserId(new UserId(superapp, email))
                );

        String type = "USER_DETAILS";

        SuperAppObjectEntity result = this.objectCrudDB.findByCreatedByAndType(createdBy, type);


        return this.objectConvertor.toBoundary(result);
    }

}
