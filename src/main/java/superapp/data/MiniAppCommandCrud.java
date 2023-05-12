package superapp.data;


import org.springframework.data.mongodb.repository.MongoRepository;



public interface MiniAppCommandCrud extends MongoRepository<MiniAppCommandEntity, String> {

}
