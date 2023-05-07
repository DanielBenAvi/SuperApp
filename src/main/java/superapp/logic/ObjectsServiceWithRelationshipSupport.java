package superapp.logic;

import superapp.logic.boundaries.ObjectId;
import superapp.logic.boundaries.SuperAppObjectBoundary;

import java.util.List;
import java.util.Optional;

public interface ObjectsServiceWithRelationshipSupport extends ObjectsService {
    public void addChild(String superapp, String parentId, ObjectId childId);

    public List<SuperAppObjectBoundary> getChildren(String superapp, String parentInternalObjectId);

    public Optional<SuperAppObjectBoundary> getParent(String superapp, String childInternalObjectId);
}
