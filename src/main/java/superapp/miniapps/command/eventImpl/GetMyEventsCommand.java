package superapp.miniapps.command.eventImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.miniapps.command.MiniAppsCommand;

@Component
public class GetMyEventsCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;

    @Autowired
    public GetMyEventsCommand (ObjectCrud objectCrudDB){
        this.objectCrudDB = objectCrudDB;
    }
    @Override
    public Object execute(MiniAppCommandBoundary commandBoundary) {

        return null;
    }
}
