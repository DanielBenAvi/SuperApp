package superapp.dal;

import org.springframework.data.repository.ListCrudRepository;
import superapp.dal.entities.SuperAppObjectEntity;

public interface ObjectCrud extends ListCrudRepository<SuperAppObjectEntity, String> {
}
