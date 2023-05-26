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
public class EventSearchEventByDate implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;

    private final ObjectConvertor objectConvertor;

    @Autowired
    public EventSearchEventByDate(ObjectCrud objectCrudDB, ObjectConvertor objectConvertor) {
        this.objectCrudDB = objectCrudDB;
        this.objectConvertor = objectConvertor;
    }

    @Override
    public List<SuperAppObjectBoundary> execute(MiniAppCommandBoundary commandBoundary) {
        long startDate = (long) commandBoundary.getCommandAttributes().get("startDate");
        long endDate = (long) commandBoundary.getCommandAttributes().get("endDate");
        String type = "EVENT";
        long now = System.currentTimeMillis();

        int page = commandBoundary
                .getCommandAttributes()
                .get("page") == null ? 0 : Integer.parseInt(commandBoundary.getCommandAttributes().get("page").toString());

        int size = commandBoundary
                .getCommandAttributes()
                .get("size") == null ? 20 : Integer.parseInt(commandBoundary.getCommandAttributes().get("size").toString());


        return this.objectCrudDB
                .searchEventByDates(type, now, startDate, endDate, PageRequest.of(page, size, Sort.by("creationTimestamp").descending()))
                .stream()
                .map(this.objectConvertor::toBoundary)
                .toList();
    }

}
