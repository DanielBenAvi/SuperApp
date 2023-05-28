package superapp.miniapps.command.eventImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.boundaries.SuperAppObjectBoundary;
import superapp.logic.utils.convertors.ObjectConvertor;
import superapp.miniapps.command.MiniAppsCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class EventGetEventsBaseOnPreferencesCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;

    private final ObjectConvertor objectConvertor;

    @Autowired
    public EventGetEventsBaseOnPreferencesCommand(ObjectCrud objectCrudDB, ObjectConvertor objectConvertor) {
        this.objectCrudDB = objectCrudDB;
        this.objectConvertor = objectConvertor;
    }

    @Override
    public List<SuperAppObjectBoundary> execute(MiniAppCommandBoundary commandBoundary) {
        //todo: fix the return type
        // get the user email and superapp from the command boundary
        String userEmail = commandBoundary.getInvokedBy().getUserId().getEmail();
        String superApp = commandBoundary.getInvokedBy().getUserId().getSuperapp();
        String userId = superApp + "_" + userEmail;
        String type = "EVENT";

        // get the user preferences from the database
        SuperAppObjectEntity superAppObjectEntity = this.objectCrudDB.findByCreatedByAndType(userId, "USER_DETAILS");

        SuperAppObjectBoundary superAppObjectBoundary = this.objectConvertor.toBoundary(superAppObjectEntity);

        ObjectMapper objectMapper = new ObjectMapper();
        String[] preferences = objectMapper.convertValue(superAppObjectBoundary.getObjectDetails().get("preferences"), String[].class);


        int page = commandBoundary
                .getCommandAttributes()
                .get("page") == null ? 0 : Integer.parseInt(commandBoundary.getCommandAttributes().get("page").toString());

        int size = commandBoundary
                .getCommandAttributes()
                .get("size") == null ? 20 : Integer.parseInt(commandBoundary.getCommandAttributes().get("size").toString());


        long now = System.currentTimeMillis();



        return this.objectCrudDB
                .findAllEventsBaseOnPreferencesCommand(type, userId, now, preferences, PageRequest.of(page, size, Sort.by("creationTimestamp").descending()))
                .stream()
                .map(this.objectConvertor::toBoundary)
                .toList();
    }

}
