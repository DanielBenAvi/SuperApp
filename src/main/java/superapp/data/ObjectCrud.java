package superapp.data;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ObjectCrud extends MongoRepository<SuperAppObjectEntity, String> {


    public Optional<SuperAppObjectEntity> findByObjectIdAndActiveIsTrue(@Param("objectId") String objectId);
    public List<SuperAppObjectEntity> findAllByActiveIsTrue(Pageable pageable);

    public List<SuperAppObjectEntity> findAllByParent_objectIdAndActiveIsTrue(@Param("parentObjectId") String parentObjectId, Pageable pageable);

    public List<SuperAppObjectEntity> findAllByChildren_objectIdAndActiveIsTrue(@Param("childObjectId") String childObjectId, Pageable pageable);

    public List<SuperAppObjectEntity> findAllByParent_objectId(@Param("parentObjectId") String parentObjectId, Pageable pageable);

    public List<SuperAppObjectEntity> findAllByChildren_objectId(@Param("childObjectId") String childObjectId, Pageable pageable);


    public List<SuperAppObjectEntity> findAllByType(@Param("type") String type, Pageable pageable);
    public List<SuperAppObjectEntity> findAllByTypeAndActiveIsTrue(@Param("type") String type, Pageable pageable);
    public List<SuperAppObjectEntity> findAllByAlias(@Param("alias") String alias, Pageable pageable);
    public List<SuperAppObjectEntity> findAllByAliasAndActiveIsTrue(@Param("alias") String alias, PageRequest pageRequest);
    public List<SuperAppObjectEntity> findAllByLocation(Double lat, Double lng, Double distance, String distanceUnits, Pageable pageable);


}
