package superapp.miniapps.command.eventImpl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.miniapps.command.MiniAppsCommand;
import superapp.miniapps.eventsMiniApp.Event;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventJoinEventCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;

    @Autowired
    public EventJoinEventCommand(ObjectCrud objectCrudDB) {
        this.objectCrudDB = objectCrudDB;
    }

    @Override
    public Object execute(MiniAppCommandBoundary commandBoundary) {
        // todo: ERROR - check if user is in the event and add the mail to the attendees list
        String eventObjectId = commandBoundary.getTargetObject().getObjectId().getSuperapp() + "_" + commandBoundary.getTargetObject().getObjectId().getInternalObjectId();
        String userEmail = commandBoundary.getInvokedBy().getUserId().getEmail();


        this.objectCrudDB.addAttendeeToEvent(eventObjectId, userEmail);

        Map<String, String> event = new HashMap<>();
        event.put("status", "success");
        return event;
    }


}
