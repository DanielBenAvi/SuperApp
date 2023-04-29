package superapp.data;

import org.springframework.data.repository.ListCrudRepository;

public interface UserCrud extends ListCrudRepository<UserEntity, String> {
}
