package superapp;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import superapp.logic.boundaries.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ObjectTestSet {
    private String springApplicationName;
    // private RestTemplateBuilder restTemplateBuilder;
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

        // create one user for all tests
//        NewUserBoundary user = new NewUserBoundary();
//        user.setEmail("demo@gmail.com").setRole(UserRole.ADMIN.toString()).setUsername("demo_user").setAvatar("demo_avatar");
//
//        this.restTemplate.postForObject(this.baseUrl + "/superapp/users"
//                                , user
//                                , UserBoundary.class);
    }

    @Test
    @DisplayName("Successful create object")
    public void SuccessfulCreateObject() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. db contain user - initialize in setup


        // when
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
        // then
        // the server response with status 2xx code and return  SuperAppObjectBoundary as json

        SuperAppObjectBoundary objectFromGet = help_GetObjectBoundary(
                                        postObject.getObjectId().getInternalObjectId(),
                                        postObject.getObjectId().getSuperapp());

        assertThat(objectFromGet)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(postObject);
    }


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
                this.baseUrl + "/superapp/users/{superapp}/{email}"
                , objectBoundary
                , springApplicationName
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

}
