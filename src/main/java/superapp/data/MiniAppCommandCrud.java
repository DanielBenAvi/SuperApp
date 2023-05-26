package superapp.data;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface MiniAppCommandCrud extends MongoRepository<MiniAppCommandEntity, String> {

    List<MiniAppCommandEntity> findAllByCommandIdContaining(String miniAppName, Pageable pageable);

}
