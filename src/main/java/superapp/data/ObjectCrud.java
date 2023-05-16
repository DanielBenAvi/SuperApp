package superapp.data;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ObjectCrud extends MongoRepository<SuperAppObjectEntity, String> {
    public List<SuperAppObjectEntity> findAllByParent_objectId(@Param("parentObjectId") String parentObjectId, Pageable pageable);

    public List<SuperAppObjectEntity> findAllByChildren_objectId(@Param("childObjectId") String childObjectId, Pageable pageable);
}
