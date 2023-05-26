package superapp.logic.utils.convertors.impl;

import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.boundaries.*;
import superapp.logic.utils.convertors.ConvertIdsHelper;
import superapp.logic.utils.convertors.ObjectConvertor;


@Component
public class ConvertSuperappObject implements ObjectConvertor {


    @Override
    public SuperAppObjectBoundary toBoundary(SuperAppObjectEntity entity) {

        return new SuperAppObjectBoundary()
                .setObjectId(this.objectIdToBoundary(entity.getObjectId()))
                .setType(entity.getType())
                .setAlias(entity.getAlias())
                .setActive(entity.getActive())
                .setCreationTimestamp(entity.getCreationTimestamp())
                .setLocation(this.locationToBoundary(entity.getLocation()))
                .setCreatedBy(this.createByToBoundary(entity.getCreatedBy()))
                .setObjectDetails(entity.getObjectDetails());

    }

    @Override
    public SuperAppObjectEntity toEntity(SuperAppObjectBoundary boundary) {

        return new SuperAppObjectEntity()
                .setObjectId(this.objectIdToEntity(boundary.getObjectId()))
                .setCreationTimestamp(boundary.getCreationTimestamp())
                .setType(boundary.getType())
                .setAlias(boundary.getAlias())
                .setActive(boundary.getActive() != null ? boundary.getActive() : true)
                .setLocation(this.locationToEntity(boundary.getLocation()))
                .setCreatedBy(this.createByToEntity(boundary.getCreatedBy()))
                .setObjectDetails(boundary.getObjectDetails());
    }

    @Override
    public String objectIdToEntity(ObjectId objectId) {
        return ConvertIdsHelper
                .concatenateIds(new String[]{ objectId.getSuperapp(),
                        objectId.getInternalObjectId() });
    }

    @Override
    public ObjectId objectIdToBoundary(String objectId) {

        String[] str = ConvertIdsHelper.splitConcretedIds(objectId);

        return new ObjectId()
                        .setSuperapp(str[0])
                        .setInternalObjectId(str[1]);
    }

    @Override
    public String createByToEntity(CreatedBy createdBy) {
        return ConvertIdsHelper
                .concatenateIds(new String[]{ createdBy.getUserId().getSuperapp(),
                        createdBy.getUserId().getEmail()});
    }

    @Override
    public CreatedBy createByToBoundary(String cratedBy) {

        String[] str = ConvertIdsHelper.splitConcretedIds(cratedBy);

        return new CreatedBy()
                .setUserId(new UserId()
                        .setSuperapp(str[0])
                        .setEmail(str[1]));
    }

    @Override
    public Point locationToEntity(Location location) {

        return new Point(location.getLat(), location.getLng());
    }

    @Override
    public Location locationToBoundary(Point point) {

        return new Location(point.getX(), point.getY());

    }


}
