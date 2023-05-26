package superapp.logic.utils.convertors;

import org.springframework.data.geo.Point;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.boundaries.*;

public interface ObjectConvertor {
    public SuperAppObjectBoundary toBoundary(SuperAppObjectEntity entity);
    public SuperAppObjectEntity toEntity(SuperAppObjectBoundary boundary);

    /** Object **/
    public String objectIdToEntity(ObjectId objectId);
    public ObjectId objectIdToBoundary(String objectId);
    public String createByToEntity(CreatedBy createdBy);
    public CreatedBy createByToBoundary(String cratedBy);
    public Point locationToEntity(Location location);
    public Location locationToBoundary(Point point);
}
