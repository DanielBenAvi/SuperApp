package superapp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import superapp.data.UserRole;
import superapp.logic.boundaries.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

public class MarketplaceTestSet extends BaseTestSet {

    // get all products created by a user
    @Test
    @DisplayName("Get all products created by a user")
    public void getAllProductsCreatedByUser() {
        // user 1
        String email1 = "user1@gmail.com";
        String role1 = UserRole.SUPERAPP_USER.toString();
        String username1 = "user1";
        String avatar1 = "user1.png";

        // user 2
        String email2 = "user2@gmail.com";
        String role2 = UserRole.SUPERAPP_USER.toString();
        String username2 = "user2";
        String avatar2 = "user2.png";

        String type = "PRODUCT";
        String alias = "product1";
        Location location = new Location().setLat(32.115139).setLng(34.817804);

        // create product 1 -> user 1
        boolean active1 = true;
        CreatedBy createdBy1 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes1 = new HashMap<>();
        Attributes1.put("name", "product1");
        Attributes1.put("price", 100);
        Attributes1.put("description", "product1 description");
        Attributes1.put("image", "product1.png");
        Attributes1.put("preferences", new String[]{"music", "sport"});

        // create product 2 -> user 1 not active
        boolean active2 = false;
        CreatedBy createdBy2 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes2 = new HashMap<>();
        Attributes2.put("name", "product2");
        Attributes2.put("price", 200);
        Attributes2.put("description", "product2 description");
        Attributes2.put("image", "product2.png");
        Attributes2.put("preferences", new String[]{"music", "sport"});

        // create product 3 -> user 2 active
        boolean active3 = true;
        CreatedBy createdBy3 = new CreatedBy().setUserId(new UserId().setEmail(email2).setSuperapp(springApplicationName));
        Map<String, Object> Attributes3 = new HashMap<>();
        Attributes3.put("name", "product3");
        Attributes3.put("price", 300);
        Attributes3.put("description", "product3 description");
        Attributes3.put("image", "product3.png");
        Attributes3.put("preferences", new String[]{"music", "sport"});

        // demo target object
        String type4 = "TARGET_OBJECT";
        String alias4 = "TARGET_OBJECT";
        CreatedBy createdBy4 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> targetObjectAttributes = new HashMap<>();
        targetObjectAttributes.put("name", "target object");

        // ##################### GIVEN #####################
        // 1. 2 users are registered
        help_PostUserBoundary(email1, role1, username1, avatar1);
        help_PostUserBoundary(email2, role2, username2, avatar2);
        // 2. user 1 created 2 products
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active1, location, createdBy1, Attributes1);
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active2, location, createdBy2, Attributes2);
        // 3. user 2 created 1 product
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active3, location, createdBy3, Attributes3);
        // 4. 1 target object
        String targetObjectId = help_PostObjectBoundary(new ObjectId(), type4, alias4, new Date(), active3, location, createdBy4, targetObjectAttributes).getObjectId().getInternalObjectId();

        // ##################### WHEN #####################
        // 1. change user 1 role to miniapp user
        UserBoundary userBoundary = help_GetUserBoundary(email1);
        userBoundary.setRole(UserRole.MINIAPP_USER.toString());
        help_PutUserBoundary(userBoundary, email1);

        // 2. get all products created by user 1
        String miniappName = "MARKETPLACE";
        CommandId commandId = new CommandId().setSuperapp(springApplicationName).setMiniapp(miniappName);
        String command = "GET_ALL_MY_PRODUCTS";
        TargetObject targetObject = new TargetObject().setObjectId(new ObjectId().setInternalObjectId(targetObjectId).setSuperapp(springApplicationName));
        Date date = new Date();
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> params = new HashMap<>();
        Object object = help_PostCommandBoundary(miniappName, commandId, command, targetObject, date, invokedBy, params);

        List<SuperAppObjectBoundary> objectBoundaries = objectToListOfObjectBoundaries(object);

        // ##################### THEN #####################

        assertThat(objectBoundaries).hasSize(1);
        for (SuperAppObjectBoundary objectBoundary : objectBoundaries) {
            assertEquals(objectBoundary.getCreatedBy().getUserId().getEmail(), email1);
        }

    }

    // get all products by users preferences
    @Test
    @DisplayName("Get all products by users preferences")
    public void getAllProductsByUsersPreferences() {
        // user 1 -> preferences: music, sport
        // user details

        // product 1 -> preferences: music
        // product 2 -> preferences: music, sport
        // product 3 -> preferences: sport
        // product 4 -> preferences: food
        // product 5 -> preferences: food, sport

        // demo target object

        // ##################### GIVEN #####################

        // ##################### WHEN #####################

        // ##################### THEN #####################




    }

    // search for products by name

    // search for products by preferences

    // search for products by name

    // search for products by price
}
