package superapp.logic;

import superapp.logic.boundaries.ObjectId;
import superapp.logic.boundaries.SuperAppObjectBoundary;

import java.util.List;

public interface ObjectsServiceWithRelationshipSupport extends ObjectsService {

    @Deprecated
    public void addChild(String superapp, String parentId, ObjectId childId);
    @Deprecated
    public List<SuperAppObjectBoundary> getChildren(String superapp, String parentInternalObjectId);
    @Deprecated
    public List<SuperAppObjectBoundary> getParent(String superapp, String childInternalObjectId);
}
