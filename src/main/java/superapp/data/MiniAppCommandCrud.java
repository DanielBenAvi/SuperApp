package superapp.data;

import org.springframework.data.repository.ListCrudRepository;

public interface MiniAppCommandCrud extends ListCrudRepository<MiniAppCommandEntity, String> {
}
