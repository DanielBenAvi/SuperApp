package superapp;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
                this.baseUrl + "/superapp/objects/{superapp}/{email}"
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



    public void help_PutRelationBetweenObjects(String superapp, String internalObjectId, ObjectId childObjId) {

        // internalObjectId here is a parent-target
        this.restTemplate.put(this.baseUrl + "/superapp/objects/{superapp}/{internalObjectId}/children"
                            , childObjId
                            , superapp
                            , internalObjectId
        );
    }

    public SuperAppObjectBoundary[] help_GetRelationParents(String superapp, String internalObjectId) {

        return this.restTemplate
                .getForObject(this.baseUrl + "/superapp/objects/{superapp}/{internalObjectId}/parents"
                            , SuperAppObjectBoundary[].class
                            , superapp
                            , internalObjectId
                );
    }

    public SuperAppObjectBoundary[] help_GetRelationChildren(String superapp, String internalObjectId) {

        return this.restTemplate
                .getForObject(this.baseUrl + "/superapp/objects/{superapp}/{internalObjectId}/children"
                            , SuperAppObjectBoundary[].class
                            , superapp
                            , internalObjectId
                );
    }
}
