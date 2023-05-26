package superapp.data;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserCrud extends MongoRepository<UserEntity, String> {

}
