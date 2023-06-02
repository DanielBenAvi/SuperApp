package superapp.miniapps.command.marketplaceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.utils.convertors.ObjectConvertor;
import superapp.miniapps.command.MiniAppsCommand;

@Component
public class SearchProductByCategory implements MiniAppsCommand {
    private final ObjectCrud objectCrudDB;
    private final ObjectConvertor objectConvertor;

    @Autowired
    public SearchProductByCategory(ObjectCrud objectCrudDB, ObjectConvertor objectConvertor) {
        this.objectCrudDB = objectCrudDB;
        this.objectConvertor = objectConvertor;
    }

    @Override
    public Object execute(MiniAppCommandBoundary commandBoundary) {
        ObjectMapper objectMapper = new ObjectMapper();
        String[] preference = objectMapper.convertValue(commandBoundary.getCommandAttributes().get("preferences"), String[].class);
        String type = "PRODUCT";
        int page = commandBoundary.getCommandAttributes().get("page") == null ? 0 : Integer.parseInt(commandBoundary.getCommandAttributes().get("page").toString());
        int size = commandBoundary.getCommandAttributes().get("size") == null ? 20 : Integer.parseInt(commandBoundary.getCommandAttributes().get("size").toString());

        return this.objectCrudDB.findAllProductsByCategory(type, preference, PageRequest.of(page, size))
                .stream()
                .map(this.objectConvertor::toBoundary)
                .toList();
    }
}
