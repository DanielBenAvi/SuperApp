package superapp.miniapps.command.eventImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.boundaries.SuperAppObjectBoundary;
import superapp.logic.utils.convertors.ObjectConvertor;
import superapp.miniapps.command.MiniAppsCommand;

import java.util.List;

@Component
public class EventGetMyEventsCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;
    private final ObjectConvertor objectConvertor;

    @Autowired
    public EventGetMyEventsCommand(ObjectCrud objectCrudDB, ObjectConvertor objectConvertor) {
        this.objectCrudDB = objectCrudDB;
        this.objectConvertor = objectConvertor;
    }

    @Override
    public List<SuperAppObjectBoundary> execute(MiniAppCommandBoundary commandBoundary) {
        String userEmail = commandBoundary.getInvokedBy().getUserId().getEmail();
        String type = "EVENT";
        long now = System.currentTimeMillis();

        int page = commandBoundary
                .getCommandAttributes()
                .get("page") == null ? 0 : Integer.parseInt(commandBoundary.getCommandAttributes().get("page").toString());

        int size = commandBoundary
                .getCommandAttributes()
                .get("size") == null ? 20 : Integer.parseInt(commandBoundary.getCommandAttributes().get("size").toString());


        return this.objectCrudDB.findAllByTypeAndMyEvents(userEmail, type, now,
                        PageRequest.of(page, size, Sort.by("creationTimestamp").descending()))
                .stream()
                .map(this.objectConvertor::toBoundary)
                .toList();
    }

}
