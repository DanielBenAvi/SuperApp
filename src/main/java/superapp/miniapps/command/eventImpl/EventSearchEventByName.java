package superapp.miniapps.command.eventImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.ConvertHelp;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.boundaries.SuperAppObjectBoundary;
import superapp.miniapps.command.MiniAppsCommand;

import java.util.Date;
import java.util.List;

@Component
public class EventSearchEventByName implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;

    @Autowired
    public EventSearchEventByName(ObjectCrud objectCrudDB) {
        this.objectCrudDB = objectCrudDB;
    }

    @Override
    public List<SuperAppObjectBoundary> execute(MiniAppCommandBoundary commandBoundary) {
        String name = commandBoundary.getCommandAttributes().get("name").toString();
        String type = "EVENT";
        Date now = new Date();
        int page = commandBoundary.getCommandAttributes().get("page") == null ? 0 : Integer.parseInt(commandBoundary.getCommandAttributes().get("page").toString());
        int size = commandBoundary.getCommandAttributes().get("size") == null ? 20 : Integer.parseInt(commandBoundary.getCommandAttributes().get("size").toString());


        return this.objectCrudDB.searchEventByName(type, now, name, PageRequest.of(page, size, Sort.by("creationTimestamp").descending()))
                .stream()
                .map(this::convertEntityToBoundary)
                .toList();
    }


    private SuperAppObjectBoundary convertEntityToBoundary(SuperAppObjectEntity entity) {

        SuperAppObjectBoundary boundary = new SuperAppObjectBoundary();

        boundary.setObjectId(ConvertHelp.strObjectIdToBoundary(entity.getObjectId()));
        boundary.setType(entity.getType());
        boundary.setAlias(entity.getAlias());
        boundary.setActive(entity.getActive());
        boundary.setCreationTimestamp(entity.getCreationTimestamp());
        boundary.setLocation(ConvertHelp.locationEntityToBoundary(entity.getLocation()));
        boundary.setCreatedBy(ConvertHelp.strCreateByToBoundary(entity.getCreatedBy()));

        boundary.setObjectDetails(entity.getObjectDetails());

        return boundary;
    }
}
