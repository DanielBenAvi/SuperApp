package superapp;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import superapp.data.UserRole;
import superapp.logic.boundaries.*;


import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ObjectTestSet {
    private String springApplicationName;
    private RestTemplate restTemplate;
    private String baseUrl;
    private int port;

    @Value("${spring.application.name:defaultAppName}")
    public void setApplicationName(String springApplicationName) {
        this.springApplicationName = springApplicationName;
    }

    @LocalServerPort
    public void setPort(int port) {
        this.port = port;
    }

    @PostConstruct
    public void setup() {
        this.baseUrl = "http://localhost:" + this.port;
        this.restTemplate = new RestTemplate();
    }

    @AfterEach
    public void tearDown() {

        String email = "admin@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "admin_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        this.restTemplate.delete(this.baseUrl + "/superapp/admin/objects?userSuperapp={userSuperapp}&userEmail={email}"
                , springApplicationName,email);
        this.restTemplate.delete(this.baseUrl + "/superapp/admin/users?userSuperapp={userSuperapp}&userEmail={email}"
                , springApplicationName,email);
    }

    /*post Tests*/
    @Test
    @DisplayName("Successful create object")
    public void SuccessfulCreateObject() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject = help_PostObjectBoundary(null, type, alias, null,
                                    active,  location, createdBy,  objectDetails);
        // THEN
        // the server response with status 2xx code and return  SuperAppObjectBoundary as json

        SuperAppObjectBoundary objectFromGet = help_GetObjectBoundary(
                                        postObject.getObjectId().getInternalObjectId(),
                                        postObject.getObjectId().getSuperapp(),springApplicationName,email);

        assertThat(objectFromGet)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(postObject);
    }

    @Test
    @DisplayName("Successful create object with null active")
    public void SuccessfulCreateObjectWith_NullActive() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects
        String type = "EVENT";
        String alias = "demo";
        Boolean active = null;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject = help_PostObjectBoundary(null, type, alias, null,
                active,  location, createdBy,  objectDetails);



        // THEN
        // the server response with status 2xx code and return  SuperAppObjectBoundary as json

        SuperAppObjectBoundary objectFromGet = help_GetObjectBoundary(
                postObject.getObjectId().getInternalObjectId(),
                postObject.getObjectId().getSuperapp(),springApplicationName,email);
        objectFromGet.setActive(false);

        assertThat(objectFromGet)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(postObject);
    }

    @Test
    @DisplayName("Successful create object with values in objectId and Timestamp")
    public void SuccessfulCreateObjectWith_ObjectId_TimestampValues() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary
        ObjectId objectId = new ObjectId().setSuperapp(springApplicationName).
                setInternalObjectId("this is my id that was not created by server");
        String type = "EVENT";
        String alias = "demo";
        String input = "2022-01-25T08:36:50.925+00:00";
        Date creationTimestamp = Date.from(Instant.parse(input));
        Boolean active = false;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject = help_PostObjectBoundary(objectId, type, alias, creationTimestamp,
                active,  location, createdBy,  objectDetails);
        // THEN
        // the server response with status 2xx code and return  SuperAppObjectBoundary as json

        SuperAppObjectBoundary objectFromGet = help_GetObjectBoundary(
                postObject.getObjectId().getInternalObjectId(),
                postObject.getObjectId().getSuperapp(),springApplicationName,email);

        assertThat(objectFromGet)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(postObject);

        assertThat(objectFromGet.getObjectId().getInternalObjectId()).isNotNull().
                isNotEqualTo("this is my id that was not created by server");

        assertThat(objectFromGet.getCreationTimestamp()).isNotNull().
                isNotEqualTo(creationTimestamp);

    }

    @Test
    @DisplayName("unsuccessful create object, userId attributes are null")
    public void unsuccessfulCreateObject_userIdAttributeNull() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running
        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "DATING";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId());
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");


        // THEN
        // the server response with bad request status 400 code

        assertThatThrownBy(() ->
                help_PostObjectBoundary(null, type, alias, null, active, location, createdBy, objectDetails))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));

    }

    @Test
    @DisplayName("unsuccessful create object, no userId")
    public void unsuccessfulCreateObject_noUserId() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "GROUP";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy();
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");


        // THEN
        // the server response with bad request status 400 code

        assertThatThrownBy(() ->
                help_PostObjectBoundary(null, type, alias, null, active, location, createdBy, objectDetails))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));

    }

    @Test
    @DisplayName("unsuccessful create object, invalid email")
    public void unsuccessfulCreateObject_invalidEmail() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,"demo@_s.afeka.ac.il" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");


        // THEN
        // the server response with bad request status 400 code

        assertThatThrownBy(() ->
                help_PostObjectBoundary(null, type, alias, null, active, location, createdBy, objectDetails))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));

    }

    @Test
    @DisplayName("unsuccessful create object, invalid superapp")
    public void unsuccessfulCreateObject_invalidSuperapp() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId("2023LiorAriely", email));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");


        // THEN
        // the server response with Bad request status 400 code

        assertThatThrownBy(() ->
                help_PostObjectBoundary(null, type, alias, null, active, location, createdBy, objectDetails))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));

    }

    @Test
    @DisplayName("unsuccessful create object, email not found")
    public void unsuccessfulCreateObject_emailNotFound() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // when
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,"demoTest@s.afeka.ac.il" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");


        // THEN
        // the server response with bad request status 404 code
        assertThatThrownBy(() ->
                help_PostObjectBoundary(null, type, alias, null, active, location, createdBy, objectDetails))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));

    }

    @Test
    @DisplayName("unsuccessful create object, no createdBy")
    public void unsuccessfulCreateObject_noCreatedBy() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running


        // when
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "GROUP";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");


        // THEN
        // the server response with bad request status 400 code

        assertThatThrownBy(() ->
                help_PostObjectBoundary(null, type, alias, null, active, location, null, objectDetails))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));

    }

    @Test
    @DisplayName("unsuccessful create object, no Location")
    public void unsuccessfulCreateObject_noLocation() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        // THEN
        // the server response with bad request status 400 code

        assertThatThrownBy(() ->
                help_PostObjectBoundary(null, type, alias, null, active, null, createdBy, objectDetails))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("unsuccessful create object, empty attributes in location")
    public void unsuccessfulCreateObject_LocationAttributesEmpty() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location();
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        // THEN
        // the server response with 200 ok and the new object location attributes are set to 0.0,0.0
        SuperAppObjectBoundary postObject = help_PostObjectBoundary(null, type, alias,
                null, active, location, createdBy, objectDetails);

        SuperAppObjectBoundary objectFromGet = help_GetObjectBoundary(
                postObject.getObjectId().getInternalObjectId(),
                postObject.getObjectId().getSuperapp(),springApplicationName,email);

        postObject.getLocation().setLat(0.0).setLng(0.0);
        assertThat(objectFromGet)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(postObject);

    }

    @Test
    @DisplayName("unsuccessful create object, no objectDetails")
    public void unsuccessfulCreateObject_noObjectDetails() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "GROUP";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" ));

        // THEN
        // the server response with bad request status 400 code

        assertThatThrownBy(() ->
                help_PostObjectBoundary(null, type, alias, null, active, location, createdBy, null))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }
    @Test
    @DisplayName("unsuccessful create object, no superapp")
    public void unsuccessfulCreateObject_noSuperapp() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "GROUP";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId().setEmail("demo@gmail.com"));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        // THEN
        // the server response with bad request status 400 code

        assertThatThrownBy(() ->
                help_PostObjectBoundary(null, type, alias, null, active, location, createdBy, objectDetails))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("unsuccessful create object, type null")
    public void unsuccessfulCreateObject_nullType() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        // THEN
        // the server response with bad request status 400 code

        assertThatThrownBy(() ->
                help_PostObjectBoundary(null, null, alias, null, active, location, createdBy, objectDetails))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("unsuccessful create object, empty type")
    public void unsuccessfulCreateObject_typeEmpty() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary
        String type = "";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        // THEN
        // the server response with bad request status 400 code

        assertThatThrownBy(() ->
                help_PostObjectBoundary(null, type, alias, null, active, location, createdBy, objectDetails))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("unsuccessful create object, null alias")
    public void unsuccessfulCreateObject_nullAlias() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary
        String type = "GROUP";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        // THEN
        // the server response with bad request status 400 code

        assertThatThrownBy(() ->
                help_PostObjectBoundary(null, type, null, null, active, location, createdBy, objectDetails))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("unsuccessful create object, empty alias")
    public void unsuccessfulCreateObject_emptyAlias() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary
        String type = "GROUP";
        String alias = "";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        // THEN
        // the server response with bad request status 400 code

        assertThatThrownBy(() ->
                help_PostObjectBoundary(null, type, alias, null, active, location, createdBy, objectDetails))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    /*get Tests*/
    @Test
    @DisplayName("successful get object")
    public void SuccessfulGetObject() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName, email));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject = help_PostObjectBoundary(null, type, alias, null,
                active,  location, createdBy,  objectDetails);
        // WHEN
        // A GET request is made to the path "/superapp/objects/{superapp}/
        // {internalObjectId}?userSuperapp={userSuperapp}&userEmail={email}"
        SuperAppObjectBoundary objectFromGet = help_GetObjectBoundary(
                postObject.getObjectId().getInternalObjectId(),
                postObject.getObjectId().getSuperapp(),springApplicationName,email);



        // THEN
        // the server response with status 2xx code and return  SuperAppObjectBoundary as json

        assertThat(objectFromGet)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(postObject);
    }

    @Test
    @DisplayName("unsuccessful get object it does not exists")
    public void unsuccessfulGetObject_doesNotExists() {

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);
        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. db does not contain object
        // 4. superappUser is in the db



        // WHEN
        // A GET request is made to the path "/superapp/objects/{superapp}
        // /{internalObjectId}?userSuperapp={userSuperapp}&userEmail={email}"



        // THEN
        // the server response with not found status code 404

        assertThatThrownBy(() ->
                help_GetObjectBoundary("c2759119-f06f-4f3c-a8ca-9db9b16301c1", springApplicationName,
                        springApplicationName,email))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    @DisplayName("unsuccessful get object invalid superapp")
    public void unsuccessfulGetObject_invalidSuperapp() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject = help_PostObjectBoundary(null, type, alias, null,
                active,  location, createdBy,  objectDetails);

        // WHEN
        // A GET request is made to the path "/superapp/objects/{superapp}
        // /{internalObjectId}?userSuperapp={userSuperapp}&userEmail={email}"



        // THEN
        // the server response with BadRequest status code 400

        assertThatThrownBy(() ->
                help_GetObjectBoundary(postObject.getObjectId().getInternalObjectId(), "2021a.someGuy"
                ,springApplicationName,email))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }
    /*get all Tests*/
    @Test
    @DisplayName("successful get all objects with larger paging size")
    public void successfulGetAllObjects() {


        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,email));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);
        help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);

        // WHEN
        // A GET request is made to the path /superapp/objects?userSuperapp={superapp}&
        // userEmail={email}&size={size}&page={page}"

        SuperAppObjectBoundary[] objects = help_GetAllObjectsBoundary(springApplicationName,email,20,0);

        // THEN
        // the server response with status 2xx code
        assertThat(objects).hasSize(2);
    }


    @Test
    @DisplayName("successful get all objects when there are none")
    public void successfulGetAllObjectsEmpty() {

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        // WHEN
        // A GET request is made to the path "/superapp/objects"
        SuperAppObjectBoundary[] objects = help_GetAllObjectsBoundary(springApplicationName,email,20,0);

        // THEN
        // the server response with status 2xx code
        assertThat(objects).isEmpty();
    }

    /*delete all Tests*/

    @Test
    @DisplayName("successful delete all objects of none empty db")
    public void successfulDelAllObjects() {

        String adminEmail = "testingAdmin@gmail.com";
        String adminRole = UserRole.ADMIN.toString();
        String adminUsername = "testAdmin_user";
        String adminAvatar = "testAdmin_avatar";
        help_PostUserBoundary(adminEmail, adminRole, adminUsername, adminAvatar);

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);
        help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);

        // WHEN
        // A DELETE request is made to the path "/superapp/admin/objects"
        help_DeleteObjectsBoundary(springApplicationName, adminEmail);


        // THEN
        // the server response with status 2xx code
        SuperAppObjectBoundary[] objects = help_GetAllObjectsBoundary(springApplicationName,email,20,0);
        assertThat(objects).isEmpty();
    }

    @Test
    @DisplayName("successful delete all objects in empty db")
    public void successfulDelAllObjectsEmpty() {

        String adminEmail = "testingAdmin@gmail.com";
        String adminRole = UserRole.ADMIN.toString();
        String adminUsername = "testAdmin_user";
        String adminAvatar = "testAdmin_avatar";
        help_PostUserBoundary(adminEmail, adminRole, adminUsername, adminAvatar);

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        // WHEN
        // A DELETE request is made to the path "/superapp/admin/objects"
        help_DeleteObjectsBoundary(springApplicationName, adminEmail);


        // THEN
        // the server response with status 2xx code
        SuperAppObjectBoundary[] objects = help_GetAllObjectsBoundary(springApplicationName,email,20,0);
        assertThat(objects).isEmpty();
    }

    /*put Tests*/
    @Test
    @DisplayName("unsuccessful update object id does not exist")
    public void unsuccessfulUpdate_objectIdDoesNotExist() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject =
                help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);

        SuperAppObjectBoundary updateObject = new SuperAppObjectBoundary().setObjectId(null).setType(null).setAlias("test demo").setActive(null).
                setCreationTimestamp(null).setLocation(null).setCreatedBy(null).setObjectDetails(null);

        // WHEN
        // A PUT request is made to the path "/superapp/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&
        // userEmail={email}"

        // THEN
        // the server response with BadRequest status code 400
        assertThatThrownBy(() ->
                help_PutObjectBoundary(updateObject,"diffrent_ID_that_not in_db",
                        postObject.getObjectId().getSuperapp(),
                        springApplicationName,
                        email)).isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }


    @Test
    @DisplayName("successful update object alias")
    public void successfulUpdateObjectAlias() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject =
                help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);

        SuperAppObjectBoundary updateObject = new SuperAppObjectBoundary().setObjectId(null).setType(null).setAlias("another demo").setActive(null).
                setCreationTimestamp(null).setLocation(null).setCreatedBy(null).setObjectDetails(null);
        // WHEN
        // A PUT request is made to the path "/superapp/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&
        // userEmail={email}"
        help_PutObjectBoundary(updateObject,postObject.getObjectId().getInternalObjectId(),
                postObject.getObjectId().getSuperapp(),
                springApplicationName, email);

        SuperAppObjectBoundary objectFromGet = help_GetObjectBoundary(
                postObject.getObjectId().getInternalObjectId(),
                postObject.getObjectId().getSuperapp(),springApplicationName,email);


        // THEN
        // the server response with status 2xx code
        assertThat(objectFromGet.getAlias()).isNotNull().isEqualTo(updateObject.getAlias());
    }

    @Test
    @DisplayName("successful update object deatils")
    public void successfulUpdateObjectDetails() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,email));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject =
                help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);

        Map<String, Object> objectDetails2 = new HashMap<>();
        SuperAppObjectBoundary updateObject = new SuperAppObjectBoundary().setObjectId(null).setType(null).setAlias(null).setActive(null).
                setCreationTimestamp(null).setLocation(null).setCreatedBy(null).setObjectDetails(objectDetails2);

        updateObject.getObjectDetails().put("details2", "something");

        // WHEN
        // A PUT request is made to the path "/superapp/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&
        // userEmail={email}"

        help_PutObjectBoundary(updateObject,postObject.getObjectId().getInternalObjectId(),
                postObject.getObjectId().getSuperapp(),
                springApplicationName, email);

        SuperAppObjectBoundary objectFromGet = help_GetObjectBoundary(
                postObject.getObjectId().getInternalObjectId(),
                postObject.getObjectId().getSuperapp(),springApplicationName,email);

        // THEN
        // the server response with status 2xx code
        assertThat(objectFromGet.getObjectDetails()).isNotNull()
                .usingRecursiveComparison().isEqualTo(updateObject.getObjectDetails());
    }

    @Test
    @DisplayName("successful update empty object deatils")
    public void successfulUpdateEmptyObjectDetails() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject =
                help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);


        SuperAppObjectBoundary updateObject = new SuperAppObjectBoundary().setObjectId(null).setType(null).setAlias(null).setActive(null).
                setCreationTimestamp(null).setLocation(null).setCreatedBy(null).setObjectDetails(null);


        // WHEN
        // A PUT request is made to the path "/superapp/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&
        // userEmail={email}"

        help_PutObjectBoundary(updateObject,postObject.getObjectId().getInternalObjectId(),
                postObject.getObjectId().getSuperapp(),
                springApplicationName, email);

        SuperAppObjectBoundary objectFromGet = help_GetObjectBoundary(
                postObject.getObjectId().getInternalObjectId(),
                postObject.getObjectId().getSuperapp(),springApplicationName,email);
        // THEN
        // the server response with status 2xx code
        assertThat(objectFromGet.getObjectDetails()).isNotNull().usingRecursiveComparison().isEqualTo(objectDetails);
    }

    /*Wrong paths Tests*/
    @Test
    @DisplayName("unsuccessful create object, wrong path")
    public void unsuccessfulCreateObject_WrongPath() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superpp/objects" with SuperAppObjectBoundary

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,email ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary object = new SuperAppObjectBoundary().setType(type).setAlias(alias).setActive(active).
                setLocation(location).setCreatedBy(createdBy).setObjectDetails(objectDetails);

        // THEN
        // the server response with status 404 not found
        assertThatThrownBy(() ->
                this.restTemplate.postForObject(this.baseUrl + "/superpp/objects", object, SuperAppObjectBoundary.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));



    }

    @Test
    @DisplayName("unsuccessful update object, wrong path")
    public void unsuccessfulUpdateObject_WrongPath() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,email ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject =
                help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);
        // WHEN
        // A PUT request is made to the path "/superapp/obects/2023b.LiorAriely/ffc180ce-880d-4b74-9b36-8c78501a155b"

        SuperAppObjectBoundary object = new SuperAppObjectBoundary().setType(null).setAlias("demos").setActive(null).
                setLocation(null).setCreatedBy(null).setObjectDetails(null);


        // THEN
        // the server response with status 404 not found
        assertThatThrownBy(() ->
                this.restTemplate.put(this.baseUrl + "/superpp/obects{superapp}/{internalObjectId}",
                        object, postObject.getObjectId().getSuperapp(),
                        postObject.getObjectId().getInternalObjectId())).isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));

        assertThat(postObject.getAlias()).isNotNull().isNotEqualTo(object.getAlias());

    }

    @Test
    @DisplayName("unsuccessful get object, wrong path")
    public void unsuccessfulGetObject_WrongPath() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,email));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject =
                help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);
        // WHEN
        // A GET request is made to the path"/suerapp/objects/2023b.LiorAriely/{internalObjectId}"


        // THEN
        // the server response with status 404 not found
        assertThatThrownBy(() ->
                this.restTemplate.getForObject(this.baseUrl + "/suerapp/objects/{superapp}/{internalObjectId}"
                        , SuperAppObjectBoundary.class, postObject.getObjectId().getSuperapp(), postObject.getObjectId().getInternalObjectId()))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));


    }
    @Test
    @DisplayName("unsuccessful get all object, wrong path")
    public void unsuccessfulGetAllObject_WrongPath() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");


        help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);
        help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);

        // WHEN
        // A GET request is made to the path "/superapp/object"



        // THEN
        // the server response with status 404 not found
        assertThatThrownBy(() ->
                this.restTemplate.getForObject(this.baseUrl + "/superapp/object", SuperAppObjectBoundary[].class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));


    }

    @Test
    @DisplayName("unsuccessful get delete, wrong path")
    public void unsuccessfulDelete_WrongPath() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String adminEmail = "testingAdmin@gmail.com";
        String adminRole = UserRole.ADMIN.toString();
        String adminUsername = "testAdmin_user";
        String adminAvatar = "testAdmin_avatar";
        help_PostUserBoundary(adminEmail, adminRole, adminUsername, adminAvatar);

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName, email));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");


        help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);
        help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);

        // WHEN
        // A DELETE request is made to the path "/superapp/amin/objects"



        // THEN
        // the server response with status 404 NOT_FOUND

        assertThatThrownBy(() ->
                this.restTemplate
                        .delete(this.baseUrl + "/superapp/amin/objects?userSuperapp={superapp}&" +
                                "userEmail={email}",springApplicationName, adminEmail))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));


    }
    /*search objects Tests */
    @Test
    @DisplayName("Search object by type")
    public void SearchObject_ByType() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName, email));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        String type2 = "EVENT";
        String alias2 = "demo";
        Boolean active2 = true;
        Location location2 = new Location(10.200, 10.200);
        CreatedBy createdBy2 = new CreatedBy().setUserId(new UserId(springApplicationName, email));
        Map<String, Object> objectDetails2 = new HashMap<>();
        objectDetails.put("details", "String object demo");

        String type3 = "DATING";
        String alias3 = "demo";
        Boolean active3 = true;
        Location location3 = new Location(10.200, 10.200);
        CreatedBy createdBy3 = new CreatedBy().setUserId(new UserId(springApplicationName, email));
        Map<String, Object> objectDetails3 = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject = help_PostObjectBoundary(null, type, alias, null,
                active,  location, createdBy,  objectDetails);

        SuperAppObjectBoundary postObject2 = help_PostObjectBoundary(null, type2, alias2, null,
                active2,  location2, createdBy2,  objectDetails2);

        SuperAppObjectBoundary postObject3 = help_PostObjectBoundary(null, type3, alias3, null,
                active3,  location3, createdBy3,  objectDetails3);

        // WHEN
        // A GET request is made to the path "/superapp/objects/search/byType{type}?userSuperapp={superapp}&
        // userEmail={email}&size={size}&page={page}"


        SuperAppObjectBoundary[] objects = this.restTemplate.
                getForObject(this.baseUrl + "/superapp/objects/search/byType/{type}?userSuperapp={superapp}&" +
                 "userEmail={email}&size={size}&page={page}", SuperAppObjectBoundary[].class,"EVENT",springApplicationName, email, 10 , 0);



        // THEN
        // the server response with status 2xx code and return SuperAppObjectBoundary array with events only as type

        assertThat(objects.length)
                .isNotNull()
                .isEqualTo(2);

        for (SuperAppObjectBoundary object : objects){
            assertThat(object.getType())
                    .isNotNull()
                    .isEqualTo("EVENT");
        }


    }

    @Test
    @DisplayName("Search object by alias")
    public void SearchObject_ByAlias() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName, email));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        String type2 = "EVENT";
        String alias2 = "demo2";
        Boolean active2 = true;
        Location location2 = new Location(10.200, 10.200);
        CreatedBy createdBy2 = new CreatedBy().setUserId(new UserId(springApplicationName, email));
        Map<String, Object> objectDetails2 = new HashMap<>();
        objectDetails.put("details", "String object demo");

        String type3 = "DATING";
        String alias3 = "demo";
        Boolean active3 = true;
        Location location3 = new Location(10.200, 10.200);
        CreatedBy createdBy3 = new CreatedBy().setUserId(new UserId(springApplicationName, email));
        Map<String, Object> objectDetails3 = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject = help_PostObjectBoundary(null, type, alias, null,
                active,  location, createdBy,  objectDetails);

        SuperAppObjectBoundary postObject2 = help_PostObjectBoundary(null, type2, alias2, null,
                active2,  location2, createdBy2,  objectDetails2);

        SuperAppObjectBoundary postObject3 = help_PostObjectBoundary(null, type3, alias3, null,
                active3,  location3, createdBy3,  objectDetails3);

        // WHEN
        // A GET request is made to the path "/superapp/objects/search/byType{type}?userSuperapp={superapp}&
        // userEmail={email}&size={size}&page={page}"


        SuperAppObjectBoundary[] objects = this.restTemplate.
                getForObject(this.baseUrl + "/superapp/objects/search/byAlias/{alias}?userSuperapp={superapp}&" +
                        "userEmail={email}&size={size}&page={page}", SuperAppObjectBoundary[].class,"demo",springApplicationName, email, 10 , 0);



        // THEN
        // the server response with status 2xx code and return  SuperAppObjectBoundary array with demo only as alias

        assertThat(objects.length)
                .isNotNull()
                .isEqualTo(2);

        for (SuperAppObjectBoundary object : objects){
            assertThat(object.getAlias())
                    .isNotNull()
                    .isEqualTo("demo");
        }


    }

    @Test
    @DisplayName("unsuccessful Search object by type wrong permission")
    public void unsuccessfulSearchObject_ByType() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String adminEmail = "admin0@gmail.com";
        String adminRole = UserRole.ADMIN.toString();
        String adminUsername = "admin_user";
        String adminAvatar = "demo_avatar";
        help_PostUserBoundary(adminEmail, adminRole, adminUsername, adminAvatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName, email));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        String type2 = "EVENT";
        String alias2 = "demo2";
        Boolean active2 = true;
        Location location2 = new Location(10.200, 10.200);
        CreatedBy createdBy2 = new CreatedBy().setUserId(new UserId(springApplicationName, email));
        Map<String, Object> objectDetails2 = new HashMap<>();
        objectDetails.put("details", "String object demo");

        String type3 = "DATING";
        String alias3 = "demo";
        Boolean active3 = true;
        Location location3 = new Location(10.200, 10.200);
        CreatedBy createdBy3 = new CreatedBy().setUserId(new UserId(springApplicationName, email));
        Map<String, Object> objectDetails3 = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject = help_PostObjectBoundary(null, type, alias, null,
                active,  location, createdBy,  objectDetails);

        SuperAppObjectBoundary postObject2 = help_PostObjectBoundary(null, type2, alias2, null,
                active2,  location2, createdBy2,  objectDetails2);

        SuperAppObjectBoundary postObject3 = help_PostObjectBoundary(null, type3, alias3, null,
                active3,  location3, createdBy3,  objectDetails3);

        // WHEN
        // A GET request is made to the path "/superapp/objects/search/byType{type}?userSuperapp={superapp}&
        // userEmail={email}&size={size}&page={page}"






        // THEN
        // the server response with status  code 401 unauthorized

        assertThatThrownBy(() ->
                this.restTemplate.
                        getForObject(this.baseUrl + "/superapp/objects/search/byType/{type}?userSuperapp={superapp}&" +
                                        "userEmail={email}&size={size}&page={page}",
                                SuperAppObjectBoundary[].class,"EVENT",springApplicationName, adminEmail, 10 , 0))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED));


    }

    @Test
    @DisplayName("Search Object by radius")
    public void SearchObject_ByLocation() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName, email));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        String type2 = "EVENT";
        String alias2 = "demo2";
        Boolean active2 = true;
        Location location2 = new Location(10.200, 10.200);
        CreatedBy createdBy2 = new CreatedBy().setUserId(new UserId(springApplicationName, email));
        Map<String, Object> objectDetails2 = new HashMap<>();
        objectDetails.put("details", "String object demo");



        SuperAppObjectBoundary postObject = help_PostObjectBoundary(null, type, alias, null,
                active,  location, createdBy,  objectDetails);

        SuperAppObjectBoundary postObject2 = help_PostObjectBoundary(null, type2, alias2, null,
                active2,  location2, createdBy2,  objectDetails2);



        // WHEN
        // A GET request is made to the path "/superapp/objects/search/byType{type}?userSuperapp={superapp}&
        // userEmail={email}&size={size}&page={page}"


        SuperAppObjectBoundary[] objects = this.restTemplate.
                getForObject(this.baseUrl + "/superapp/objects/search/byLocation/{lat}/{lng}/" +
                        "{distance}?units={distanceUnits}&userSuperapp={superapp}&" +
                        "userEmail={email}&size={size}&page={page}", SuperAppObjectBoundary[].class, "10.200", "10.200", "1"
                        , "NEUTRAL", springApplicationName, email, 10 , 0);



        // THEN
        // the server response with status 2xx code and return  SuperAppObjectBoundary array

        assertThat(objects.length)
                .isNotNull()
                .isEqualTo(2);



    }

    @Test
    @DisplayName("unsuccessful Search Object by radius with small radius ")
    public void SearchObject_ByLocation_NoObjects() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.SUPERAPP_USER.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(75.200, 75.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId(springApplicationName, email));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        String type2 = "EVENT";
        String alias2 = "demo2";
        Boolean active2 = true;
        Location location2 = new Location(75.200, 75.200);
        CreatedBy createdBy2 = new CreatedBy().setUserId(new UserId(springApplicationName, email));
        Map<String, Object> objectDetails2 = new HashMap<>();
        objectDetails.put("details", "String object demo");



        SuperAppObjectBoundary postObject = help_PostObjectBoundary(null, type, alias, null,
                active,  location, createdBy,  objectDetails);

        SuperAppObjectBoundary postObject2 = help_PostObjectBoundary(null, type2, alias2, null,
                active2,  location2, createdBy2,  objectDetails2);



        // WHEN
        // A GET request is made to the path "/superapp/objects/search/byType{type}?userSuperapp={superapp}&
        // userEmail={email}&size={size}&page={page}"


        SuperAppObjectBoundary[] objects = this.restTemplate.
                getForObject(this.baseUrl + "/superapp/objects/search/byLocation/{lat}/{lng}/" +
                                "{distance}?units={distanceUnits}&userSuperapp={superapp}&" +
                                "userEmail={email}&size={size}&page={page}", SuperAppObjectBoundary[].class, "0.200", "0.200", "0.1"
                        , "NEUTRAL", springApplicationName, email, 10 , 0);



        // THEN
        // the server response with status 2xx code and return  SuperAppObjectBoundary array

        assertThat(objects.length)
                .isNotNull()
                .isEqualTo(0);



    }
    /*helper functions*/


    /**
     * POST:
     * Helper method to create a SuperAppObjectBoundary
     * the path is "/superapp/objects"
     *
     * @param type
     * @param alias
     * @param active
     * @param location
     * @param createdBy
     * @param objectDetails
     * @return SuperAppObjectBoundary
     */
    public SuperAppObjectBoundary help_PostObjectBoundary(
            ObjectId objectId, String type, String alias, Date creationTimestamp,
            Boolean active, Location location, CreatedBy createdBy, Map<String, Object> objectDetails) {

        SuperAppObjectBoundary objectBoundary = new SuperAppObjectBoundary();

        objectBoundary.setObjectId(objectId).setType(type).setAlias(alias).setActive(active).
                setLocation(location).setCreationTimestamp(creationTimestamp).setCreatedBy(createdBy).setObjectDetails(objectDetails);

        return this.restTemplate
                .postForObject(
                        this.baseUrl + "/superapp/objects"
                        , objectBoundary
                        , SuperAppObjectBoundary.class);
    }


    /**
     * GET:
     * Helper method to get an object
     * the path is "/superapp/objects/{superapp}/{internalObjectId}"
     *
     * @param internalObjectId
     * @param springApplicationName
     * @return SuperAppObjectBoundary
     */
    public SuperAppObjectBoundary help_GetObjectBoundary(String internalObjectId, String springApplicationName, String userSuperapp, String userEmail) {
        return this.restTemplate
                .getForObject(
                        this.baseUrl + "/superapp/objects/{superapp}/" +
                                "{internalObjectId}?userSuperapp={userSuperapp}&userEmail={email}"
                        , SuperAppObjectBoundary.class
                        , springApplicationName
                        , internalObjectId
                        , userSuperapp
                        , userEmail);

    }

    /**
     * PUT
     * Helper method to update an object
     * the path is "/superapp/objects/{superapp}/{internalObjectId}"
     *
     * @param objectBoundary
     * @param internalObjectId
     * @param springApplicationName
     */
    public void help_PutObjectBoundary(SuperAppObjectBoundary objectBoundary,
                                         String internalObjectId,
                                         String springApplicationName,
                                         String userSuperapp,
                                         String userEmail) {
        this.restTemplate.put(
                this.baseUrl + "/superapp/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&" +
                        "userEmail={email}"
                , objectBoundary, springApplicationName, internalObjectId, userSuperapp, userEmail);
    }

    /**
     * DELETE
     * Helper method to delete all objects
     * the path is "/superapp/admin/objects"
     *
     */
    public void help_DeleteObjectsBoundary(String userSuperapp, String userEmail) {
        this.restTemplate.delete(this.baseUrl + "/superapp/admin/objects?userSuperapp={superapp}&" +
                                "userEmail={email}",userSuperapp, userEmail);
    }

    /**
     * GET_ALL
     * Helper method to get all objects
     * the path is "/superapp/objects
     *
     * @return SuperAppObjectBoundary[]
     */
    public SuperAppObjectBoundary[] help_GetAllObjectsBoundary(String userSuperapp, String userEmail,
                                                               int size, int page) {

        return this.restTemplate
                .getForObject(this.baseUrl + "/superapp/objects?userSuperapp={superapp}&" +
                        "userEmail={email}&size={size}&page={page}", SuperAppObjectBoundary[].class,userSuperapp,
                        userEmail, size, page);
    }

    public UserBoundary help_PostUserBoundary(String email, String role, String username, String avatar) {
        NewUserBoundary user = new NewUserBoundary();
        user.setEmail(email).setRole(role).setUsername(username).setAvatar(avatar);
        return this.restTemplate
                .postForObject(
                        this.baseUrl + "/superapp/users"
                        , user
                        , UserBoundary.class);
    }


}
