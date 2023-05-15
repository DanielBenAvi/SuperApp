package superapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import superapp.logic.ObjectsServiceWithPaging;
import superapp.logic.boundaries.ObjectId;
import superapp.logic.boundaries.SuperAppObjectBoundary;

@RestController
public class RelationshipController {
    private final ObjectsServiceWithPaging objectsService;

    @Autowired
    public RelationshipController(ObjectsServiceWithPaging objectsService) {
        this.objectsService = objectsService;
    }

    @PutMapping(path = {"/superapp/objects/{superapp}/{internalObjectId}/children"},
                consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void updateChildren(
            @RequestParam(name = "userSuperapp", required = false) String userSuperapp,
            @RequestParam(name = "userEmail", required = false) String userEmail,
            @PathVariable("superapp") String superapp,
            @PathVariable("internalObjectId") String parentInternalObjectId,
            @RequestBody ObjectId childObjectId) {

        this.objectsService
                .addChild(superapp, parentInternalObjectId, childObjectId, userSuperapp, userEmail);
    }


    @GetMapping(path = {"/superapp/objects/{superapp}/{internalObjectId}/children"},
                produces = {MediaType.APPLICATION_JSON_VALUE})
    public SuperAppObjectBoundary[] getChildren(
            @RequestParam(name = "userSuperapp", required = false) String userSuperapp,
            @RequestParam(name = "userEmail", required = false) String userEmail,
            @RequestParam(name = "size", required = false, defaultValue = "15") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @PathVariable("superapp") String superapp,
            @PathVariable("internalObjectId") String parentInternalObjectId) {

        return this.objectsService
                .getChildren(superapp, parentInternalObjectId, userSuperapp, userEmail, size, page)
                .toArray(new SuperAppObjectBoundary[0]);
    }

    @GetMapping(path = {"/superapp/objects/{superapp}/{internalObjectId}/parents"},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public SuperAppObjectBoundary[] getParent(
            @RequestParam(name = "userSuperapp", required = false) String userSuperapp,
            @RequestParam(name = "userEmail", required = false) String userEmail,
            @RequestParam(name = "size", required = false, defaultValue = "15") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @PathVariable("superapp") String superapp,
            @PathVariable("internalObjectId") String childInternalObjectId) {

        return this.objectsService
                .getParent(superapp, childInternalObjectId, userSuperapp, userEmail, size, page)
                .toArray(new SuperAppObjectBoundary[0]);
    }

}
