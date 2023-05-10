package superapp.miniapps.datingMiniApp.command.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.UserCrud;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.miniapps.datingMiniApp.command.DatingCommand;

@Component
public class DatingLikeProfileCommand implements DatingCommand {

    private final ObjectCrud objectCrudDB;
    private final UserCrud usersCrudDB;

    @Autowired
    public DatingLikeProfileCommand(ObjectCrud objectCrudDB, UserCrud usersCrudDB) {
        this.objectCrudDB = objectCrudDB;
        this.usersCrudDB = usersCrudDB;
    }


    @Override
    public Object execute(MiniAppCommandBoundary commandBoundary) {

        //Optional<SuperAppObjectEntity> datingProfile = objectCrud.findById(targetObject.getObjectId().getInternalObjectId());

        // check cardinal
        // add like to my profile dating
        // add like to other profile dating
        return null;
    }
}
