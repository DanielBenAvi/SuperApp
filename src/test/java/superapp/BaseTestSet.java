package superapp;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import superapp.data.UserRole;
import superapp.logic.boundaries.*;
import superapp.miniapps.MiniAppNames;

import java.util.Date;
import java.util.Map;

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
        String email = "admin@gmail.com";
        String role = UserRole.ADMIN.toString();
        String username = "admin_user";
        String avatar = "demo_avatar";
        help_PostUserBoundary(email, role, username, avatar);

        this.restTemplate.delete(this.baseUrl + "/superapp/admin/miniapp?userSuperapp={userSuperapp}&userEmail={email}"
                , springApplicationName,email);

        this.restTemplate.delete(this.baseUrl + "/superapp/admin/objects?userSuperapp={userSuperapp}&userEmail={email}"
                , springApplicationName,email);

        this.restTemplate.delete(this.baseUrl + "/superapp/admin/users?userSuperapp={userSuperapp}&userEmail={email}"
                , springApplicationName,email);
    }



    /**
     * POST:
     * Helper method to create a SuperAppObjectBoundary
     * the path is "/superapp/objects"
     *
     * @param objectId - ObjectId
     * @param type - String
     * @param alias - String
     * @param creationTimestamp - Date
     * @param active - Boolean
     * @param location - Location
     * @param createdBy - CreatedBy
     * @param objectDetails - Map<String, Object>
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
     * @param internalObjectId - String
     * @param springApplicationName - String
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
     * @param objectBoundary - SuperAppObjectBoundary
     * @param internalObjectId - String
     * @param springApplicationName - String
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

    /**
     * PUT
     * Helper method to update relation between objects
     * the path is "/superapp/objects/{superapp}/{internalObjectId}/children"
     *
     * @param internalObjectId- is a parent-target
     * @param childObjId - Object
     */
    public void putRelationBetweenObjects(String internalObjectId, Object childObjId, String userSuperapp,
                                          String userEmail) {

        String putRelationUrl = "/superapp/objects/{superapp}/{internalObjectId}/children?userSuperapp={superapp}&" +
                "userEmail={email}";
        this.restTemplate.put(
                this.baseUrl + putRelationUrl
                , childObjId
                , this.springApplicationName
                , internalObjectId
                , userSuperapp
                , userEmail);
    }

    /**
     * GET
     * Helper method to get all parent objects of some object
     * the path is "/superapp/objects/{superapp}/{internalObjectId}/parents"
     *
     * @param internalObjectId - String
     * @return SuperAppObjectBoundary[] - parent of internalObjectId
     */
    public SuperAppObjectBoundary[] getRelationParents(String internalObjectId, String userSuperapp,
                                                       String userEmail, int size, int page) {

        String getParentUrl = "/superapp/objects/{superapp}/{internalObjectId}/parents?userSuperapp={superapp}&" +
                "userEmail={email}&size={size}&page={page}";

        return this.restTemplate
                .getForObject(
                        this.baseUrl + getParentUrl
                        , SuperAppObjectBoundary[].class
                        , this.springApplicationName
                        , internalObjectId
                        , userSuperapp
                        , userEmail
                        , size
                        , page );
    }

    /**
     * GET
     * Helper method to get all children objects of some object
     * the path is "/superapp/objects/{superapp}/{internalObjectId}/children"
     *
     * @param internalObjectId - String
     * @return SuperAppObjectBoundary[] - children of internalObjectId
     */
    public SuperAppObjectBoundary[] getRelationChildren(String internalObjectId, String userSuperapp,
                                                        String userEmail, int size, int page) {

        String getChildrenUrl = "/superapp/objects/{superapp}/{internalObjectId}/children?userSuperapp={superapp}&" +
                "userEmail={email}&size={size}&page={page}";

        return this.restTemplate
                .getForObject(
                        this.baseUrl + getChildrenUrl
                        , SuperAppObjectBoundary[].class
                        , this.springApplicationName
                        , internalObjectId
                        , userSuperapp
                        , userEmail
                        , size
                        , page );
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
    public UserBoundary help_PostUserBoundary(String email, String role, String username, String avatar) {
        NewUserBoundary user = new NewUserBoundary();
        user.setEmail(email).setRole(role).setUsername(username).setAvatar(avatar);
        return this.restTemplate
                .postForObject(
                        this.baseUrl + "/superapp/users"
                        , user
                        , UserBoundary.class);
    }


    /**
     * GET:
     * Helper method to get a user
     * the path is "/superapp/users/login/{superapp}/{email}"
     *
     * @param email - the email of the user
     * @return the user that was created
     */
    public UserBoundary help_GetUserBoundary(String email) {
        return this.restTemplate
                .getForObject(
                        this.baseUrl + "/superapp/users/login/{superapp}/{email}"
                        , UserBoundary.class
                        , springApplicationName
                        , email);
    }

    /**
     * PUT
     * Helper method to update a user
     * the path is "/superapp/users/{superapp}/{email}"
     *
     * @param userBoundary - the user to update
     * @param email        - the email of the user we want to update
     */
    public void help_PutUserBoundary(UserBoundary userBoundary, String email) {
        this.restTemplate.put(
                this.baseUrl + "/superapp/users/{superapp}/{email}"
                , userBoundary, springApplicationName
                , email);
    }

    /**
     * DELETE
     * Helper method to delete a user
     * the path is "/superapp/admin/users"
     */
    public void help_DeleteUsersBoundary(String email) {
        this.restTemplate.delete(this.baseUrl +
                "/superapp/admin/users?userSuperapp={superapp}&userEmail={email}",
                springApplicationName, email);
    }

    /**
     * GET_ALL
     * Helper method to get all users
     * the path is "/superapp/admin/users
     *
     * @return all users - UserBoundary[]
     */
    public UserBoundary[] help_GetAllUsersBoundary(String email) {
        return this.restTemplate.getForObject(this.baseUrl + "/superapp/admin/users?userSuperapp={superapp}&" +
                        "userEmail={email}&size={size}&page={page}",
                UserBoundary[].class,
                springApplicationName, email, 15, 0);
    }


    // ###################################### Commands ######################################

    /**
     * POST
     *
     * @param miniAppName - MiniAppNames
     * @param commandId - CommandId
     * @param command - String
     * @param targetObject - TargetObject
     * @param createdTimestamp - Date
     * @param invokedBy - InvokedBy
     * @param commandAttributes - Map<String, Object>
     */
    public Object help_PostCommandBoundary(String miniAppName, CommandId commandId, String command,
                                         TargetObject targetObject, Date createdTimestamp, InvokedBy invokedBy,
                                         Map<String, Object> commandAttributes) {

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(commandId)
                .setCommand(command)
                .setTargetObject(targetObject)
                .setInvocationTimestamp(createdTimestamp)
                .setInvokedBy(invokedBy)
                .setCommandAttributes(commandAttributes);

        Object result = this.restTemplate
                                    .postForObject(
                                            this.baseUrl + "/superapp/miniapp/{miniAppName}?async={asyncFlag}"
                                            , commandBoundary
                                            , MiniAppCommandBoundary.class
                                            , miniAppName.toString()
                                            , null
                                    );

        return  result;
    }



    /**
     * DELETE
     * Helper method to delete all commands
     * the path is "/superapp/admin/miniapp?userSuperapp={superapp}&userEmail={email}"
     */
    public void help_DeleteCommands(String userSuperapp, String userEmail) {

        this.restTemplate
                .delete(this.baseUrl + "/superapp/admin/miniapp?userSuperapp={superapp}&userEmail={email}",
                        userSuperapp, userEmail);

    }

    /**
     * GET
     * Helper method to get all commands history of  miniapp
     * the path is "/superapp/admin/miniapp?userSuperapp={superapp}&userEmail={email}&size={size}&page={page}"
     *
     * @return MiniAppCommandBoundary[]
     */
    public MiniAppCommandBoundary[] help_GetAllMiniappCommands(String userSuperapp, String userEmail, String size, String page) {

        return this.restTemplate.getForObject(this.baseUrl
                        + "/superapp/admin/miniapp?userSuperapp={superapp}&userEmail={email}&size={size}&page={page}",
                MiniAppCommandBoundary[].class, userSuperapp, userEmail, size, page);

    }

    /**
     * GET
     * Helper method to get command history of specific miniapp
     * the path is "/superapp/admin/miniapp/{miniAppName}"
     *
     * @param miniAppName - MiniAppNames
     * @return MiniAppCommandBoundary[]
     */
    public MiniAppCommandBoundary[] help_GetSpecificMiniappCommands(String miniAppName, String userSuperapp, String userEmail, String size, String page) {

        return this.restTemplate.getForObject(
                this.baseUrl + "/superapp/admin/miniapp/{miniAppName}?userSuperapp={superapp}&userEmail={email}&size={size}&page={page}",
                MiniAppCommandBoundary[].class, miniAppName, userSuperapp, userEmail, size, page);

    }

}
