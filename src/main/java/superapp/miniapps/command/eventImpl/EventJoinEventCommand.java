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
        String eventObjectId = commandBoundary.getCommandAttributes().get("eventId").toString();
        String userEmail = commandBoundary.getInvokedBy().getUserId().getEmail();


        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(this.objectCrudDB.findById(eventObjectId).isPresent(), Event.class);
    }


}
