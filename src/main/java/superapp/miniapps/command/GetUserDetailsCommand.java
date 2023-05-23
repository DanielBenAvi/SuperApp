package superapp.miniapps.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.logic.boundaries.MiniAppCommandBoundary;

import java.util.Map;

@Component
public class GetUserDetailsCommand implements MiniAppsCommand{

    private final ObjectCrud objectCrudDB;

    @Autowired
    public GetUserDetailsCommand(ObjectCrud objectCrudDB) {
        this.objectCrudDB = objectCrudDB;
    }

    @Override
    public Object execute(MiniAppCommandBoundary commandBoundary) {
        Map<String, Object> commandAttr = commandBoundary.getCommandAttributes();

        // TODO: Add validation
        String createdBy = commandAttr.get("createdBy").toString(); // todo : createdBy shall be as boundary
        String type = commandAttr.get("type").toString();

        Object result = this.objectCrudDB.findByCreatedByAndType(createdBy, type);

        return result;
    }
}
