package superapp.miniapps.command.datingimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.UserCrud;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.miniapps.command.MiniAppsCommand;

@Component
public class DatingUnmatchProfileCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;
    private final UserCrud usersCrudDB;

    @Autowired
    public DatingUnmatchProfileCommand(ObjectCrud objectCrudDB, UserCrud usersCrudDB) {
        this.objectCrudDB = objectCrudDB;
        this.usersCrudDB = usersCrudDB;
    }


    @Override
    public Object execute(MiniAppCommandBoundary commandBoundary) {

        // command attributes required : myDatingProfileId
        // command as define in MiniAppCommand.command
        // targetObject = match objectId
        // invokedBy - userId of client user

        // Note : instead  command attributes required : myDatingProfileId, we can use invokedBy.


        // return Map<String, boolean> : like_status : true, match_status : false

        return null;
    }
}