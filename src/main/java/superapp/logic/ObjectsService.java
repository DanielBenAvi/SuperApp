package superapp.logic;

import superapp.logic.boundaries.SuperAppObjectBoundary;

import java.util.List;
import java.util.Optional;

public interface ObjectsService {

    public SuperAppObjectBoundary createObject(SuperAppObjectBoundary objectBoundary);
    @Deprecated
    public SuperAppObjectBoundary updateObject(String objectSuperApp, String internalObjectId, SuperAppObjectBoundary update);
    @Deprecated
    public Optional<SuperAppObjectBoundary> getSpecificObject(String objectSuperApp, String internalObjectId);
    @Deprecated
    public List<SuperAppObjectBoundary> getAllObjects();

    @Deprecated
    public void deleteAllObjects();
}
