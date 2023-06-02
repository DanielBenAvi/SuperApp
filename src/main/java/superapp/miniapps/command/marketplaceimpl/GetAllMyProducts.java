package superapp.miniapps.command.marketplaceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.boundaries.SuperAppObjectBoundary;
import superapp.logic.utils.convertors.ObjectConvertor;
import superapp.miniapps.command.MiniAppsCommand;

import java.util.List;

@Component
public class GetAllMyProducts implements MiniAppsCommand {
    private final ObjectCrud objectCrudDB;

    private final ObjectConvertor objectConvertor;

    @Autowired
    public GetAllMyProducts(ObjectCrud objectCrudDB, ObjectConvertor objectConvertor) {
        this.objectCrudDB = objectCrudDB;
        this.objectConvertor = objectConvertor;
    }

    @Override
    public List<SuperAppObjectBoundary> execute(MiniAppCommandBoundary commandBoundary) {
        String userEmail = commandBoundary.getInvokedBy().getUserId().getEmail();
        String userId = commandBoundary.getInvokedBy().getUserId().getSuperapp() + "_" + userEmail;
        String type = "PRODUCT";
        int page = commandBoundary.getCommandAttributes().get("page") == null ? 0 : Integer.parseInt(commandBoundary.getCommandAttributes().get("page").toString());

        int size = commandBoundary.getCommandAttributes().get("size") == null ? 20 : Integer.parseInt(commandBoundary.getCommandAttributes().get("size").toString());

        return this.objectCrudDB.findAllMyProducts(userId, type, PageRequest.of(page, size))
                .stream()
                .map(this.objectConvertor::toBoundary)
                .toList();
    }
}
