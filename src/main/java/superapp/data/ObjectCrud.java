package superapp.data;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ObjectCrud extends MongoRepository<SuperAppObjectEntity, String> {
}
