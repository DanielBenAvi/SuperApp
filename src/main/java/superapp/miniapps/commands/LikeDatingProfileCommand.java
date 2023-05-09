package superapp.miniapps.commands;

import lombok.RequiredArgsConstructor;
import superapp.data.ObjectCrud;
import superapp.logic.ObjectsService;

@RequiredArgsConstructor
public class LikeDatingProfileCommand implements DatingCommand {

    private final ObjectCrud objectCrud;
    private final ObjectsService objectsService;


    @Override
    public Object execute() {



        //Optional<SuperAppObjectEntity> datingProfile = objectCrud.findById(targetObject.getObjectId().getInternalObjectId());

        // check cardinal
        // add like to my profile dating
        // add like to other profile dating

        return null;
    }
}
