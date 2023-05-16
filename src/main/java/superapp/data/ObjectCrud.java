package superapp.data;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ObjectCrud extends MongoRepository<SuperAppObjectEntity, String> {
//    public List<SuperAppObjectEntity> findAllChildrenByObject_Id(@Param("objectId") String objectId, Pageable pageable);
//
//    public List<SuperAppObjectEntity> findAllParentsByObject_Id(@Param("objectId") String objectId, Pageable pageable);
}
