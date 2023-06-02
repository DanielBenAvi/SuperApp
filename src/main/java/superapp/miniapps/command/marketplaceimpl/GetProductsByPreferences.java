package superapp.miniapps.command.marketplaceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
        String userEmail = commandBoundary.getInvokedBy().getUserId().getEmail();
        String userId = commandBoundary.getInvokedBy().getUserId().getSuperapp() + "_" + userEmail;
        String type = "PRODUCT";

        // get the user preferences from the database
        SuperAppObjectEntity superAppObjectEntity = this.objectCrudDB.findByCreatedByAndType(userId, "USER_DETAILS");


        if (Objects.isNull(superAppObjectEntity)) {
            return new ArrayList<>();
        }

        SuperAppObjectBoundary superAppObjectBoundary = this.objectConvertor.toBoundary(superAppObjectEntity);

        ObjectMapper objectMapper = new ObjectMapper();
        String[] preferences = objectMapper.convertValue(superAppObjectBoundary.getObjectDetails().get("preferences"), String[].class);

        int page = commandBoundary.getCommandAttributes().get("page") == null ? 0 : Integer.parseInt(commandBoundary.getCommandAttributes().get("page").toString());
        int size = commandBoundary.getCommandAttributes().get("size") == null ? 20 : Integer.parseInt(commandBoundary.getCommandAttributes().get("size").toString());

        return this.objectCrudDB.findAllProductsByPreferences(type, preferences,userId, PageRequest.of(page, size))
                .stream()
                .map(this.objectConvertor::toBoundary)
                .toList();
    }
}
