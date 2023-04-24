package superapp.dal;

import org.springframework.data.repository.ListCrudRepository;
import superapp.dal.entities.UserEntity;

public interface UserCrud extends ListCrudRepository<UserEntity, String> {
}
