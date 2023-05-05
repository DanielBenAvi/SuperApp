package superapp.logic;

import superapp.logic.boundaries.SuperAppObjectBoundary;

import java.util.List;
import java.util.Optional;

public interface ObjectsServiceWithRelationshipSupport extends ObjectsService {
    public void addChild(String originId, String childId);

    public List<SuperAppObjectBoundary> getChildren(String originId);

    public Optional<SuperAppObjectBoundary> getOrigin(String childId);
}
