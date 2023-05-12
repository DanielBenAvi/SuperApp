package superapp.logic;

public interface ObjectsServiceWithPaging extends ObjectsServiceWithRelationshipSupport{

    public void deleteAllObjects(String userSuperapp,String userEmail);
}
