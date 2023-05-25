package superapp.miniapps.command.datingimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.UserCrud;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.miniapps.command.MiniAppsCommand;

@Component
public class DatingGetMatchesCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;
    private final UserCrud usersCrudDB;

    @Autowired
    public DatingGetMatchesCommand(ObjectCrud objectCrudDB, UserCrud usersCrudDB) {
        this.objectCrudDB = objectCrudDB;
        this.usersCrudDB = usersCrudDB;
    }


    @Override
    public Object execute(MiniAppCommandBoundary commandBoundary) {

        // command attributes required : page, size
        // command as define in MiniAppCommand.command
        // targetObject = private dating profile object - ObjectId
        // invokedBy - userId of client user

        // return SuperAppObjectBoundary[] with objectDetails : match,
        // this is matches (of ObjectId) list.

        return null;
    }
}
