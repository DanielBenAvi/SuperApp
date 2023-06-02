package superapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import superapp.data.UserRole;
import superapp.logic.boundaries.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

public class MarketPlaceTestSet extends BaseTestSet {

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
        String email1 = "user1@gmail.com";
        String role1 = UserRole.SUPERAPP_USER.toString();
        String username1 = "user1";
        String avatar1 = "user1.png";


        // create user Details Object Boundary
        String userDetailsType = "USER_DETAILS";
        String userDetailsAlias = "USER_DETAILS";
        Location userDetailsLocation = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy userDetailsCreatedBy = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> userDetailsAttributes = new HashMap<>();
        userDetailsAttributes.put("name", "user 1");
        userDetailsAttributes.put("phoneNum", "+9721234567");
        userDetailsAttributes.put("preferences", new String[]{"music", "sport"});

        String type = "PRODUCT";
        String alias = "product";
        Location location = new Location().setLat(32.115139).setLng(34.817804);

        // product 1 -> preferences: music
        boolean active1 = true;
        CreatedBy createdBy1 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes1 = new HashMap<>();
        Attributes1.put("name", "product1");
        Attributes1.put("price", 100);
        Attributes1.put("description", "product1 description");
        Attributes1.put("image", "product1.png");
        Attributes1.put("preferences", new String[]{"music"});

        // product 2 -> preferences: music, sport
        boolean active2 = true;
        CreatedBy createdBy2 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes2 = new HashMap<>();
        Attributes2.put("name", "product2");
        Attributes2.put("price", 200);
        Attributes2.put("description", "product2 description");
        Attributes2.put("image", "product2.png");
        Attributes2.put("preferences", new String[]{"music", "sport"});

        // product 3 -> preferences: sport
        boolean active3 = true;
        CreatedBy createdBy3 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes3 = new HashMap<>();
        Attributes3.put("name", "product3");
        Attributes3.put("price", 300);
        Attributes3.put("description", "product3 description");
        Attributes3.put("image", "product3.png");
        Attributes3.put("preferences", new String[]{"sport"});

        // product 4 -> preferences: food
        boolean active4 = true;
        CreatedBy createdBy4 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes4 = new HashMap<>();
        Attributes4.put("name", "product4");
        Attributes4.put("price", 400);
        Attributes4.put("description", "product4 description");
        Attributes4.put("image", "product4.png");
        Attributes4.put("preferences", new String[]{"food"});

        // product 5 -> preferences: food, sport
        boolean active5 = true;
        CreatedBy createdBy5 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes5 = new HashMap<>();
        Attributes5.put("name", "product5");
        Attributes5.put("price", 500);
        Attributes5.put("description", "product5 description");
        Attributes5.put("image", "product5.png");
        Attributes5.put("preferences", new String[]{"food", "sport"});

        // product 6 -> preferences: sport : active = false
        boolean active6 = false;
        CreatedBy createdBy6 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes6 = new HashMap<>();
        Attributes6.put("name", "product6");
        Attributes6.put("price", 600);
        Attributes6.put("description", "product6 description");
        Attributes6.put("image", "product6.png");
        Attributes6.put("preferences", new String[]{"sport"});

        // demo target object

        // ##################### GIVEN #####################
        // 1. 1 user is registered
        help_PostUserBoundary(email1, role1, username1, avatar1);
        // 2. user 1 created 1 user details object
        String targetObjectId = help_PostObjectBoundary(new ObjectId(), userDetailsType, userDetailsAlias, new Date(), true, userDetailsLocation, userDetailsCreatedBy, userDetailsAttributes).getObjectId().getInternalObjectId();

