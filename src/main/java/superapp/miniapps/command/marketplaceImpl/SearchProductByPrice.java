package superapp.miniapps.command.marketplaceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.utils.convertors.ObjectConvertor;
import superapp.miniapps.command.MiniAppsCommand;

@Component
public class SearchProductByPrice implements MiniAppsCommand {
    private final ObjectCrud objectCrudDB;
    private final ObjectConvertor objectConvertor;

    @Autowired
    public SearchProductByPrice(ObjectCrud objectCrudDB, ObjectConvertor objectConvertor) {

        this.objectCrudDB = objectCrudDB;
        this.objectConvertor = objectConvertor;
    }

    @Override
    public Object execute(MiniAppCommandBoundary commandBoundary) {
        double maxPrice = (double) commandBoundary.getCommandAttributes().get("maxPrice");
        double minPrice = (double) commandBoundary.getCommandAttributes().get("minPrice");
        String type = "MARKETPLACE";
        int page = commandBoundary.getCommandAttributes().get("page") == null ? 0 : Integer.parseInt(commandBoundary.getCommandAttributes().get("page").toString());
        int size = commandBoundary.getCommandAttributes().get("size") == null ? 20 : Integer.parseInt(commandBoundary.getCommandAttributes().get("size").toString());

        return this.objectCrudDB.findAllProductsByPrice(type, maxPrice, minPrice,  PageRequest.of(page, size, Sort.Direction.DESC, "price"))
                .stream()
                .map(this.objectConvertor::toBoundary)
                .toList();
    }
}