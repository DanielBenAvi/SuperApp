package superapp.logic;

import superapp.logic.boundaries.SuperAppObjectBoundary;

import java.util.List;

public interface ObjectsService {

    SuperAppObjectBoundary createObject(SuperAppObjectBoundary objectBoundary);
    SuperAppObjectBoundary updateObject(String objectSuperApp, String internalObjectId, SuperAppObjectBoundary update);
    SuperAppObjectBoundary getSpecificObject(String objectSuperApp, String internalObjectId);
    List<SuperAppObjectBoundary> getAllObjects();
    void deleteAllObjects();
}
