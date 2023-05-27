package superapp.miniapps.command.marketplaceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.utils.convertors.ObjectConvertor;
import superapp.miniapps.command.MiniAppsCommand;

import java.util.List;

@Component
public class GetProductsByPreferences implements MiniAppsCommand {
    private final ObjectCrud objectCrudDB;

    private final ObjectConvertor objectConvertor;

    @Autowired
    public GetProductsByPreferences(ObjectCrud objectCrudDB, ObjectConvertor objectConvertor) {
        this.objectCrudDB = objectCrudDB;
        this.objectConvertor = objectConvertor;
    }

    @Override
    public Object execute(MiniAppCommandBoundary commandBoundary) {
       //TODO:fix implementation
        List<String> preferencesList = List.of();
        String type = "MARKETPLACE";

        int page = commandBoundary.getCommandAttributes().get("page") == null ? 0 : Integer.parseInt(commandBoundary.getCommandAttributes().get("page").toString());
        int size = commandBoundary.getCommandAttributes().get("size") == null ? 20 : Integer.parseInt(commandBoundary.getCommandAttributes().get("size").toString());

        return this.objectCrudDB.findAllProductsByPreferences(type, preferencesList, PageRequest.of(page, size))
                .stream()
                .map(this.objectConvertor::toBoundary)
                .toList();
    }
}
