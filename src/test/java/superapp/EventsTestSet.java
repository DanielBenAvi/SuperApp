package superapp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import superapp.data.UserRole;
import superapp.logic.boundaries.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventsTestSet extends BaseTestSet {

    // get all future events
    @Test
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

    // get all my events

    // join event

    // leave event

    // create event

    // search event by name

    // search event by location // todo

    // search event by date

    // search event by category



    private static List<SuperAppObjectBoundary> objectToListOfObjectBoundaries(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.convertValue(object, Map.class);
        Map.Entry<String, Object> entry = map.entrySet().iterator().next();
        Object value = entry.getValue();

        // convert object to list of object boundary
        List<SuperAppObjectBoundary> objectBoundaries = mapper.convertValue(value, new TypeReference<>() {});
        return objectBoundaries;
    }
}
