package superapp.logic;

import superapp.logic.boundaries.ObjectId;
import superapp.logic.boundaries.SuperAppObjectBoundary;

import java.util.List;
import java.util.Optional;

public interface ObjectsServiceWithPaging extends ObjectsServiceWithRelationshipSupport{


    // paging support

    public SuperAppObjectBoundary updateObject(String objectSuperApp, String internalObjectId,
                                               SuperAppObjectBoundary update, String userSuperapp,String userEmail);

    public Optional<SuperAppObjectBoundary> getSpecificObject(String objectSuperApp, String internalObjectId,
                                                              String userSuperapp,String userEmail);
    public List<SuperAppObjectBoundary> getAllObjects(String userSuperapp,String userEmail, int size, int page);


    // relationship method
    public void addChild(String superapp, String parentId, ObjectId childId,
                         String userSuperapp,String userEmail);

    public List<SuperAppObjectBoundary> getChildren(String superapp, String parentInternalObjectId,
                                                    String userSuperapp,String userEmail, int size, int page);

    public List<SuperAppObjectBoundary> getParent(String superapp, String childInternalObjectId,
                                                  String userSuperapp,String userEmail, int size, int page);


    public void deleteAllObjects(String userSuperapp,String userEmail);
}