        // 3. user 1 created 6 products
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active1, location, createdBy1, Attributes1);
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active2, location, createdBy2, Attributes2);
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active3, location, createdBy3, Attributes3);
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active4, location, createdBy4, Attributes4);
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active5, location, createdBy5, Attributes5);
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active6, location, createdBy6, Attributes6);


        // ##################### WHEN #####################
        // 1. change user 1 role to miniapp user
        UserBoundary userBoundary = help_GetUserBoundary(email1);
        userBoundary.setRole(UserRole.MINIAPP_USER.toString());
        help_PutUserBoundary(userBoundary, email1);

        // 2. get all products by user 1 preference
        String miniappName = "MARKETPLACE";
        CommandId commandId = new CommandId().setSuperapp(springApplicationName).setMiniapp(miniappName);
        String command = "GET_PRODUCTS_BY_PREFERENCES";
        TargetObject targetObject = new TargetObject().setObjectId(new ObjectId().setInternalObjectId(targetObjectId).setSuperapp(springApplicationName));
        Date date = new Date();
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> params = new HashMap<>();
        Object object = help_PostCommandBoundary(miniappName, commandId, command, targetObject, date, invokedBy, params);

        List<SuperAppObjectBoundary> objectBoundaries = objectToListOfObjectBoundaries(object);


        // ##################### THEN #####################
        assertThat(objectBoundaries.size()).isEqualTo(4);

        for (SuperAppObjectBoundary objectBoundary : objectBoundaries) {

            // check that all products are active and all type is PRODUCT
            assertThat(objectBoundary.getActive()).isTrue();
            assertThat(objectBoundary.getType()).isEqualTo("PRODUCT");


            ObjectMapper objectMapper = new ObjectMapper();
            String[] preferences = objectMapper.convertValue(objectBoundary.getObjectDetails().get("preferences"), String[].class);
            // check that all products have at least 1 preference that are in user preferences
            assertThat(preferences)
                    .isNotEmpty()
                    .containsAnyOf("music", "sport");
        }
    }

    // search for products by name
    @Test
    @DisplayName("test search for products by name")
    public void searchProductsByName() {
        // user 1 -> preferences: music, sport
        String email1 = "user1@gmail.com";
        String role1 = UserRole.SUPERAPP_USER.toString();
        String username1 = "user1";
        String avatar1 = "user1.png";


        // create user Details Object Boundary
        String userDetailsType = "USER_DETAILS";
        String userDetailsAlias = "USER_DETAILS";
        Location userDetailsLocation = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy userDetailsCreatedBy = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> userDetailsAttributes = new HashMap<>();
        userDetailsAttributes.put("name", "user 1");
        userDetailsAttributes.put("phoneNum", "+9721234567");
        userDetailsAttributes.put("preferences", new String[]{"music", "sport"});

        String type = "PRODUCT";
        String alias = "product";
        Location location = new Location().setLat(32.115139).setLng(34.817804);

        // product 1 -> name: product1
        boolean active1 = true;
        CreatedBy createdBy1 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes1 = new HashMap<>();
        Attributes1.put("name", "product1");
        Attributes1.put("price", 100);
        Attributes1.put("description", "product1 description");
        Attributes1.put("image", "product1.png");
        Attributes1.put("preferences", new String[]{"music"});

        // product 2 -> name: product2
        boolean active2 = true;
        CreatedBy createdBy2 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes2 = new HashMap<>();
        Attributes2.put("name", "product2");
        Attributes2.put("price", 200);
        Attributes2.put("description", "product2 description");
        Attributes2.put("image", "product2.png");
        Attributes2.put("preferences", new String[]{"music", "sport"});

        // product 3 -> name: product1 : active = false
        boolean active3 = false;
        CreatedBy createdBy3 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes3 = new HashMap<>();
        Attributes3.put("name", "product3");
        Attributes3.put("price", 300);
        Attributes3.put("description", "product3 description");
        Attributes3.put("image", "product3.png");
        Attributes3.put("preferences", new String[]{"music"});


        // ##################### GIVEN #####################
        // 1. 1 user is registered
        help_PostUserBoundary(email1, role1, username1, avatar1);
        // 2. user 1 created 1 user details object
        String targetObjectId = help_PostObjectBoundary(new ObjectId(), userDetailsType, userDetailsAlias, new Date(), true, userDetailsLocation, userDetailsCreatedBy, userDetailsAttributes).getObjectId().getInternalObjectId();
        // 3. user 1 created 3 products
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active1, location, createdBy1, Attributes1);
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active2, location, createdBy2, Attributes2);
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active3, location, createdBy3, Attributes3);

        // ##################### WHEN #####################
        // 1. change user 1 role to miniapp user
        UserBoundary userBoundary = help_GetUserBoundary(email1);
        userBoundary.setRole(UserRole.MINIAPP_USER.toString());
        help_PutUserBoundary(userBoundary, email1);

        // 2. get all products by name
        String miniappName = "MARKETPLACE";
        CommandId commandId = new CommandId().setSuperapp(springApplicationName).setMiniapp(miniappName);
        String command = "SEARCH_PRODUCT_BY_NAME";
        TargetObject targetObject = new TargetObject().setObjectId(new ObjectId().setInternalObjectId(targetObjectId).setSuperapp(springApplicationName));
        Date date = new Date();
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> params = new HashMap<>();
        params.put("name", "product");
        Object object = help_PostCommandBoundary(miniappName, commandId, command, targetObject, date, invokedBy, params);

        List<SuperAppObjectBoundary> objectBoundaries = objectToListOfObjectBoundaries(object);

        // ##################### THEN #####################
        assertThat(objectBoundaries.size()).isEqualTo(2);

        for (SuperAppObjectBoundary objectBoundary : objectBoundaries) {
            // check that all products are active and all type is PRODUCT
            assertThat(objectBoundary.getActive()).isTrue();
            assertThat(objectBoundary.getType()).isEqualTo("PRODUCT");

            // check that all products name contains "product"
            assertThat(objectBoundary.getObjectDetails().get("name").toString()).contains("product");

        }
    }

    // search for products by preferences
    @Test
    @DisplayName("test search for products by preferences")
    public void searchProductsByPreferences() {
        // user 1 -> preferences: music, sport
        String email1 = "user1@gmail.com";
        String role1 = UserRole.SUPERAPP_USER.toString();
        String username1 = "user1";
        String avatar1 = "user1.png";


        // create user Details Object Boundary
        String userDetailsType = "USER_DETAILS";
        String userDetailsAlias = "USER_DETAILS";
        Location userDetailsLocation = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy userDetailsCreatedBy = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> userDetailsAttributes = new HashMap<>();
        userDetailsAttributes.put("name", "user 1");
        userDetailsAttributes.put("phoneNum", "+9721234567");
        userDetailsAttributes.put("preferences", new String[]{"music", "sport"});

        String type = "PRODUCT";
        String alias = "product";
        Location location = new Location().setLat(32.115139).setLng(34.817804);

        // product 1 -> preferences: music
        boolean active1 = true;
        CreatedBy createdBy1 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes1 = new HashMap<>();
        Attributes1.put("name", "product1");
        Attributes1.put("price", 100);
        Attributes1.put("description", "product1 description");
        Attributes1.put("image", "product1.png");
        Attributes1.put("preferences", new String[]{"music"});

        // product 2 -> preferences: music, sport
        boolean active2 = true;
        CreatedBy createdBy2 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes2 = new HashMap<>();
        Attributes2.put("name", "product2");
        Attributes2.put("price", 200);
        Attributes2.put("description", "product2 description");
        Attributes2.put("image", "product2.png");
        Attributes2.put("preferences", new String[]{"music", "sport"});

        // product 3 -> preferences: sport
        boolean active3 = true;
        CreatedBy createdBy3 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes3 = new HashMap<>();
        Attributes3.put("name", "product3");
        Attributes3.put("price", 300);
        Attributes3.put("description", "product3 description");
        Attributes3.put("image", "product3.png");
        Attributes3.put("preferences", new String[]{"sport"});

        // product 4 -> preferences: food
        boolean active4 = true;
        CreatedBy createdBy4 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes4 = new HashMap<>();
        Attributes4.put("name", "product4");
        Attributes4.put("price", 400);
        Attributes4.put("description", "product4 description");
        Attributes4.put("image", "product4.png");
        Attributes4.put("preferences", new String[]{"food"});

        // product 5 -> preferences: food, sport
        boolean active5 = true;
        CreatedBy createdBy5 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes5 = new HashMap<>();
        Attributes5.put("name", "product5");
        Attributes5.put("price", 500);
        Attributes5.put("description", "product5 description");
        Attributes5.put("image", "product5.png");
        Attributes5.put("preferences", new String[]{"food", "sport"});

        // product 6 -> preferences: sport : active = false
        boolean active6 = false;
        CreatedBy createdBy6 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes6 = new HashMap<>();
        Attributes6.put("name", "product6");
        Attributes6.put("price", 600);
        Attributes6.put("description", "product6 description");
        Attributes6.put("image", "product6.png");
        Attributes6.put("preferences", new String[]{"sport"});


        // ##################### GIVEN #####################
        // 1. 1 user is registered
        help_PostUserBoundary(email1, role1, username1, avatar1);
        // 2. user 1 created 1 user details object
        String targetObjectId = help_PostObjectBoundary(new ObjectId(), userDetailsType, userDetailsAlias, new Date(), true, userDetailsLocation, userDetailsCreatedBy, userDetailsAttributes).getObjectId().getInternalObjectId();
        // 3. user 1 created 3 products
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active1, location, createdBy1, Attributes1);
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active2, location, createdBy2, Attributes2);
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active3, location, createdBy3, Attributes3);
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active4, location, createdBy4, Attributes4);
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active5, location, createdBy5, Attributes5);
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active6, location, createdBy6, Attributes6);

        // ##################### WHEN #####################
        // 1. change user 1 role to miniapp user
        UserBoundary userBoundary = help_GetUserBoundary(email1);
        userBoundary.setRole(UserRole.MINIAPP_USER.toString());
        help_PutUserBoundary(userBoundary, email1);

        // 2. get all products by name
        String miniappName = "MARKETPLACE";
        CommandId commandId = new CommandId().setSuperapp(springApplicationName).setMiniapp(miniappName);
        String command = "SEARCH_PRODUCT_BY_PREFERENCES";
        TargetObject targetObject = new TargetObject().setObjectId(new ObjectId().setInternalObjectId(targetObjectId).setSuperapp(springApplicationName));
        Date date = new Date();
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> params = new HashMap<>();
        params.put("preferences", new String[]{"food", "sport"});
        Object object = help_PostCommandBoundary(miniappName, commandId, command, targetObject, date, invokedBy, params);

        List<SuperAppObjectBoundary> objectBoundaries = objectToListOfObjectBoundaries(object);

        // ##################### THEN #####################
        assertThat(objectBoundaries.size()).isEqualTo(4);

        for (SuperAppObjectBoundary objectBoundary : objectBoundaries) {

            // check that all products are active and all type is PRODUCT
            assertThat(objectBoundary.getActive()).isTrue();
            assertThat(objectBoundary.getType()).isEqualTo("PRODUCT");


            ObjectMapper objectMapper = new ObjectMapper();
            String[] preferences = objectMapper.convertValue(objectBoundary.getObjectDetails().get("preferences"), String[].class);
            // check that all products have at least 1 preference that are in user preferences
            assertThat(preferences)
                    .isNotEmpty()
                    .containsAnyOf("sport", "food");
        }

    }

    // search for products by price
    @Test
    @DisplayName("test search products by price")
    public void searchProductByPrice(){
        // user 1 -> preferences: music, sport
        String email1 = "user1@gmail.com";
        String role1 = UserRole.SUPERAPP_USER.toString();
        String username1 = "user1";
        String avatar1 = "user1.png";


        // create user Details Object Boundary
        String userDetailsType = "USER_DETAILS";
        String userDetailsAlias = "USER_DETAILS";
        Location userDetailsLocation = new Location().setLat(32.115139).setLng(34.817804);
        CreatedBy userDetailsCreatedBy = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> userDetailsAttributes = new HashMap<>();
        userDetailsAttributes.put("name", "user 1");
        userDetailsAttributes.put("phoneNum", "+9721234567");
        userDetailsAttributes.put("preferences", new String[]{"music", "sport"});

        String type = "PRODUCT";
        String alias = "product";
        Location location = new Location().setLat(32.115139).setLng(34.817804);

        // product 1 -> preferences: music
        boolean active1 = true;
        CreatedBy createdBy1 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes1 = new HashMap<>();
        Attributes1.put("name", "product1");
        Attributes1.put("price", 100);
        Attributes1.put("description", "product1 description");
        Attributes1.put("image", "product1.png");
        Attributes1.put("preferences", new String[]{"music"});

        // product 2 -> preferences: music, sport
        boolean active2 = true;
        CreatedBy createdBy2 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes2 = new HashMap<>();
        Attributes2.put("name", "product2");
        Attributes2.put("price", 200.5);
        Attributes2.put("description", "product2 description");
        Attributes2.put("image", "product2.png");
        Attributes2.put("preferences", new String[]{"music", "sport"});

        // product 3 -> preferences: sport
        boolean active3 = true;
        CreatedBy createdBy3 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes3 = new HashMap<>();
        Attributes3.put("name", "product3");
        Attributes3.put("price", 300);
        Attributes3.put("description", "product3 description");
        Attributes3.put("image", "product3.png");
        Attributes3.put("preferences", new String[]{"sport"});

        // product 4 -> preferences: food
        boolean active4 = true;
        CreatedBy createdBy4 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes4 = new HashMap<>();
        Attributes4.put("name", "product4");
        Attributes4.put("price", 400);
        Attributes4.put("description", "product4 description");
        Attributes4.put("image", "product4.png");
        Attributes4.put("preferences", new String[]{"food"});

        // product 5 -> preferences: food, sport
        boolean active5 = true;
        CreatedBy createdBy5 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes5 = new HashMap<>();
        Attributes5.put("name", "product5");
        Attributes5.put("price", 500);
        Attributes5.put("description", "product5 description");
        Attributes5.put("image", "product5.png");
        Attributes5.put("preferences", new String[]{"food", "sport"});

        // product 6 -> preferences: sport : active = false
        boolean active6 = false;
        CreatedBy createdBy6 = new CreatedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> Attributes6 = new HashMap<>();
        Attributes6.put("name", "product6");
        Attributes6.put("price", 600);
        Attributes6.put("description", "product6 description");
        Attributes6.put("image", "product6.png");
        Attributes6.put("preferences", new String[]{"sport"});

        // ##################### GIVEN #####################
        // 1. 1 user is registered
        help_PostUserBoundary(email1, role1, username1, avatar1);
        // 2. user 1 created 1 user details object
        String targetObjectId = help_PostObjectBoundary(new ObjectId(), userDetailsType, userDetailsAlias, new Date(), true, userDetailsLocation, userDetailsCreatedBy, userDetailsAttributes).getObjectId().getInternalObjectId();
        // 3. user 1 created 3 products
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active1, location, createdBy1, Attributes1);
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active2, location, createdBy2, Attributes2);
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active3, location, createdBy3, Attributes3);
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active4, location, createdBy4, Attributes4);
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active5, location, createdBy5, Attributes5);
        help_PostObjectBoundary(new ObjectId(), type, alias, new Date(), active6, location, createdBy6, Attributes6);

        // ##################### WHEN #####################
        // 1. change user 1 role to miniapp user
        UserBoundary userBoundary = help_GetUserBoundary(email1);
        userBoundary.setRole(UserRole.MINIAPP_USER.toString());
        help_PutUserBoundary(userBoundary, email1);

        // 2. get all products by name
        String miniappName = "MARKETPLACE";
        CommandId commandId = new CommandId().setSuperapp(springApplicationName).setMiniapp(miniappName);
        String command = "SEARCH_PRODUCT_BY_PRICE";
        TargetObject targetObject = new TargetObject().setObjectId(new ObjectId().setInternalObjectId(targetObjectId).setSuperapp(springApplicationName));
        Date date = new Date();
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setEmail(email1).setSuperapp(springApplicationName));
        Map<String, Object> params = new HashMap<>();
        params.put("minPrice", 200);
        params.put("maxPrice", 400);
        Object object = help_PostCommandBoundary(miniappName, commandId, command, targetObject, date, invokedBy, params);

        List<SuperAppObjectBoundary> objectBoundaries = objectToListOfObjectBoundaries(object);

        // ##################### THEN #####################
        assertThat(objectBoundaries.size()).isEqualTo(3);

        for (SuperAppObjectBoundary objectBoundary : objectBoundaries) {

            // check that all products are active and all type is PRODUCT
            assertThat(objectBoundary.getActive()).isTrue();
            assertThat(objectBoundary.getType()).isEqualTo("PRODUCT");

            // check that all products are in the price range
            double price = Double.parseDouble(objectBoundary.getObjectDetails().get("price").toString());
            assertThat(price).isBetween(200.0, 400.0);
        }


    }
}
