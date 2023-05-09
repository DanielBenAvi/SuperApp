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
        this.restTemplate.delete(this.baseUrl + "/superapp/admin/users");
        this.restTemplate.delete(this.baseUrl + "/superapp/admin/objects");
    }


    @Test
    @DisplayName("Successful create object")
    public void SuccessfulCreateObject() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject = help_PostObjectBoundary(null, type, alias, null,
                active,  location, createdBy,  objectDetails);
        // THEN
        // the server response with status 2xx code and return  SuperAppObjectBoundary as json

        SuperAppObjectBoundary objectFromGet = help_GetObjectBoundary(
                postObject.getObjectId().getInternalObjectId(),
                postObject.getObjectId().getSuperapp());

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
        // 2. the database is up and runnin

        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects
        String type = "EVENT";
        String alias = "demo";
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject = help_PostObjectBoundary(null, type, alias, null,
                null,  location, createdBy,  objectDetails);
        postObject.setActive(false);


        // THEN
        // the server response with status 415 code and return  SuperAppObjectBoundary as json

        SuperAppObjectBoundary objectFromGet = help_GetObjectBoundary(
                postObject.getObjectId().getInternalObjectId(),
                postObject.getObjectId().getSuperapp());


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
        String role = UserRole.ADMIN.toString();
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
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject = help_PostObjectBoundary(objectId, type, alias, creationTimestamp,
                active,  location, createdBy,  objectDetails);
        // THEN
        // the server response with status 2xx code and return  SuperAppObjectBoundary as json

        SuperAppObjectBoundary objectFromGet = help_GetObjectBoundary(
                postObject.getObjectId().getInternalObjectId(),
                postObject.getObjectId().getSuperapp());

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
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "DATING";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID());
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
        String role = UserRole.ADMIN.toString();
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
        // the server response with  status 400 code

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
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@@s.afeka.ac.il" ));
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
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID("2023LiorAriely", "demo@s.afeka.ac.il"));
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
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // when
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demoTest@gmail.com" ));
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
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
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
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location();
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
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
    @DisplayName("unsuccessful create object, no objectDetails")
    public void unsuccessfulCreateObject_noObjectDetails() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "GROUP";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));

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
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "GROUP";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID().setEmail("demo@gmail.com"));
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
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
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
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary
        String type = "";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
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
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary
        String type = "GROUP";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
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
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary
        String type = "GROUP";
        String alias = "";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        // THEN
        // the server response with bad request status 400 code

        assertThatThrownBy(() ->
                help_PostObjectBoundary(null, type, alias, null, active, location, createdBy, objectDetails))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    /*get specific*/
    @Test
    @DisplayName("successful get object")
    public void SuccessfulGetObject() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject = help_PostObjectBoundary(null, type, alias, null,
                active,  location, createdBy,  objectDetails);
        // WHEN
        // A GET request is made to the path "/superapp/objects/2023b.LiorAriely/{internalObjectId}"
        SuperAppObjectBoundary objectFromGet = help_GetObjectBoundary(
                postObject.getObjectId().getInternalObjectId(),
                postObject.getObjectId().getSuperapp());



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

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. db does not contain object

        // WHEN
        // A GET request is made to the path "/superapp/objects/2023b.LiorAriely/{internalObjectId}"



        // THEN
        // the server response with not found status code 404

        assertThatThrownBy(() ->
                help_GetObjectBoundary("c2759119-f06f-4f3c-a8ca-9db9b16301c1", springApplicationName))
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
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject = help_PostObjectBoundary(null, type, alias, null,
                active,  location, createdBy,  objectDetails);

        // WHEN
        // A GET request is made to the path "/superapp/objects/2023b.LiorAriely/{internalObjectId}"



        // THEN
        // the server response with not found status code 404

        assertThatThrownBy(() ->
                help_GetObjectBoundary(postObject.getObjectId().getInternalObjectId(), "2021a.someGuy"))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }
    /*get all*/
    @Test
    @DisplayName("successful get all objects")
    public void successfulGetAll_Objects() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);
        help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);

        // WHEN
        // A GET request is made to the path "/superapp/objects"

        SuperAppObjectBoundary[] objects = help_GetAllObjectsBoundary();

        // THEN
        // the server response with status 2xx code
        assertThat(objects).hasSize(2);
    }

    @Test
    @DisplayName("successful get all objects when there are none")
    public void successfulGetAll_ObjectsEmpty() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        // WHEN
        // A GET request is made to the path "/superapp/objects"

        SuperAppObjectBoundary[] objects = help_GetAllObjectsBoundary();

        // THEN
        // the server response with status 2xx code
        assertThat(objects).isEmpty();
    }

    /*delete all*/

    @Test
    @DisplayName("successful delete all objects of none empty db")
    public void successfulDelAll_Objects() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);
        help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);

        // WHEN
        // A DELETE request is made to the path "/superapp/admin/objects"
        help_DeleteObjectsBoundary();


        // THEN
        // the server response with status 2xx code
        SuperAppObjectBoundary[] objects = help_GetAllObjectsBoundary();
        assertThat(objects).isEmpty();
    }

    @Test
    @DisplayName("successful delete all objects in empty db")
    public void successfulDelAll_ObjectsEmpty() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        // WHEN
        // A DELETE request is made to the path "/superapp/admin/objects"
        help_DeleteObjectsBoundary();


        // THEN
        // the server response with status 2xx code
        SuperAppObjectBoundary[] objects = help_GetAllObjectsBoundary();
        assertThat(objects).isEmpty();
    }
    /*update object*/
    @Test
    @DisplayName("unsuccessful update object id does not exist")
    public void unsuccessfulUpdate_objectIdDoesNotExist() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject =
                help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);

        SuperAppObjectBoundary updateObject = new SuperAppObjectBoundary().setObjectId(null).setType(null).setAlias("test demo").setActive(null).
                setCreationTimestamp(null).setLocation(null).setCreatedBy(null).setObjectDetails(null);

        // WHEN
        // A PUT request is made to the path "/superapp/objects/2023b.LiorAriely/diffrent_ID_that_not in_db"

        // THEN
        // the server response with not found status code 404
        assertThatThrownBy(() ->
                help_PutObjectBoundary(updateObject,"diffrent_ID_that_not in_db",
                        postObject.getObjectId().getSuperapp())).isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }



    @Test
    @DisplayName("successful update object alias")
    public void successfulUpdate_ObjectAlias() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject =
                help_PostObjectBoundary(null, type, alias,null, active, location, createdBy, objectDetails);

        SuperAppObjectBoundary updateObject = new SuperAppObjectBoundary().setAlias("another demo");
        // WHEN
        // A PUT request is made to the path "/superapp/objects/2023b.LiorAriely/{internalObjectId}"
        this.help_PutObjectBoundary(updateObject,postObject.getObjectId().getInternalObjectId(), postObject.getObjectId().getSuperapp());


        // THEN
        // the server response with status 2xx code
        SuperAppObjectBoundary updatedObject =
                this.help_GetObjectBoundary(postObject.getObjectId().getInternalObjectId(), this.springApplicationName);

        assertThat(updatedObject.getAlias())
                .isNotNull()
                .isEqualTo(updateObject.getAlias());
    }

    @Test
    @DisplayName("successful update object active")
    public void successfulUpdate_ObjectActive() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = false;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject =
                help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);

        SuperAppObjectBoundary updateObject = new SuperAppObjectBoundary().setObjectId(null).setType(null).setAlias(null).setActive(true).
                setCreationTimestamp(null).setLocation(null).setCreatedBy(null).setObjectDetails(null);
        // WHEN
        // A PUT request is made to the path "/superapp/objects/2023b.LiorAriely/{internalObjectId}"
        help_PutObjectBoundary(updateObject,postObject.getObjectId().getInternalObjectId(), postObject.getObjectId().getSuperapp());

        // THEN
        // the server response with status 2xx code
        SuperAppObjectBoundary updatedObject =
                this.help_GetObjectBoundary(postObject.getObjectId().getInternalObjectId(), this.springApplicationName);
        assertThat(updatedObject.getActive()).isNotNull().isEqualTo(updateObject.getActive());
    }

    @Test
    @DisplayName("unsuccessful update object alias is empty")
    public void unsuccessfulUpdate_ObjectAliasEmpty() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject =
                help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);

        SuperAppObjectBoundary updateObject = new SuperAppObjectBoundary().setObjectId(null).setType(null).setAlias("").setActive(null).
                setCreationTimestamp(null).setLocation(null).setCreatedBy(null).setObjectDetails(null);
        // WHEN
        // A PUT request is made to the path "/superapp/objects/2023b.LiorAriely/{internalObjectId}"


        // THEN
        // the server response with Bad Request status code 400
        SuperAppObjectBoundary updatedObject =
                this.help_GetObjectBoundary(postObject.getObjectId().getInternalObjectId(), this.springApplicationName);

        assertThatThrownBy(() ->
                help_PutObjectBoundary(updateObject,updatedObject.getObjectId().getInternalObjectId(),
                        updatedObject.getObjectId().getSuperapp())).isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));

        assertThat(updatedObject.getAlias()).isNotNull().isEqualTo(postObject.getAlias());
    }


    @Test
    @DisplayName("unsuccessful Update, Type cannot be changed")
    public void unsuccessfulUpdate_Type() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "GROUP";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject =
                help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);

        SuperAppObjectBoundary updateObject = new SuperAppObjectBoundary().setObjectId(null).setType("EVENT").setAlias(null).setActive(null).
                setCreationTimestamp(null).setLocation(null).setCreatedBy(null).setObjectDetails(null);
        // WHEN
        // A PUT request is made to the path "/superapp/objects/2023b.LiorAriely/{internalObjectId}"
        SuperAppObjectBoundary updatedObject =
                this.help_GetObjectBoundary(postObject.getObjectId().getInternalObjectId(), this.springApplicationName);

        assertThatThrownBy(() ->
                help_PutObjectBoundary(updateObject,updatedObject.getObjectId().getInternalObjectId(),
                        updatedObject.getObjectId().getSuperapp())).isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        // THEN
        // the server response with Bad Request status 400 code
        assertThat(updatedObject.getType()).isNotNull().isEqualTo(type);

    }

    @Test
    @DisplayName("successful update object deatils")
    public void successfulUpdate_ObjectDetails() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject =
                help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);

        Map<String, Object> objectDetails2 = new HashMap<>();
        objectDetails2.put("details", "somethingsomething");

        SuperAppObjectBoundary updateObject = new SuperAppObjectBoundary().setObjectId(null).setType(null).setAlias(null).setActive(null).
                setCreationTimestamp(null).setLocation(null).setCreatedBy(null).setObjectDetails(objectDetails2);



        // WHEN
        // A PUT request is made to the path "/superapp/objects/2023b.LiorAriely/{internalObjectId}"

        help_PutObjectBoundary(updateObject,postObject.getObjectId().getInternalObjectId(), postObject.getObjectId().getSuperapp());
        // THEN
        // the server response with status 2xx code
        SuperAppObjectBoundary updatedObject =
                this.help_GetObjectBoundary(postObject.getObjectId().getInternalObjectId(), this.springApplicationName);

        assertThat(updatedObject.getObjectDetails()).isNotNull().usingRecursiveComparison().isEqualTo(updateObject.getObjectDetails());
    }

    @Test
    @DisplayName("successful update object details specific value")
    public void successfulUpdate_ObjectDetailsSpesificValue() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");
        objectDetails.put("details2", null);

        SuperAppObjectBoundary postObject =
                help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);

        Map<String, Object> objectDetails2 = new HashMap<>();
        objectDetails2.put("details2", "somethingsomething");

        SuperAppObjectBoundary updateObject = new SuperAppObjectBoundary().setObjectId(null).setType(null).setAlias(null).setActive(null).
                setCreationTimestamp(null).setLocation(null).setCreatedBy(null).setObjectDetails(objectDetails2);



        // WHEN
        // A PUT request is made to the path "/superapp/objects/2023b.LiorAriely/{internalObjectId}"

        help_PutObjectBoundary(updateObject,postObject.getObjectId().getInternalObjectId(), postObject.getObjectId().getSuperapp());
        // THEN
        // the server response with status 2xx code

        SuperAppObjectBoundary updatedObject =
                this.help_GetObjectBoundary(postObject.getObjectId().getInternalObjectId(), this.springApplicationName);

        Map<String, Object> objectDetailsChecker = new HashMap<>();
        objectDetailsChecker.put("details", "String object demo");
        objectDetailsChecker.put("details2", "somethingsomething");
        assertThat(updatedObject.getObjectDetails()).isNotNull().usingRecursiveComparison().isEqualTo(objectDetailsChecker);
    }


    @Test
    @DisplayName("successful update empty object details")
    public void successfulUpdate_EmptyObjectDetails() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject =
                help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);


        SuperAppObjectBoundary updateObject = new SuperAppObjectBoundary().setObjectId(null).setType(null).setAlias(null).setActive(null).
                setCreationTimestamp(null).setLocation(null).setCreatedBy(null).setObjectDetails(null);



        // WHEN
        // A PUT request is made to the path "/superapp/objects/2023b.LiorAriely/{internalObjectId}"

        help_PutObjectBoundary(updateObject,postObject.getObjectId().getInternalObjectId(), postObject.getObjectId().getSuperapp());
        // THEN
        // the server response with status 2xx code
        SuperAppObjectBoundary updatedObject =
                this.help_GetObjectBoundary(postObject.getObjectId().getInternalObjectId(), this.springApplicationName);

        assertThat(updatedObject.getObjectDetails()).isNotNull().usingRecursiveComparison().isEqualTo(objectDetails);
    }

    @Test
    @DisplayName("successful update, location")
    public void successfulUpdate_location() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject =
                help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);

        Location expectedLocation = new Location(5, 5);
        SuperAppObjectBoundary updateObject = new SuperAppObjectBoundary().setObjectId(null).setType(null).setAlias(null).setActive(null).
                setCreationTimestamp(null).setLocation(expectedLocation).setCreatedBy(null).setObjectDetails(null);



        // WHEN
        // A PUT request is made to the path "/superapp/objects/2023b.LiorAriely/{internalObjectId}"

        help_PutObjectBoundary(updateObject,postObject.getObjectId().getInternalObjectId(), postObject.getObjectId().getSuperapp());
        // THEN
        // the server response with status 2xx code
        SuperAppObjectBoundary updatedObject =
                this.help_GetObjectBoundary(postObject.getObjectId().getInternalObjectId(), this.springApplicationName);

        assertThat(updatedObject.getLocation()).isNotNull().usingRecursiveComparison().isEqualTo(expectedLocation);
    }

    @Test
    @DisplayName("successful update, location is empty")
    public void successfulUpdate_locationEmpty() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(1, 1);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject =
                help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);

        Location expectedLocation = new Location();
        SuperAppObjectBoundary updateObject = new SuperAppObjectBoundary().setObjectId(null).setType(null).setAlias(null).setActive(null).
                setCreationTimestamp(null).setLocation(expectedLocation).setCreatedBy(null).setObjectDetails(null);



        // WHEN
        // A PUT request is made to the path "/superapp/objects/2023b.LiorAriely/{internalObjectId}"

        help_PutObjectBoundary(updateObject,postObject.getObjectId().getInternalObjectId(), postObject.getObjectId().getSuperapp());
        // THEN
        // the server response with status 2xx code

        SuperAppObjectBoundary updatedObject =
                this.help_GetObjectBoundary(postObject.getObjectId().getInternalObjectId(), this.springApplicationName);

        assertThat(updatedObject.getLocation()).isNotNull().usingRecursiveComparison().isEqualTo(location);
    }

    @Test
    @DisplayName("successful update, location attribute")
    public void successfulUpdate_locationAttribute() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(1, 1);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject =
                help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);

        Location expectedLocation = new Location().setLat(3);

        SuperAppObjectBoundary updateObject = new SuperAppObjectBoundary().setObjectId(null).setType(null).setAlias(null).setActive(null).
                setCreationTimestamp(null).setLocation(expectedLocation).setCreatedBy(null).setObjectDetails(null);



        // WHEN
        // A PUT request is made to the path "/superapp/objects/2023b.LiorAriely/{internalObjectId}"

        help_PutObjectBoundary(updateObject,postObject.getObjectId().getInternalObjectId(), postObject.getObjectId().getSuperapp());
        // THEN
        // the server response with status 2xx code
        SuperAppObjectBoundary updatedObject =
                this.help_GetObjectBoundary(postObject.getObjectId().getInternalObjectId(), this.springApplicationName);
        Location locationChecker = new Location().setLat(expectedLocation.getLat()).setLng(location.getLng());

        assertThat(updatedObject.getLocation()).isNotNull().usingRecursiveComparison().isEqualTo(locationChecker);
    }

