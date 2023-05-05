package superapp;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import superapp.logic.boundaries.*;

import java.util.Map;

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
        this.restTemplate = new RestTemplate();
        this.baseUrl = "http://localhost:" + this.port;
    }
    @AfterEach
    public void tearDown() {
        this.restTemplate.delete(this.baseUrl + "/superapp/admin/objects");
    }
//    public SuperAppObjectBoundary help_PostObjectBoundary(String type, String alias, boolean active, Location location,
//                                                          CreatedBy createdBy, Map<String, Object> objectDetails) {
//        SuperAppObjectBoundary objectBoundary = new SuperAppObjectBoundary();
//        objectBoundary.setType(type).setAlias(alias).setActive(active).
//                setLocation(location).setCreatedBy(createdBy).setObjectDetails(objectDetails);
//        return this.restTemplate
//                .postForObject(
//                        this.baseUrl + "/superapp/objects"
//                        , user
//                        , UserBoundary.class);
//    }
//
//
//    /**
//     * GET:
//     * Helper method to get a user
//     * the path is "/superapp/users/login/{superapp}/{email}"
//     *
//     * @param email - the email of the user
//     * @return the user that was created
//     */
//    public UserBoundary help_GetUserBoundary(String email) {
//        return this.restTemplate
//                .getForObject(
//                        this.baseUrl + "/superapp/users/login/{superapp}/{email}"
//                        , UserBoundary.class
//                        , springApplicationName
//                        , email);
//    }
//
//    /**
//     * PUT
//     * Helper method to update a user
//     * the path is "/superapp/users/{superapp}/{email}"
//     *
//     * @param userBoundary - the user to update
//     * @param email        - the email of the user we want to update
//     */
//    public void help_PutUserBoundary(UserBoundary userBoundary, String email) {
//        this.restTemplate.put(
//                this.baseUrl + "/superapp/users/{superapp}/{email}"
//                , userBoundary, springApplicationName
//                , email);
//    }
//
//    /**
//     * DELETE
//     * Helper method to delete a user
//     * the path is "/superapp/admin/users"
//     */
//    public void help_DeleteUsersBoundary() {
//        this.restTemplate.delete(this.baseUrl + "/superapp/admin/users");
//    }
//
//    /**
//     * GET_ALL
//     * Helper method to get all users
//     * the path is "/superapp/admin/users
//     *
//     * @return all users - UserBoundary[]
//     */
//    public UserBoundary[] help_GetAllUsersBoundary() {
////
//        return this.restTemplate.getForObject(this.baseUrl + "/superapp/admin/users", UserBoundary[].class);
//    }
}
