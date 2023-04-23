package superapp.logic;

import superapp.logic.boundaries.SuperAppObjectBoundary;

import java.util.List;
import java.util.Optional;

public interface ObjectsService {

    public SuperAppObjectBoundary createObject(SuperAppObjectBoundary objectBoundary);
    public SuperAppObjectBoundary updateObject(String objectSuperApp, String internalObjectId, SuperAppObjectBoundary update);
    public Optional<SuperAppObjectBoundary> getSpecificObject(String objectSuperApp, String internalObjectId);
    public List<SuperAppObjectBoundary> getAllObjects();
    void deleteAllObjects();
}
