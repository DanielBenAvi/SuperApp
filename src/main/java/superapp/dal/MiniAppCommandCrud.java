package superapp.dal;

import org.springframework.data.repository.ListCrudRepository;
import superapp.dal.entities.MiniAppCommandEntity;

public interface MiniAppCommandCrud extends ListCrudRepository<MiniAppCommandEntity, String> {
}
