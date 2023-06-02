package superapp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import superapp.data.UserRole;
import superapp.logic.boundaries.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class EventsTestSet extends BaseTestSet {

    // get all future events
    @Test
    @DisplayName("get all future events")
    void getAllFutureEvents() {
        // USER 1
        String email1 = "user1@gmail.com";
        String role1 = UserRole.SUPERAPP_USER.toString();
        String username1 = "user1";
        String avatar1 = "user1.png";
        // USER 2
        String email2 = "user2@gmail.com";
        String role2 = UserRole.SUPERAPP_USER.toString();
        String username2 = "user2";
        String avatar2 = "user2.png";

        // event 1 in the future
        String type1 = "EVENT";
        String alias1 = "EVENT";
        Location location1 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy1 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event1Attributes = new HashMap<>();
        event1Attributes.put("name", "event1");
        event1Attributes.put("description", "event1 description");
        event1Attributes.put("location", "demo location");
        // date in current time in millis + 1 hour
        event1Attributes.put("date", new Date().getTime() + 3600000);
        event1Attributes.put("preferences", new String[]{"music", "sport"});
        event1Attributes.put("attendees", new String[]{email1, email2});

        // event 2 in the past
        String type2 = "EVENT";
        String alias2 = "EVENT";
        Location location2 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy2 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event2Attributes = new HashMap<>();
        event2Attributes.put("name", "event2");
        event2Attributes.put("description", "event2 description");
        event2Attributes.put("location", "demo location");
        // date in current time in millis - 1 hour
        event2Attributes.put("date", new Date().getTime() - 3600000);
        event2Attributes.put("preferences", new String[]{"music", "sport"});
        event2Attributes.put("attendees", new String[]{email1, email2});


        // event 3 in the future
        String type3 = "EVENT";
        String alias3 = "EVENT";
        Location location3 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy3 = new CreatedBy().setUserId(new UserId().setEmail(email2).setSuperapp(springApplicationName));
        Map<String, Object> event3Attributes = new HashMap<>();
        event3Attributes.put("name", "event3");
        event3Attributes.put("description", "event3 description");
        event3Attributes.put("location", "demo location");
        // date in current time in millis + 1 hour
        event3Attributes.put("date", new Date().getTime() + 3600000);
        event3Attributes.put("preferences", new String[]{"music", "sport"});
        event3Attributes.put("attendees", new String[]{email1, email2});

        // demo target object
        String type4 = "TARGET_OBJECT";
        String alias4 = "TARGET_OBJECT";
        Location location4 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy4 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> targetObjectAttributes = new HashMap<>();
        targetObjectAttributes.put("name", "target object");

        // ###################### Gherkin ######################
        // GIVEN
        // 1. SUPERAPP_USER 1,2 are in the system
        help_PostUserBoundary(email1, role1, username1, avatar1);
        help_PostUserBoundary(email2, role2, username2, avatar2);
        // 2. SUPERAPP_USER 1 has created 2 events. one in the past and one in the future
        help_PostObjectBoundary(new ObjectId(), type1, alias1, new Date(), true, location1, createdBy1, event1Attributes);
        help_PostObjectBoundary(new ObjectId(), type2, alias2, new Date(), true, location2, createdBy2, event2Attributes);
        // 3. SUPERAPP_USER 2 has created 1 event in the future
        help_PostObjectBoundary(new ObjectId(), type3, alias3, new Date(), true, location3, createdBy3, event3Attributes);
        String targetObjectId = help_PostObjectBoundary(new ObjectId(), type4, alias4, new Date(), true, location4, createdBy4, targetObjectAttributes).getObjectId().getInternalObjectId();
        // 4. Demo Target Object is created


        // WHEN
        // user 1 role is changed to MINIAPP_USER
        UserBoundary userBoundary = help_GetUserBoundary(email1);
        userBoundary.setRole(UserRole.MINIAPP_USER.toString());
        help_PutUserBoundary(userBoundary, email1);

        // SUPERAPP_USER 1 requests all future events
        String miniappName = "EVENT";
        CommandId commandId = new CommandId().setSuperapp(springApplicationName).setMiniapp(miniappName);
        String command = "GET_ALL_FUTURE_EVENTS";
        TargetObject targetObject = new TargetObject().setObjectId(new ObjectId().setInternalObjectId(targetObjectId).setSuperapp(springApplicationName));
        Date date = new Date();
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> params = new HashMap<>();
        Object object = help_PostCommandBoundary(miniappName, commandId, command, targetObject, date, invokedBy, params);

        List<SuperAppObjectBoundary> objectBoundaries = objectToListOfObjectBoundaries(object);

        // THEN
        // SUPERAPP_USER 1 gets 2 event
        assertEquals(2, objectBoundaries.size());
        // objectDetails.date > current time
        for (SuperAppObjectBoundary objectBoundary : objectBoundaries) {
            assertTrue(Long.parseLong(objectBoundary.getObjectDetails().get("date").toString()) > new Date().getTime());
        }
    }


    // get all events created by user
    @Test
    @DisplayName("test get all future events created by user")
    void getAllEventsCreatedByUser() {
        // USER 1
        String email1 = "user1@gmail.com";
        String role1 = UserRole.SUPERAPP_USER.toString();
        String username1 = "user1";
        String avatar1 = "user1.png";
        // USER 2
        String email2 = "user2@gmail.com";
        String role2 = UserRole.SUPERAPP_USER.toString();
        String username2 = "user2";
        String avatar2 = "user2.png";

        // event 1 in the future
        String type1 = "EVENT";
        String alias1 = "EVENT";
        Location location1 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy1 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event1Attributes = new HashMap<>();
        event1Attributes.put("name", "event1");
        event1Attributes.put("description", "event1 description");
        event1Attributes.put("location", "demo location");
        // date in current time in millis + 1 hour
        event1Attributes.put("date", new Date().getTime() + 3600000);
        event1Attributes.put("preferences", new String[]{"music", "sport"});
        event1Attributes.put("attendees", new String[]{email1, email2});

        // event 2 in the past
        String type2 = "EVENT";
        String alias2 = "EVENT";
        Location location2 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy2 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event2Attributes = new HashMap<>();
        event2Attributes.put("name", "event2");
        event2Attributes.put("description", "event2 description");
        event2Attributes.put("location", "demo location");
        // date in current time in millis - 1 hour
        event2Attributes.put("date", new Date().getTime() - 3600000);
        event2Attributes.put("preferences", new String[]{"music", "sport"});
        event2Attributes.put("attendees", new String[]{email1, email2});


        // event 3 in the future
        String type3 = "EVENT";
        String alias3 = "EVENT";
        Location location3 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy3 = new CreatedBy().setUserId(new UserId().setEmail(email2).setSuperapp(springApplicationName));
        Map<String, Object> event3Attributes = new HashMap<>();
        event3Attributes.put("name", "event3");
        event3Attributes.put("description", "event3 description");
        event3Attributes.put("location", "demo location");
        // date in current time in millis + 1 hour
        event3Attributes.put("date", new Date().getTime() + 3600000);
        event3Attributes.put("preferences", new String[]{"music", "sport"});
        event3Attributes.put("attendees", new String[]{email1, email2});

        // demo target object
        String type4 = "TARGET_OBJECT";
        String alias4 = "TARGET_OBJECT";
        Location location4 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy4 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> targetObjectAttributes = new HashMap<>();
        targetObjectAttributes.put("name", "target object");


        // GIVEN
        // 1. SUPERAPP_USER 1,2 are in the system
        help_PostUserBoundary(email1, role1, username1, avatar1);
        help_PostUserBoundary(email2, role2, username2, avatar2);
        // 2. SUPERAPP_USER 1 has created 2 events. one in the past and one in the future
        help_PostObjectBoundary(new ObjectId(), type1, alias1, new Date(), true, location1, createdBy1, event1Attributes);
        help_PostObjectBoundary(new ObjectId(), type2, alias2, new Date(), true, location2, createdBy2, event2Attributes);
        // 3. SUPERAPP_USER 2 has created 1 event in the future
        help_PostObjectBoundary(new ObjectId(), type3, alias3, new Date(), true, location3, createdBy3, event3Attributes);
        // 4. Demo Target Object is created
        String targetObjectId = help_PostObjectBoundary(new ObjectId(), type4, alias4, new Date(), true, location4, createdBy4, targetObjectAttributes).getObjectId().getInternalObjectId();


        // WHEN
        // user 1 role is changed to MINIAPP_USER
        UserBoundary userBoundary = help_GetUserBoundary(email1);
        userBoundary.setRole(UserRole.MINIAPP_USER.toString());
        help_PutUserBoundary(userBoundary, email1);

        // SUPERAPP_USER 1 requests all future events created by him
        String miniappName = "EVENT";
        CommandId commandId = new CommandId().setSuperapp(springApplicationName).setMiniapp(miniappName);
        String command = "GET_EVENTS_CREATED_BY_ME";
        TargetObject targetObject = new TargetObject().setObjectId(new ObjectId().setInternalObjectId(targetObjectId).setSuperapp(springApplicationName));
        Date date = new Date();
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> params = new HashMap<>();
        Object object = help_PostCommandBoundary(miniappName, commandId, command, targetObject, date, invokedBy, params);

        // THEN
        // 1. SUPERAPP_USER 1 gets 1 event
        List<SuperAppObjectBoundary> objectBoundaries = objectToListOfObjectBoundaries(object);
        assertEquals(1, objectBoundaries.size());
        for (SuperAppObjectBoundary objectBoundary : objectBoundaries) {
            // check that the event is in the future
            assertTrue(Long.parseLong(objectBoundary.getObjectDetails().get("date").toString()) > new Date().getTime());
            // check that the event is created by SUPERAPP_USER 1
            assertEquals(email1, objectBoundary.getCreatedBy().getUserId().getEmail());

        }
    }

    // get all events based on preferences // todo
    @Test
    @DisplayName("Test get all events based on user preferences")
    void getAllEventsBasedOnPreferences() {
        // USER 1
        String email1 = "user1@gmail.com";
        String role1 = UserRole.SUPERAPP_USER.toString();
        String username1 = "user1";
        String avatar1 = "user1.png";        // USER 1

        // USER 2
        String email2 = "user2@gmail.com";
        String role2 = UserRole.SUPERAPP_USER.toString();
        String username2 = "user2";
        String avatar2 = "user2.png";


        // create user Details Object Boundary
        String type4 = "USER_DETAILS";
        String alias4 = "USER_DETAILS";
        Location location4 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy4 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> targetObjectAttributes = new HashMap<>();
        targetObjectAttributes.put("name", "user 1");
        targetObjectAttributes.put("phoneNum", "+9721234567");
        targetObjectAttributes.put("preferences", new String[]{"music", "sport"});

        // create user Details Object Boundary
        String type5 = "USER_DETAILS";
        String alias5 = "USER_DETAILS";
        Location location5 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy5 = new CreatedBy().setUserId(new UserId().setEmail(email2).setSuperapp(springApplicationName));
        Map<String, Object> targetObjectAttributes5 = new HashMap<>();
        targetObjectAttributes5.put("name", "user 2");
        targetObjectAttributes5.put("phoneNum", "+9721234567");
        targetObjectAttributes5.put("preferences", new String[]{"music", "sport"});


        // event 1 in the future
        String type1 = "EVENT";
        String alias1 = "EVENT";
        Location location1 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy1 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event1Attributes = new HashMap<>();
        event1Attributes.put("name", ",music event");
        event1Attributes.put("description", "event1 description");
        event1Attributes.put("location", "demo location");
        // date in current time in millis + 1 hour
        event1Attributes.put("date", new Date().getTime() + 3600000);
        event1Attributes.put("preferences", new String[]{"music", "ski"});
        event1Attributes.put("attendees", new String[]{});

        // event 2 in the future
        String type2 = "EVENT";
        String alias2 = "EVENT";
        Location location2 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy2 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event2Attributes = new HashMap<>();
        event2Attributes.put("name", "sport event");
        event2Attributes.put("description", "event2 description");
        event2Attributes.put("location", "demo location");
        // date in current time in millis + 2 hour
        event2Attributes.put("date", new Date().getTime() + 7200000);
        event2Attributes.put("preferences", new String[]{"sport"});
        event2Attributes.put("attendees", new String[]{});

        // event 3 in the future
        String type3 = "EVENT";
        String alias3 = "EVENT";
        Location location3 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy3 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event3Attributes = new HashMap<>();
        event3Attributes.put("name", "tennis event");
        event3Attributes.put("description", "event3 description");
        event3Attributes.put("location", "demo location");
        // date in current time in millis + 3 hour
        event3Attributes.put("date", new Date().getTime() + 10800000);
        event3Attributes.put("preferences", new String[]{"tennis"});
        event3Attributes.put("attendees", new String[]{});


        // ###################### Gherkin ######################
        // GIVEN
        // 1. SUPERAPP_USER 1 are in the system
        help_PostUserBoundary(email1, role1, username1, avatar1);
        help_PostUserBoundary(email2, role2, username2, avatar2);
        // 2. SUPERAPP_USER 1 has created 3 events
        help_PostObjectBoundary(new ObjectId(), type1, alias1, new Date(), true, location1, createdBy1, event1Attributes);
        help_PostObjectBoundary(new ObjectId(), type2, alias2, new Date(), true, location2, createdBy2, event2Attributes);
        help_PostObjectBoundary(new ObjectId(), type3, alias3, new Date(), true, location3, createdBy3, event3Attributes);
        // 4. user details object
        String targetObjectId = help_PostObjectBoundary(new ObjectId(), type4, alias4, new Date(), true, location4, createdBy4, targetObjectAttributes).getObjectId().getInternalObjectId();
        String targetObjectId5 = help_PostObjectBoundary(new ObjectId(), type5, alias5, new Date(), true, location5, createdBy5, targetObjectAttributes5).getObjectId().getInternalObjectId();

        // WHEN
        // 1. SUPERAPP_USER 1 gets all events based on preferences
        UserBoundary userBoundary = help_GetUserBoundary(email2);
        userBoundary.setRole(UserRole.MINIAPP_USER.toString());
        help_PutUserBoundary(userBoundary, email2);
        // user 1 search for event by category
        String miniappName = "EVENT";
        CommandId commandId = new CommandId().setSuperapp(springApplicationName).setMiniapp(miniappName);
        String command = "GET_EVENTS_BASED_ON_PREFERENCES";
        TargetObject targetObject = new TargetObject().setObjectId(new ObjectId().setInternalObjectId(targetObjectId5).setSuperapp(springApplicationName));
        Date date = new Date();
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setEmail(email2).setSuperapp(springApplicationName));
        Map<String, Object> params = new HashMap<>();
        Object object = help_PostCommandBoundary(miniappName, commandId, command, targetObject, date, invokedBy, params);

        // THEN
        // 1. SUPERAPP_USER 1 gets all events based on preferences
        // 1.1. SUPERAPP_USER 1 gets 2 events
        List<SuperAppObjectBoundary> objectBoundaries = objectToListOfObjectBoundaries(object);
        System.err.println(objectBoundaries);
        assertEquals(2, objectBoundaries.size());


    }

    // get all my events
    @Test
    @DisplayName("Test get all the events that the user is attending")
    void getAllMyEvents() {
        // USER 1
        String email1 = "user1@gmail.com";
        String role1 = UserRole.SUPERAPP_USER.toString();
        String username1 = "user1";
        String avatar1 = "user1.png";

        // event 1 in the future
        String type1 = "EVENT";
        String alias1 = "EVENT";
        Location location1 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy1 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event1Attributes = new HashMap<>();
        event1Attributes.put("name", "event1");
        event1Attributes.put("description", "event1 description");
        event1Attributes.put("location", "demo location");
        // date in current time in millis + 1 hour
        event1Attributes.put("date", new Date().getTime() + 3600000);
        event1Attributes.put("preferences", new String[]{"music", "sport"});
        event1Attributes.put("attendees", new String[]{email1});

        // event 2 in the past
        String type2 = "EVENT";
        String alias2 = "EVENT";
        Location location2 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy2 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event2Attributes = new HashMap<>();
        event2Attributes.put("name", "event2");
        event2Attributes.put("description", "event2 description");
        event2Attributes.put("location", "demo location");
        // date in current time in millis - 1 hour
        event2Attributes.put("date", new Date().getTime() - 3600000);
        event2Attributes.put("preferences", new String[]{"music", "sport"});
        event2Attributes.put("attendees", new String[]{email1});


        // event 3 in the future
        String type3 = "EVENT";
        String alias3 = "EVENT";
        Location location3 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy3 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event3Attributes = new HashMap<>();
        event3Attributes.put("name", "event3");
        event3Attributes.put("description", "event3 description");
        event3Attributes.put("location", "demo location");
        // date in current time in millis + 1 hour
        event3Attributes.put("date", new Date().getTime() + 3600000);
        event3Attributes.put("preferences", new String[]{"music", "sport"});
        event3Attributes.put("attendees", new String[]{});

        // demo target object
        String type4 = "TARGET_OBJECT";
        String alias4 = "TARGET_OBJECT";
        Location location4 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy4 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> targetObjectAttributes = new HashMap<>();
        targetObjectAttributes.put("name", "target object");


        // GIVEN
        // 1. USER 1 is registered to the system
        help_PostUserBoundary(email1, role1, username1, avatar1);
        // 2. USER 1 creates 3 events
        // event 1 in the future
        help_PostObjectBoundary(new ObjectId(), type1, alias1, new Date(), true, location1, createdBy1, event1Attributes);
        // event 2 in the past
        help_PostObjectBoundary(new ObjectId(), type2, alias2, new Date(), true, location2, createdBy2, event2Attributes);
        // event 3 in the future and no attendees
        help_PostObjectBoundary(new ObjectId(), type3, alias3, new Date(), true, location3, createdBy3, event3Attributes);
        // 3. USER 1 creates a target object
        String targetObjectId = help_PostObjectBoundary(new ObjectId(), type4, alias4, new Date(), true, location4, createdBy4, targetObjectAttributes).getObjectId().getInternalObjectId();


        // WHEN
        // user 1 role is changed to MINIAPP_USER
        UserBoundary userBoundary = help_GetUserBoundary(email1);
        userBoundary.setRole(UserRole.MINIAPP_USER.toString());
        help_PutUserBoundary(userBoundary, email1);

        // 1. USER 1 gets all the events that he is attending
        String miniappName = "EVENT";
        CommandId commandId = new CommandId().setSuperapp(springApplicationName).setMiniapp(miniappName);
        String command = "GET_MY_EVENTS";
        TargetObject targetObject = new TargetObject().setObjectId(new ObjectId().setInternalObjectId(targetObjectId).setSuperapp(springApplicationName));
        Date date = new Date();
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> params = new HashMap<>();
        Object object = help_PostCommandBoundary(miniappName, commandId, command, targetObject, date, invokedBy, params);

        // THEN
        // 1. USER 1 gets all the events that he is attending
        List<SuperAppObjectBoundary> objectBoundaries = objectToListOfObjectBoundaries(object);
        assertEquals(1, objectBoundaries.size());

        // 2. USER 1 gets all the events that he is attending and the event is in the future
        for (SuperAppObjectBoundary objectBoundary : objectBoundaries) {
            // check that the event is in the future
            assertTrue(Long.parseLong(objectBoundary.getObjectDetails().get("date").toString()) > new Date().getTime());
            // check that the event has attendees

            ObjectMapper mapper = new ObjectMapper();
            List<String> list = mapper.convertValue(objectBoundary.getObjectDetails().get("attendees"), List.class);
            assertTrue(list.contains(email1));
        }
    }

    // join event
    @Test
    @DisplayName("Test join event")
    void joinTest() {
        // USER 1
        String email1 = "user1@gmail.com";
        String role1 = UserRole.SUPERAPP_USER.toString();
        String username1 = "user1";
        String avatar1 = "user1.png";

        // event 1 in the future
        String type1 = "EVENT";
        String alias1 = "EVENT";
        Location location1 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy1 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event1Attributes = new HashMap<>();
        event1Attributes.put("name", "event1");
        event1Attributes.put("description", "event1 description");
        event1Attributes.put("location", "demo location");
        // date in current time in millis + 1 hour
        event1Attributes.put("date", new Date().getTime() + 3600000);
        event1Attributes.put("preferences", new String[]{"music", "sport"});
        event1Attributes.put("attendees", new String[]{});

        // GIVEN
        // 1. USER 1 is registered to the system
        help_PostUserBoundary(email1, role1, username1, avatar1);
        // 2. USER 1 creates 1 event in the future with no attendees
        String internalObjectId = help_PostObjectBoundary(new ObjectId(), type1, alias1, new Date(), true, location1, createdBy1, event1Attributes).getObjectId().getInternalObjectId();

        // WHEN
        // user 1 role is changed to MINIAPP_USER
        UserBoundary userBoundary = help_GetUserBoundary(email1);
        userBoundary.setRole(UserRole.MINIAPP_USER.toString());
        help_PutUserBoundary(userBoundary, email1);
        // 1. USER 1 joins the event
        String miniappName = "EVENT";
        CommandId commandId = new CommandId().setSuperapp(springApplicationName).setMiniapp(miniappName);
        String command = "JOIN_EVENT";
        TargetObject targetObject = new TargetObject().setObjectId(new ObjectId().setInternalObjectId(internalObjectId).setSuperapp(springApplicationName));
        Date date = new Date();
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> params = new HashMap<>();
        help_PostCommandBoundary(miniappName, commandId, command, targetObject, date, invokedBy, params);


        // THEN
        // 1. USER 1 is added to the attendees list
        SuperAppObjectBoundary objectBoundary = help_GetObjectBoundary(internalObjectId, springApplicationName, springApplicationName, email1);
        ObjectMapper mapper = new ObjectMapper();
        List<String> list = mapper.convertValue(objectBoundary.getObjectDetails().get("attendees"), List.class);
        assertTrue(list.contains(email1));
    }

    @Test
    @DisplayName("Test join event that i am already attending")
    void joinTest2() {
        // USER 1
        String email1 = "user1@gmail.com";
        String role1 = UserRole.SUPERAPP_USER.toString();
        String username1 = "user1";
        String avatar1 = "user1.png";

        // event 1 in the future
        String type1 = "EVENT";
        String alias1 = "EVENT";
        Location location1 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy1 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event1Attributes = new HashMap<>();
        event1Attributes.put("name", "event1");
        event1Attributes.put("description", "event1 description");
        event1Attributes.put("location", "demo location");
        // date in current time in millis + 1 hour
        event1Attributes.put("date", new Date().getTime() + 3600000);
        event1Attributes.put("preferences", new String[]{"music", "sport"});
        event1Attributes.put("attendees", new String[]{email1});

        // GIVEN
        // 1. USER 1 is registered to the system
        help_PostUserBoundary(email1, role1, username1, avatar1);
        // 2. USER 1 creates 1 event in the future with no attendees
        String internalObjectId = help_PostObjectBoundary(new ObjectId(), type1, alias1, new Date(), true, location1, createdBy1, event1Attributes).getObjectId().getInternalObjectId();

        // WHEN
        // user 1 role is changed to MINIAPP_USER
        UserBoundary userBoundary = help_GetUserBoundary(email1);
        userBoundary.setRole(UserRole.MINIAPP_USER.toString());
        help_PutUserBoundary(userBoundary, email1);
        // 1. USER 1 joins the event
        String miniappName = "EVENT";
        CommandId commandId = new CommandId().setSuperapp(springApplicationName).setMiniapp(miniappName);
        String command = "JOIN_EVENT";
        TargetObject targetObject = new TargetObject().setObjectId(new ObjectId().setInternalObjectId(internalObjectId).setSuperapp(springApplicationName));
        Date date = new Date();
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> params = new HashMap<>();
        help_PostCommandBoundary(miniappName, commandId, command, targetObject, date, invokedBy, params);


        // THEN
        // 1. USER 1 is not added to the attendees list
        SuperAppObjectBoundary objectBoundary = help_GetObjectBoundary(internalObjectId, springApplicationName, springApplicationName, email1);
        ObjectMapper mapper = new ObjectMapper();
        List<String> list = mapper.convertValue(objectBoundary.getObjectDetails().get("attendees"), List.class);
        assertTrue(list.contains(email1));
        assertEquals(1, list.size());

    }

    // leave event
    @Test
    @DisplayName("Test leave event")
    void leaveTest() {
        // USER 1
        String email1 = "user1@gmail.com";
        String role1 = UserRole.SUPERAPP_USER.toString();
        String username1 = "user1";
        String avatar1 = "user1.png";

        // event 1 in the future
        String type1 = "EVENT";
        String alias1 = "EVENT";
        Location location1 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy1 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event1Attributes = new HashMap<>();
        event1Attributes.put("name", "event1");
        event1Attributes.put("description", "event1 description");
        event1Attributes.put("location", "demo location");
        // date in current time in millis + 1 hour
        event1Attributes.put("date", new Date().getTime() + 3600000);
        event1Attributes.put("preferences", new String[]{"music", "sport"});
        event1Attributes.put("attendees", new String[]{email1});

        // GIVEN
        // 1. USER 1 is registered to the system
        help_PostUserBoundary(email1, role1, username1, avatar1);
        // 2. USER 1 creates 1 event in the future with no attendees
        String internalObjectId = help_PostObjectBoundary(new ObjectId(), type1, alias1, new Date(), true, location1, createdBy1, event1Attributes).getObjectId().getInternalObjectId();

        // WHEN
        // user 1 role is changed to MINIAPP_USER
        UserBoundary userBoundary = help_GetUserBoundary(email1);
        userBoundary.setRole(UserRole.MINIAPP_USER.toString());
        help_PutUserBoundary(userBoundary, email1);
        // 1. USER 1 joins the event
        String miniappName = "EVENT";
        CommandId commandId = new CommandId().setSuperapp(springApplicationName).setMiniapp(miniappName);
        String command = "LEAVE_EVENT";
        TargetObject targetObject = new TargetObject().setObjectId(new ObjectId().setInternalObjectId(internalObjectId).setSuperapp(springApplicationName));
        Date date = new Date();
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> params = new HashMap<>();
        help_PostCommandBoundary(miniappName, commandId, command, targetObject, date, invokedBy, params);


        // THEN
        // 1. USER 1 is removed from to the attendees list
        SuperAppObjectBoundary objectBoundary = help_GetObjectBoundary(internalObjectId, springApplicationName, springApplicationName, email1);
        ObjectMapper mapper = new ObjectMapper();
        List<String> list = mapper.convertValue(objectBoundary.getObjectDetails().get("attendees"), List.class);
        assertFalse(list.contains(email1));
    }

    // create event
    @Test
    @DisplayName("Test create event")
    void createTest() {
        // USER 1
        String email1 = "user1@gmail.com";
        String role1 = UserRole.SUPERAPP_USER.toString();
        String username1 = "user1";
        String avatar1 = "user1.png";

        //GIVEN
        // 1. USER 1 is registered to the system
        help_PostUserBoundary(email1, role1, username1, avatar1);

        // WHEN
        // event 1 in the future
        String type1 = "EVENT";
        String alias1 = "EVENT";
        Location location1 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy1 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event1Attributes = new HashMap<>();
        event1Attributes.put("name", "event1");
        event1Attributes.put("description", "event1 description");
        event1Attributes.put("location", "demo location");
        // date in current time in millis + 1 hour
        event1Attributes.put("date", new Date().getTime() + 3600000);
        event1Attributes.put("preferences", new String[]{"music", "sport"});
        event1Attributes.put("attendees", new String[]{email1});
        SuperAppObjectBoundary superAppObjectBoundary = help_PostObjectBoundary(new ObjectId(), type1, alias1, new Date(), true, location1, createdBy1, event1Attributes);


        // THEN
        // 1. the event is created
        assertThat(superAppObjectBoundary)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(help_GetObjectBoundary(superAppObjectBoundary.getObjectId().getInternalObjectId(), springApplicationName, springApplicationName, email1));

    }

    // search event by name
    @Test
    @DisplayName("Test search event by name = ski")
    void searchByNameTest() {
        // USER 1
        String email1 = "user1@gmail.com";
        String role1 = UserRole.SUPERAPP_USER.toString();
        String username1 = "user1";
        String avatar1 = "user1.png";

        // event 1 in the future
        String type1 = "EVENT";
        String alias1 = "EVENT";
        Location location1 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy1 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event1Attributes = new HashMap<>();
        event1Attributes.put("name", "ski event");
        event1Attributes.put("description", "event1 description");
        event1Attributes.put("location", "demo location");
        // date in current time in millis + 1 hour
        event1Attributes.put("date", new Date().getTime() + 3600000);
        event1Attributes.put("preferences", new String[]{"music", "sport"});
        event1Attributes.put("attendees", new String[]{});

        // event 2 in the past
        String type2 = "EVENT";
        String alias2 = "EVENT";
        Location location2 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy2 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event2Attributes = new HashMap<>();
        event2Attributes.put("name", "tennis event");
        event2Attributes.put("description", "event2 description");
        event2Attributes.put("location", "demo location");
        // date in current time in millis - 1 hour
        event2Attributes.put("date", new Date().getTime() - 3600000);
        event2Attributes.put("preferences", new String[]{"music", "sport"});
        event2Attributes.put("attendees", new String[]{});


        // demo target object
        String type4 = "TARGET_OBJECT";
        String alias4 = "TARGET_OBJECT";
        Location location4 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy4 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> targetObjectAttributes = new HashMap<>();
        targetObjectAttributes.put("name", "target object");

        // GIVEN
        // 1. USER 1 is registered to the system
        help_PostUserBoundary(email1, role1, username1, avatar1);
        // 2. USER 1 creates  2 event
        help_PostObjectBoundary(new ObjectId(), type1, alias1, new Date(), true, location1, createdBy1, event1Attributes);
        help_PostObjectBoundary(new ObjectId(), type2, alias2, new Date(), true, location2, createdBy2, event2Attributes);
        // 3. target object is created
        String targetObjectId = help_PostObjectBoundary(new ObjectId(), type4, alias4, new Date(), true, location4, createdBy4, targetObjectAttributes).getObjectId().getInternalObjectId();

        // WHEN
        // user 1 role is changed to MINIAPP_USER
        UserBoundary userBoundary = help_GetUserBoundary(email1);
        userBoundary.setRole(UserRole.MINIAPP_USER.toString());
        help_PutUserBoundary(userBoundary, email1);

        // user 1 search for event by name
        String searchName = "ski";
        String miniappName = "EVENT";
        CommandId commandId = new CommandId().setSuperapp(springApplicationName).setMiniapp(miniappName);
        String command = "SEARCH_EVENTS_BY_NAME";
        TargetObject targetObject = new TargetObject().setObjectId(new ObjectId().setInternalObjectId(targetObjectId).setSuperapp(springApplicationName));
        Date date = new Date();
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> params = new HashMap<>();
        params.put("name", searchName);
        Object object = help_PostCommandBoundary(miniappName, commandId, command, targetObject, date, invokedBy, params);

        // THEN
        List<SuperAppObjectBoundary> objectBoundaries = objectToListOfObjectBoundaries(object);
        assertThat(objectBoundaries)
                .isNotNull()
                .hasSize(1);

        for (SuperAppObjectBoundary objectBoundary : objectBoundaries) {
            assertTrue(objectBoundary.getObjectDetails().get("name").toString().contains(searchName));
        }
    }

    @Test
    @DisplayName("Test search event by name = event")
    void searchByNameTest2() {
        // USER 1
        String email1 = "user1@gmail.com";
        String role1 = UserRole.SUPERAPP_USER.toString();
        String username1 = "user1";
        String avatar1 = "user1.png";

        // event 1 in the future
        String type1 = "EVENT";
        String alias1 = "EVENT";
        Location location1 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy1 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event1Attributes = new HashMap<>();
        event1Attributes.put("name", "ski event");
        event1Attributes.put("description", "event1 description");
        event1Attributes.put("location", "demo location");
        // date in current time in millis + 1 hour
        event1Attributes.put("date", new Date().getTime() + 3600000);
        event1Attributes.put("preferences", new String[]{"music", "sport"});
        event1Attributes.put("attendees", new String[]{});

        // event 2 in the past
        String type2 = "EVENT";
        String alias2 = "EVENT";
        Location location2 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy2 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event2Attributes = new HashMap<>();
        event2Attributes.put("name", "tennis event");
        event2Attributes.put("description", "event2 description");
        event2Attributes.put("location", "demo location");
        // date in current time in millis - 1 hour
        event2Attributes.put("date", new Date().getTime() + 3600000);
        event2Attributes.put("preferences", new String[]{"music", "sport"});
        event2Attributes.put("attendees", new String[]{});


        // demo target object
        String type4 = "TARGET_OBJECT";
        String alias4 = "TARGET_OBJECT";
        Location location4 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy4 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> targetObjectAttributes = new HashMap<>();
        targetObjectAttributes.put("name", "target object");

        // GIVEN
        // 1. USER 1 is registered to the system
        help_PostUserBoundary(email1, role1, username1, avatar1);
        // 2. USER 1 creates  2 event
        help_PostObjectBoundary(new ObjectId(), type1, alias1, new Date(), true, location1, createdBy1, event1Attributes);
        help_PostObjectBoundary(new ObjectId(), type2, alias2, new Date(), true, location2, createdBy2, event2Attributes);
        // 3. target object is created
        String targetObjectId = help_PostObjectBoundary(new ObjectId(), type4, alias4, new Date(), true, location4, createdBy4, targetObjectAttributes).getObjectId().getInternalObjectId();

        // WHEN
        // user 1 role is changed to MINIAPP_USER
        UserBoundary userBoundary = help_GetUserBoundary(email1);
        userBoundary.setRole(UserRole.MINIAPP_USER.toString());
        help_PutUserBoundary(userBoundary, email1);

        // user 1 search for event by name
        String searchName = "event";
        //
        String miniappName = "EVENT";
        CommandId commandId = new CommandId().setSuperapp(springApplicationName).setMiniapp(miniappName);
        String command = "SEARCH_EVENTS_BY_NAME";
        TargetObject targetObject = new TargetObject().setObjectId(new ObjectId().setInternalObjectId(targetObjectId).setSuperapp(springApplicationName));
        Date date = new Date();
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> params = new HashMap<>();
        params.put("name", searchName);
        Object object = help_PostCommandBoundary(miniappName, commandId, command, targetObject, date, invokedBy, params);

        // THEN
        List<SuperAppObjectBoundary> objectBoundaries = objectToListOfObjectBoundaries(object);

        assertThat(objectBoundaries)
                .isNotNull()
                .hasSize(2);

        for (SuperAppObjectBoundary objectBoundary : objectBoundaries) {
            assertTrue(objectBoundary.getObjectDetails().get("name").toString().contains(searchName));
        }
    }

    // search event by date
    @Test
    @DisplayName("Test search event by date")
    void searchByDateTest() {
        // USER 1
        String email1 = "user1@gmail.com";
        String role1 = UserRole.SUPERAPP_USER.toString();
        String username1 = "user1";
        String avatar1 = "user1.png";

        // event 1 in the future
        String type1 = "EVENT";
        String alias1 = "EVENT";
        Location location1 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy1 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event1Attributes = new HashMap<>();
        event1Attributes.put("name", "ski event");
        event1Attributes.put("description", "event1 description");
        event1Attributes.put("location", "demo location");
        // date in current time in millis + 1 hour
        event1Attributes.put("date", new Date().getTime() + 3600000);
        event1Attributes.put("preferences", new String[]{"music", "sport"});
        event1Attributes.put("attendees", new String[]{});

        // event 2 in the future
        String type2 = "EVENT";
        String alias2 = "EVENT";
        Location location2 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy2 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event2Attributes = new HashMap<>();
        event2Attributes.put("name", "tennis event");
        event2Attributes.put("description", "event2 description");
        event2Attributes.put("location", "demo location");
        // date in current time in millis + 2 hour
        event2Attributes.put("date", new Date().getTime() + 7200000);
        event2Attributes.put("preferences", new String[]{"music", "sport"});
        event2Attributes.put("attendees", new String[]{});

        // event 3 in the future
        String type3 = "EVENT";
        String alias3 = "EVENT";
        Location location3 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy3 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event3Attributes = new HashMap<>();
        event3Attributes.put("name", "tennis event");
        event3Attributes.put("description", "event2 description");
        event3Attributes.put("location", "demo location");
        // date in current time in millis + 2 days
        event3Attributes.put("date", new Date().getTime() + 172800000);
        event3Attributes.put("preferences", new String[]{"music", "sport"});
        event3Attributes.put("attendees", new String[]{});

        // demo target object
        String type4 = "TARGET_OBJECT";
        String alias4 = "TARGET_OBJECT";
        Location location4 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy4 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> targetObjectAttributes = new HashMap<>();
        targetObjectAttributes.put("name", "target object");


        // GIVEN
        // 1. USER 1 is registered to the system
        help_PostUserBoundary(email1, role1, username1, avatar1);
        // 2. USER 1 creates  3 event
        help_PostObjectBoundary(new ObjectId(), type1, alias1, new Date(), true, location1, createdBy1, event1Attributes);
        help_PostObjectBoundary(new ObjectId(), type2, alias2, new Date(), true, location2, createdBy2, event2Attributes);
        help_PostObjectBoundary(new ObjectId(), type3, alias3, new Date(), true, location3, createdBy3, event3Attributes);
        // 3. USER 1 creates target object
        String targetObjectId = help_PostObjectBoundary(new ObjectId(), type4, alias4, new Date(), true, location4, createdBy4, targetObjectAttributes).getObjectId().getInternalObjectId();


        // WHEN
        // user 1 role is changed to MINIAPP_USER
        UserBoundary userBoundary = help_GetUserBoundary(email1);
        userBoundary.setRole(UserRole.MINIAPP_USER.toString());
        help_PutUserBoundary(userBoundary, email1);

        // user 1 search for event by dates
        String miniappName = "EVENT";
        CommandId commandId = new CommandId().setSuperapp(springApplicationName).setMiniapp(miniappName);
        String command = "SEARCH_EVENTS_BY_DATE";
        TargetObject targetObject = new TargetObject().setObjectId(new ObjectId().setInternalObjectId(targetObjectId).setSuperapp(springApplicationName));
        Date date = new Date();
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> params = new HashMap<>();
        long startDate = new Date().getTime();
        long endDate = new Date().getTime() + 3600000 * 4;
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        Object object = help_PostCommandBoundary(miniappName, commandId, command, targetObject, date, invokedBy, params);


        // THEN
        // 1. USER 1 receives 2 events
        List<SuperAppObjectBoundary> objectBoundaries = objectToListOfObjectBoundaries(object);
        assertEquals(2, objectBoundaries.size());

    }

    // search event by category needs to be exact match
    @Test
    @DisplayName("Test search event by category")
    void searchByCategoryTest() {
        // USER 1
        String email1 = "user1@gmail.com";
        String role1 = UserRole.SUPERAPP_USER.toString();
        String username1 = "user1";
        String avatar1 = "user1.png";

        // event 1 in the future
        String type1 = "EVENT";
        String alias1 = "EVENT";
        Location location1 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy1 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event1Attributes = new HashMap<>();
        event1Attributes.put("name", "ski event");
        event1Attributes.put("description", "event1 description");
        event1Attributes.put("location", "demo location");
        // date in current time in millis + 1 hour
        event1Attributes.put("date", new Date().getTime() + 3600000);
        event1Attributes.put("preferences", new String[]{"music"});
        event1Attributes.put("attendees", new String[]{});

        // event 2 in the future
        String type2 = "EVENT";
        String alias2 = "EVENT";
        Location location2 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy2 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event2Attributes = new HashMap<>();
        event2Attributes.put("name", "tennis event");
        event2Attributes.put("description", "event2 description");
        event2Attributes.put("location", "demo location");
        // date in current time in millis + 2 hour
        event2Attributes.put("date", new Date().getTime() + 7200000);
        event2Attributes.put("preferences", new String[]{"sport"});
        event2Attributes.put("attendees", new String[]{});

        // event 3 in the future
        String type3 = "EVENT";
        String alias3 = "EVENT";
        Location location3 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy3 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> event3Attributes = new HashMap<>();
        event3Attributes.put("name", "tennis event");
        event3Attributes.put("description", "event2 description");
        event3Attributes.put("location", "demo location");
        // date in current time in millis + 2 days
        event3Attributes.put("date", new Date().getTime() + 172800000);
        event3Attributes.put("preferences", new String[]{"music", "sport"});
        event3Attributes.put("attendees", new String[]{});

        // demo target object
        String type4 = "TARGET_OBJECT";
        String alias4 = "TARGET_OBJECT";
        Location location4 = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy createdBy4 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> targetObjectAttributes = new HashMap<>();
        targetObjectAttributes.put("name", "target object");


        // GIVEN
        // 1. USER 1 is registered to the system
        help_PostUserBoundary(email1, role1, username1, avatar1);
        // 2. USER 1 creates  3 event
        help_PostObjectBoundary(new ObjectId(), type1, alias1, new Date(), true, location1, createdBy1, event1Attributes);
        help_PostObjectBoundary(new ObjectId(), type2, alias2, new Date(), true, location2, createdBy2, event2Attributes);
        help_PostObjectBoundary(new ObjectId(), type3, alias3, new Date(), true, location3, createdBy3, event3Attributes);
        // 3. USER 1 creates target object
        String targetObjectId = help_PostObjectBoundary(new ObjectId(), type4, alias4, new Date(), true, location4, createdBy4, targetObjectAttributes).getObjectId().getInternalObjectId();


        // WHEN
        // user 1 role is changed to MINIAPP_USER
        UserBoundary userBoundary = help_GetUserBoundary(email1);
        userBoundary.setRole(UserRole.MINIAPP_USER.toString());
        help_PutUserBoundary(userBoundary, email1);
        // user 1 search for event by category
        String miniappName = "EVENT";
        CommandId commandId = new CommandId().setSuperapp(springApplicationName).setMiniapp(miniappName);
        String command = "SEARCH_EVENTS_BY_PREFERENCES";
        TargetObject targetObject = new TargetObject().setObjectId(new ObjectId().setInternalObjectId(targetObjectId).setSuperapp(springApplicationName));
        Date date = new Date();
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> params = new HashMap<>();
        params.put("preferences", new String[]{"sport"});
        Object object = help_PostCommandBoundary(miniappName, commandId, command, targetObject, date, invokedBy, params);

        // THEN
        // 1. USER 1 receives 1 event
        List<SuperAppObjectBoundary> objectBoundaries = objectToListOfObjectBoundaries(object);
        assertEquals(2, objectBoundaries.size());


    }


}
