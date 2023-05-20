package superapp.data;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ObjectCrud extends MongoRepository<SuperAppObjectEntity, String> {


    public Optional<SuperAppObjectEntity> findByObjectIdAndActiveIsTrue(@Param("objectId") String objectId);

    public List<SuperAppObjectEntity> findAllByActiveIsTrue(Pageable pageable);

    public List<SuperAppObjectEntity> findAllByParent_objectIdAndActiveIsTrue(@Param("parentObjectId") String parentObjectId, Pageable pageable);

    public List<SuperAppObjectEntity> findAllByChildren_objectIdAndActiveIsTrue(@Param("childObjectId") String childObjectId, Pageable pageable);

    public List<SuperAppObjectEntity> findAllByParent_objectId(@Param("parentObjectId") String parentObjectId, Pageable pageable);

    public List<SuperAppObjectEntity> findAllByChildren_objectId(@Param("childObjectId") String childObjectId, Pageable pageable);

    public List<SuperAppObjectEntity> findAllByType(@Param("type") String type, Pageable pageable);

    public List<SuperAppObjectEntity> findAllByTypeAndActiveIsTrue(@Param("type") String type, Pageable pageable);

    public List<SuperAppObjectEntity> findAllByAlias(@Param("alias") String alias, Pageable pageable);

    public List<SuperAppObjectEntity> findAllByAliasAndActiveIsTrue(@Param("alias") String alias, PageRequest pageRequest);

    public List<SuperAppObjectEntity> findAllByLocation(@Param("lat")Double lat,
                                                        @Param("lng")Double lng,
                                                        @Param("distance")Double distance,
                                                        @Param("distanceUnits")String distanceUnits,
                                                        Pageable pageable);

    @Query("{'type' :?1, 'objectDetails.attendees': {'$in':[?0] }, 'objectDetails.date': {'$gt': ?2 } }")
    public List<SuperAppObjectEntity> findAllByTypeAndMyEvents(String userEmail, String type, Date now, PageRequest creationTimestamp);

    // search by event name
    @Query("{'type': ?0, 'objectDetails.date': {'$gt': ?1 }, 'objectDetails.name': {'$regex': ?2 }}")
    public List<SuperAppObjectEntity> searchEventByName(String type, Date now, String name, Pageable pageable);
    // search by event date

    @Query("{'type': ?0, 'objectDetails.date': {'$gt': ?1 } ,'objectDetails.date': {'$gt': ?2, '$lt': ?3 }}")
    public List<SuperAppObjectEntity> searchEventByDates(String type, Date now, Date startDate, Date endDate, Pageable pageable);

    // search by event contains preferences
    @Query("{'type': ?0, 'objectDetails.date': {'$gt': ?1 }, 'objectDetails.preferences': {'$in': [?2] }}")
    public List<SuperAppObjectEntity> searchEventByPreferences(String type, Date now, String preference, Pageable pageable); //p    reference must be exact string

    // search by event location
    // todo : fix this query
    @Query("{'type': ?0, 'objectDetails.date': {'$gt': ?1 }, " +
            "'objectDetails.location': {'$near': {'$geometry': {'type': 'Point', 'coordinates': [?2, ?3]}, '$maxDistance': ?4}}}")
    public List<SuperAppObjectEntity> searchEventByLocation(String type, Date now, Double lat, Double lng, Double distance, Pageable pageable);

}
