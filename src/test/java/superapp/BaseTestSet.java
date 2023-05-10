package superapp;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import superapp.logic.boundaries.*;

import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseTestSet {
    protected String springApplicationName;
    protected RestTemplate restTemplate;
    protected String baseUrl;
    protected int port;

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
        this.restTemplate.delete(this.baseUrl + "/superapp/admin/miniapp");
        this.restTemplate.delete(this.baseUrl + "/superapp/admin/users");
        this.restTemplate.delete(this.baseUrl + "/superapp/admin/objects");
    }

    /**
     * POST:
     * Helper method to create a user
     * the path is "/superapp/users"
     *
     * @param email    - the email of the user
     * @param role     - the role of the user
     * @param username - the username of the user
     * @param avatar   - the avatar of the user
     * @return the user that was created
     */
    public UserBoundary postUserBoundary(String email, String role, String username, String avatar) {
        NewUserBoundary user = new NewUserBoundary();
        user.setEmail(email).setRole(role).setUsername(username).setAvatar(avatar);
        return this.restTemplate
                .postForObject(
                        this.baseUrl + "/superapp/users"
                        , user
                        , UserBoundary.class);
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
    public SuperAppObjectBoundary help_PostObjectBoundary(ObjectId objectId, String type, String alias,
                                                          Date creationTimestamp, Boolean active, Location location,
                                                          CreatedBy createdBy, Map<String, Object> objectDetails) {

        SuperAppObjectBoundary objectBoundary = new SuperAppObjectBoundary()
                .setObjectId(objectId)
                .setType(type)
                .setAlias(alias)
                .setActive(active)
                .setLocation(location)
                .setCreationTimestamp(creationTimestamp)
                .setCreatedBy(createdBy)
                .setObjectDetails(objectDetails);

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
    public void help_PutObjectBoundary(SuperAppObjectBoundary objectBoundary, String internalObjectId,
                                                                        String springApplicationName) {

        this.restTemplate.put(
                this.baseUrl + "/superapp/objects/{superapp}/{internalObjectId}"
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

    /**
     * PUT
     * Helper method to update relation between objects
     * the path is "/superapp/objects/{superapp}/{internalObjectId}/children"
     *
     * @param internalObjectId- is a parent-target
     * @param childObjId
     */
    public void putRelationBetweenObjects(String internalObjectId, ObjectId childObjId) {

        String putRelationUrl = "/superapp/objects/{superapp}/{internalObjectId}/children";
        this.restTemplate.put(
                this.baseUrl + putRelationUrl
                , childObjId
                , this.springApplicationName
                , internalObjectId );
    }

    /**
     * GET
     * Helper method to get all parent objects of some object
     * the path is "/superapp/objects/{superapp}/{internalObjectId}/parents"
     *
     * @param internalObjectId
     * @return SuperAppObjectBoundary[] - parent of internalObjectId
     */
    public SuperAppObjectBoundary[] getRelationParents(String internalObjectId) {

        String getParentUrl = "/superapp/objects/{superapp}/{internalObjectId}/parents";

        return this.restTemplate
                .getForObject(
                        this.baseUrl + getParentUrl
                            , SuperAppObjectBoundary[].class
                            , this.springApplicationName
                            , internalObjectId );
    }

    /**
     * GET
     * Helper method to get all children objects of some object
     * the path is "/superapp/objects/{superapp}/{internalObjectId}/children"
     *
     * @param internalObjectId
     * @return SuperAppObjectBoundary[] - children of internalObjectId
     */
    public SuperAppObjectBoundary[] getRelationChildren(String internalObjectId) {

        String getChildrenUrl = "/superapp/objects/{superapp}/{internalObjectId}/children";

        return this.restTemplate
                .getForObject(
                        this.baseUrl + getChildrenUrl
                            , SuperAppObjectBoundary[].class
                            , this.springApplicationName
                            , internalObjectId );
    }
}
