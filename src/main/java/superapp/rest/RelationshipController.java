package superapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import superapp.logic.ConvertHelp;
import superapp.logic.ObjectsServiceWithRelationshipSupport;
import superapp.logic.boundaries.ObjectId;
import superapp.logic.boundaries.SuperAppObjectBoundary;
import superapp.logic.excptions.NotFoundException;

@RestController
public class RelationshipController {
    private ObjectsServiceWithRelationshipSupport objectsService;

    @Autowired
    public RelationshipController(ObjectsServiceWithRelationshipSupport objectsService) {
        super();
        this.objectsService = objectsService;
    }

    @RequestMapping(method = {RequestMethod.PUT},
            path = {"/superapp/objects/{superapp}/{internalObjectId}/children"},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void updateChildren(
            @PathVariable("superapp") String superapp,
            @PathVariable("internalObjectId") String OriginInternalObjectId,
            @RequestBody ObjectId childObjectId) {
        this.objectsService
                .addChild(
                        superapp + ConvertHelp.DELIMITER_ID + OriginInternalObjectId,
                        superapp + ConvertHelp.DELIMITER_ID + childObjectId.getInternalObjectId()
                );
    }


    @RequestMapping(method = {RequestMethod.GET},
            path = {"/superapp/objects/{superapp}/{internalObjectId}/children"},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public SuperAppObjectBoundary[] getChildren(
            @PathVariable("superapp") String superapp,
            @PathVariable("internalObjectId") String originInternalObjectId
    ) {
        return this.objectsService
                .getChildren(
                        superapp + ConvertHelp.DELIMITER_ID + originInternalObjectId)
                .toArray(new SuperAppObjectBoundary[0]
                );
    }

    @RequestMapping(method = {RequestMethod.GET},
            path = {"/superapp/objects/{superapp}/{internalObjectId}/parents"},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public SuperAppObjectBoundary getParent(
            @PathVariable("superapp") String superapp,
            @PathVariable("internalObjectId") String childInternalObjectId
    ) {
        return this.objectsService
                .getOrigin(
                        superapp + ConvertHelp.DELIMITER_ID + childInternalObjectId)
                .orElseThrow(() -> new NotFoundException("could not find origin for object with id: " + childInternalObjectId));
    }


}
