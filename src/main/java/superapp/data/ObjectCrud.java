package superapp.data;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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


    public List<SuperAppObjectEntity> findAllByLocationNear(@Param("location") Point location,
                                                            @Param("maxDistance") Distance maxDistance,
                                                            Pageable pageable);

    public List<SuperAppObjectEntity> findAllByActiveIsTrueAndLocationNear(@Param("location") Point location,
                                                                           @Param("maxDistance") Distance maxDistance,
                                                                           Pageable pageable);

    @Query("{'type' :?1, 'objectDetails.attendees': {'$in':[?0] }, 'objectDetails.date': {'$gt': ?2 } }")
    public List<SuperAppObjectEntity> findAllByTypeAndMyEvents(String userEmail, String type, long now, PageRequest creationTimestamp);

    // search by event name
    @Query("{'type': ?0, 'objectDetails.date': {'$gt': ?1 }, 'objectDetails.name': {'$regex': ?2 }}")
    public List<SuperAppObjectEntity> searchEventByName(String type, long now, String name, Pageable pageable);
    // search by event date

    @Query("{'type': ?0, 'objectDetails.date': {'$gt': ?1 } ,'objectDetails.date': {'$gt': ?2, '$lt': ?3 }}")
    public List<SuperAppObjectEntity> searchEventByDates(String type, long now, long startDate, long endDate, Pageable pageable);

    // search by event contains preferences
    @Query("{'type': ?0, 'objectDetails.date': {'$gt': ?1 }, 'objectDetails.preferences': {'$in': [?2] }}")
    public List<SuperAppObjectEntity> searchEventByPreferences(String type, long now, String preference, Pageable pageable); //p    reference must be exact string

    // search by event location
    // todo : fix this query
    @Query("{'type': ?0, 'objectDetails.date': {'$gt': ?1 }, " +
            "'objectDetails.location': {'$near': {'$geometry': {'type': 'Point', 'coordinates': [?2, ?3]}, '$maxDistance': ?4}}}")
    public List<SuperAppObjectEntity> searchEventByLocation(String type, long now, Double lat, Double lng, Double distance, Pageable pageable);

    public SuperAppObjectEntity findByCreatedByAndType(@Param("createdBy") String createdBy, @Param("type") String type);

    // add attendee to event
    @Query("{ 'objectId' : ?0, 'objectDetails.attendees': {'$not':{'$in':[?1] }}}")
    @Update("{ $push: { 'objectDetails.attendees': ?1 } }")
    void addAttendeeToEvent(String eventObjectId, String userEmail);

    // remove attendee from event
    @Query("{ 'objectId' : ?0, 'objectDetails.attendees': {'$in':[?1] }}")
    @Update("{ $pull: { 'objectDetails.attendees': ?1 } }")
    void removeAttendeeFromEvent(String eventObjectId, String userEmail);

    @Query("{'createdBy': ?0,'type' :?1, 'objectDetails.date': {'$gt': ?2 } }")
    public List<SuperAppObjectEntity> findAllByEventsCreatedByMe(String owner, String type, long now, Pageable pageable);

    @Query("{'type' :?0, 'objectDetails.date': {'$gt': ?1 } }")
    public List<SuperAppObjectEntity> findAllEventsInTheFuture(String type, long now, Pageable pageable);

    @Query("{'type' :?0, 'objectDetails.category': ?1}")
    public List<SuperAppObjectEntity> findAllProductsByCategory(String type, String category, Pageable pageable);

    @Query("{ 'type': ?0, 'objectDetails.price': { $gt: ?1, $lt: ?2 } }")
    public List<SuperAppObjectEntity> findAllProductsByPrice(String type, double maxPrice, double minPrice,  Pageable pageable);

    @Query("{'createdBy': ?0,'type' :?1 }")
    public List<SuperAppObjectEntity> findAllProductsCreatedBySupplier(String businessName, String type, Pageable pageable);

    @Query("{'type' :?0, 'objectDetails.currency': ?1}")
    public List<SuperAppObjectEntity> findAllProductsByCurrency(String type, String currency, Pageable pageable);

    @Query("{'type' :?0, 'objectDetails.name': ?1 }")
    public List<SuperAppObjectEntity> findAllProductsByName(String type, String name,  Pageable pageable);

    @Query("{ 'type' : ?0, 'objectDetails.preferences' : { $in: ?1 } }")
    public List<SuperAppObjectEntity> findAllProductsByPreferences(String type, List<String> preferences, Pageable pageable);
}