// future test
//    @Test
//    @DisplayName("unsuccessful update, objectDetails has invalid attribute")
//    public void unsuccessfulUpdate_invalidObjectDetails() {
//
//        // GIVEN
//        // 1. the server is up and running
//        // 2. the database is up and running
//
//        String email = "demo@gmail.com";
//        String role = UserRole.ADMIN.toString();
//        String username = "demo_user";
//        String avatar = "demo_avatar";
//        help_PostUserBoundary(email, role, username, avatar);
//
//        String type = "EVENT";
//        String alias = "demo";
//        Boolean active = true;
//        Location location = new Location(10.200, 10.200);
//        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
//        Map<String, Object> objectDetails = new HashMap<>();
//        objectDetails.put("active", true);
//
//        SuperAppObjectBoundary postObject =
//                help_PostObjectBoundary(null, type, alias, null, active,  location, createdBy,  objectDetails);
//
//        Map<String, Object> objectDetails2 = new HashMap<>();
//        objectDetails.put("active", "3.4");
//
//        SuperAppObjectBoundary updateObject = new SuperAppObjectBoundary().setObjectId(null).setType(null).setAlias(null).setActive(null).
//                setCreationTimestamp(null).setLocation(null).setCreatedBy(null).setObjectDetails(objectDetails2);
//
//
//
//        // WHEN
//        // A PUT request is made to the path "/superapp/objects/2023b.LiorAriely/{internalObjectId}"
//
//        help_PutObjectBoundary(updateObject,postObject.getObjectId().getInternalObjectId(), postObject.getObjectId().getSuperapp());
//
//
//        // THEN
//        // the server response with Bad Request status 400 code
//        SuperAppObjectBoundary updatedObject =
//                this.help_GetObjectBoundary(postObject.getObjectId().getInternalObjectId(), this.springApplicationName);
//
//        assertThatThrownBy(() ->
//                help_PutObjectBoundary(updateObject,updatedObject.getObjectId().getInternalObjectId(),
//                        updatedObject.getObjectId().getSuperapp())).isInstanceOf(HttpClientErrorException.class)
//                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
//
//        assertThat(updatedObject.getObjectDetails()).isNotNull().usingRecursiveComparison().isNotEqualTo(updateObject.getObjectDetails());
//    }

    /*Wrong Paths*/
    @Test
    @DisplayName("unsuccessful create object, wrong path")
    public void unsuccessfulCreateObject_WrongPath() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);


        // WHEN
        // A POST request is made to the path "superpp/objects" with SuperAppObjectBoundary

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
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
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
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

        SuperAppObjectBoundary updatedObject =
                this.help_GetObjectBoundary(postObject.getObjectId().getInternalObjectId(), this.springApplicationName);

        assertThat(updatedObject.getAlias()).isNotNull().isNotEqualTo(object.getAlias());

    }

    @Test
    @DisplayName("unsuccessful get object, wrong path")
    public void unsuccessfulGetObject_WrongPath() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        String email = "demo@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
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
        String role = UserRole.ADMIN.toString();
        String username = "demo_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(springApplicationName,"demo@gmail.com" ));
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
    @DisplayName("unsuccessful delete, wrong path")
    public void unsuccessfulDelete_WrongPath() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running

        // WHEN
        // A DELETE request is made to the path "/superapp/amin/objects"



        // THEN
        // the server response with status 404 not found

        assertThatThrownBy(() ->
                this.restTemplate.getForObject(this.baseUrl + "/superapp/amin/objects", SuperAppObjectBoundary[].class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> assertThat(((HttpClientErrorException) e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));


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
    public SuperAppObjectBoundary help_GetObjectBoundary(String internalObjectId, String springApplicationName) {
        return this.restTemplate
                .getForObject(
                        this.baseUrl + "/superapp/objects/{superapp}/{internalObjectId}"
                        , SuperAppObjectBoundary.class
                        , springApplicationName
                        , internalObjectId);
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
                                       String springApplicationName) {
        this.restTemplate.put(
                this.baseUrl + "/superapp/objects/{superapp}/{internalObjectId}"
                , objectBoundary, springApplicationName
                , internalObjectId);
    }

    /**
     * DELETE
     * Helper method to delete all objects
     * the path is "/superapp/admin/objects"
     *
     */
    public void help_DeleteObjectsBoundary() {
        this.restTemplate.delete(this.baseUrl + "/superapp/admin/objects");
    }

    /**
     * GET_ALL
     * Helper method to get all objects
     * the path is "/superapp/objects
     *
     * @return SuperAppObjectBoundary[]
     */
    public SuperAppObjectBoundary[] help_GetAllObjectsBoundary() {

        return this.restTemplate
                .getForObject(this.baseUrl + "/superapp/objects", SuperAppObjectBoundary[].class);
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
