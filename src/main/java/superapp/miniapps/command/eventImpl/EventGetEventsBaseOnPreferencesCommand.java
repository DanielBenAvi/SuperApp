package superapp.miniapps.command.eventImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.expression.spel.ast.TypeReference;
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
public class EventGetEventsBaseOnPreferencesCommand implements MiniAppsCommand {

    private final ObjectCrud objectCrudDB;

    @Autowired
    public EventGetEventsBaseOnPreferencesCommand(ObjectCrud objectCrudDB) {
        this.objectCrudDB = objectCrudDB;
    }

    @Override
    public List<SuperAppObjectBoundary> execute(MiniAppCommandBoundary commandBoundary) {
        //todo: fix the return type
        // get the user email and superapp from the command boundary
        String userEmail = commandBoundary.getInvokedBy().getUserId().getEmail();
        String superApp = commandBoundary.getInvokedBy().getUserId().getSuperapp();
        String userId = superApp + "_" + userEmail;

        // get the user preferences from the database
        SuperAppObjectEntity superAppObjectEntity = this.objectCrudDB.findByCreatedByAndType(userId, "USER_DETAILS");

// Convert the object to a string representation
        String objString = superAppObjectEntity.getObjectDetails().get("preferences").toString();

// Split the string representation into individual parts or elements
        String[] elements = objString.split(" "); // You can use a different delimiter if needed

// Store the elements in an array of strings
        String[] array = new String[elements.length];
        System.arraycopy(elements, 0, array, 0, elements.length);

        System.out.println("The array of strings is: " + array);
        // convert the user preferences to a boundary


        // get the events from the database

        return null;
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
