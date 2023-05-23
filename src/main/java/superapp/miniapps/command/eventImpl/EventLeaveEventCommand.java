package superapp.miniapps.command.eventImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.miniapps.command.MiniAppsCommand;

import java.util.HashMap;
import java.util.Map;

@Component
public class EventLeaveEventCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;

    @Autowired
    public EventLeaveEventCommand(ObjectCrud objectCrudDB) {
        this.objectCrudDB = objectCrudDB;
    }

    @Override
    public Object execute(MiniAppCommandBoundary commandBoundary) {
        String eventObjectId = commandBoundary.getTargetObject().getObjectId().getSuperapp() + "_" + commandBoundary.getTargetObject().getObjectId().getInternalObjectId();
        String userEmail = commandBoundary.getInvokedBy().getUserId().getEmail();


        this.objectCrudDB.removeAttendeeFromEvent(eventObjectId, userEmail);

        Map<String, String> event = new HashMap<>();
        event.put("status", "success");
        return event;
    }


}
