package superapp.data;

import org.springframework.data.repository.ListCrudRepository;

public interface ObjectCrud extends ListCrudRepository<SuperAppObjectEntity, String> {
}
