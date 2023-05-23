package superapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import superapp.data.UserDetails;
import superapp.logic.boundaries.*;
import superapp.miniapps.MiniAppNames;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommandTestSet extends BaseTestSet {

    private String idForCommandWithoutTarget = "EMPTY_OBJECT_FOR_COMMAND_THAT_NO_TARGET";

    private String admin = "ADMIN";
    private String miniappRole = "MINIAPP_USER";
    private String superappRole = "SUPERAPP_USER";

    private UserBoundary createUser(String email, String role) {

        String username = "demo";
        String avatar = "demo";
        return help_PostUserBoundary(email, role, username, avatar);
    }

    private SuperAppObjectBoundary createObject(String type, String email, Map<String, Object> attributes) {

        help_PutUserBoundary(new UserBoundary().setRole(superappRole), email);
        ObjectId objectId = new ObjectId();

        String alias = "demo name";
        boolean active = true;
        Date createdTimestamp = new Date();
        Location location = new Location().setLat(0.0).setLng(0.0);

        CreatedBy createdBy = new CreatedBy().setUserId(new UserId().setSuperapp(springApplicationName).setEmail(email));

        // post object
        return help_PostObjectBoundary(objectId, type, alias, createdTimestamp, active, location, createdBy, attributes);
    }

    private void createCommand(String email, String role, String miniAppName, String command,
                               TargetObject targetObject, Map<String, Object> commandAttributes) {

        // control of role
        help_PutUserBoundary(new UserBoundary().setRole(role), email);

        InvokedBy invokedBy = new InvokedBy()
                .setUserId(new UserId().setSuperapp(this.springApplicationName).setEmail(email));


        this.help_PostCommandBoundary(miniAppName, new CommandId(), command, targetObject, null,
                invokedBy, commandAttributes);
    }


    private void changeRole(String role, String email) {
        help_PutUserBoundary(new UserBoundary().setRole(role), email);
    }

    @Test
    @DisplayName("Delete all commands from full DB")
    public void testSuccessfulDeleteAllFullDB() {

        String email = "demo@gmail.com";
        // create users
        UserBoundary userBoundary = createUser(email, admin);

        //GIVEN The server is up
        //AND db is up
        //AND command objects exist in db

        String miniAppName = "MARKETPLACE";
        String command = "command ";
        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, idForCommandWithoutTarget));

        createCommand(email, miniappRole, miniAppName, command, targetObject, new HashMap<>());

        createCommand(email, miniappRole, miniAppName, "command 2", targetObject, new HashMap<>());


        help_PutUserBoundary(new UserBoundary().setRole(admin), email);

        assertThat(help_GetAllMiniappCommands(this.springApplicationName, email, null, null))
                .isNotNull()
                .isNotEmpty()
                .hasSize(2);

        //WHEN a delete request is made to the path "/superapp/admin/miniapp?userSuperapp={superapp}&userEmail={email}"

        //THEN the db response with the status 2xx code
        //AND commands objects no longer exist

        this.help_DeleteCommands(this.springApplicationName, email);


        assertThat(help_GetAllMiniappCommands(this.springApplicationName, email, null, null))
                .isNotNull()
                .isEmpty();

    }


    @Test
    @DisplayName("Delete all commands when commands collection is empty")
    public void testSuccessfulDeleteAllEmptyDB() {

        //GIVEN The server is up
        //AND db is up
        //AND commands objects don't exist in db

        // create users
        String email = "demo@gmail.com";
        createUser(email, admin);
        this.help_DeleteCommands(this.springApplicationName, email);

        //WHEN a delete request is made to the path /superapp/admin/miniapp
        //THEN the db response with the status 2xx code
        //AND commands objects no longer exist
        assertThat(help_GetAllMiniappCommands(this.springApplicationName, email, null, null))
                .isEmpty();

    }


    @Test
    @DisplayName("Delete all commands history with illegal path")
    public void testSuccessfulDeleteOfAllHistoryCommandsIllegalPath() {

        // GIVEN The server is up
        // AND
        // database is up
        // AND
        // any commands objects exists in database


        String email = "demo@gmail.com";
        createUser(email, admin);

        String miniAppName = "MARKETPLACE";
        String command = "command ";
        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, idForCommandWithoutTarget));

        createCommand(email, miniappRole, miniAppName, command, targetObject, new HashMap<>());

        //WHEN illegal path of delete request is made
        //THEN The server response with status 4xx code
        //AND
        //commands objects are not deleted

        changeRole(admin, email);
        assertThatThrownBy(() -> this.restTemplate
                .delete(this.baseUrl + "/superapp/admin/NOT_VALID_MINIAPP?userSuperapp={superapp}&userEmail={email}", this.springApplicationName, email))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.NOT_FOUND.value());


        assertThat(help_GetAllMiniappCommands(this.springApplicationName, email, null, null))
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    @DisplayName("Get command history of specific miniapp - no history")
    public void testSuccessfulGetCommandHistoryOfSpecificMiniappNoHistory() {

        //  GIVEN The server is up
//        database is up
//        commands objects of a chosen ""miniAppName"" do NOT exist in database
//        commands objects of any other miniapps exists in database


        String email = "demo@gmail.com";
        createUser(email, admin);

        String command = "command ";
        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, idForCommandWithoutTarget));

        createCommand(email, miniappRole, "DATING", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "DATING", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "EVENT", command, targetObject, new HashMap<>());
        //  WHEN A GET request is made to the path "superapp/admin/miniapp/{miniAppName}"

        //  THEN The server response with status 2xx code
        changeRole(admin, email);

        assertThat(help_GetSpecificMiniappCommands("MARKETPLACE", this.springApplicationName, email, null, null))
                .isNotNull()
                .isEmpty();

        assertThat(help_GetAllMiniappCommands(this.springApplicationName, email, null, null))
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    @DisplayName("Get command history of specific miniapp")
    public void testSuccessfulGetCommandHistoryOfSpecificMiniapp() {
        // GIVEN The server is up
//        AND
//        database is up
//        AND
//        commands objects of a chosen ""miniAppName"" do exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        String command = "command ";
        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, idForCommandWithoutTarget));

        createCommand(email, miniappRole, "DATING", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "DATING", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "EVENT", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "EVENT", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "MARKETPLACE", command, targetObject, new HashMap<>());

        // WHEN A GET request is made to the path "superapp/admin/miniapp/{miniAppName}"

        // THEN The server response with status 2xx code

        changeRole(admin, email);
        assertThat(help_GetSpecificMiniappCommands("DATING", this.springApplicationName, email, null, null))
                .isNotNull()
                .isNotEmpty()
                .hasSize(2);

        assertThat(help_GetSpecificMiniappCommands("EVENT", this.springApplicationName, email, null, null))
                .isNotNull()
                .isNotEmpty()
                .hasSize(2);
    }

    @Test
    @DisplayName("Get all commands history of specific miniapp with illegal path")
    public void testSuccessfulGetAllHistoryCommandsSpecificMiniappIllegalPath() {


//        GIVEN The server is up
//        AND
//        database is up
//        AND
//        any commands objects exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        String command = "command ";
        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, idForCommandWithoutTarget));

        createCommand(email, miniappRole, "DATING", command, targetObject, new HashMap<>());

        //WHEN illegal path of GET request is made
        //THEN The server response with status 4xx code
        //AND
        //commands objects are not retrieved
        changeRole(admin, email);
        assertThatThrownBy(() -> this.restTemplate
                .getForObject((this.baseUrl + "/superapp/admin/NOT_VALID/{miniAppName}"),
                        MiniAppCommandBoundary[].class,
                        "DATING", this.springApplicationName, email, null, null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Get command history of all miniapps")
    public void testSuccessfulGetCommandHistoryOfMiniapps() {
        //createCommand();

//        GIVEN The server is up
//        database is up
//        commands objects of 2 miniapps exists in database
        String email = "demo@gmail.com";
        createUser(email, admin);

        String command = "command ";
        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, idForCommandWithoutTarget));

        createCommand(email, miniappRole, "DATING", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "DATING", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "EVENT", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "EVENT", command, targetObject, new HashMap<>());

        // WHEN A GET request is made to the path "superapp/admin/miniapp"

        //       THEN The server response with status 2xx code


        changeRole(admin, email);
        assertThat(help_GetAllMiniappCommands(this.springApplicationName, email, null, null))
                .isNotNull()
                .isNotEmpty()
                .hasSize(4);
    }


    @Test
    @DisplayName("Get command history of all miniapps when db is empty")
    public void testSuccessfulGetCommandHistoryOfMiniappsDbEmpty() {

        // GIVEN The server is up
        // database is up
        // Any commands objects dose NOT exists in database
        String email = "demo@gmail.com";
        createUser(email, admin);

        //WHEN A GET request is made to the path "superapp/admin/miniapp"
        //THEN The server response with status 2xx code

        assertThat(help_GetAllMiniappCommands(this.springApplicationName, email, null, null))
                .isNotNull()
                .isEmpty();
    }


    @Test
    @DisplayName("Create command by invoke func and the return object")
    public void testSuccessfulPostOfCommandEntityByInvokeFunc() {
//        UserBoundary userBoundary = createUser();
//        SuperAppObjectBoundary superAppObjectBoundary = createObject(userBoundary.getUserId().getEmail());
//        //GIVEN The server is up
//        //AND
//        //database is up
//        //AND
//        // user exists in database
//        //AND
//        // object exists in database
//
//        //WHEN A POST request is made to the path
//        MiniAppNames miniAppName = MiniAppNames.EVENT;
//        CommandId commandId = new CommandId();
//        String command = "DO_SOMETHING";
//        TargetObject targetObject = new TargetObject().setObjectId(superAppObjectBoundary.getObjectId());
//        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setSuperapp(springApplicationName).setEmail(userBoundary.getUserId().getEmail()));
//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put("demo", "demo");
//        MiniAppCommandBoundary miniAppCommandBoundary = new MiniAppCommandBoundary()
//                .setCommand(command)
//                .setCommandId(commandId)
//                .setTargetObject(targetObject)
//                .setInvokedBy(invokedBy)
//                .setCommandAttributes(attributes);
//
//        this.restTemplate.postForObject((this.baseUrl + "/superapp/miniapp/{miniAppName}"), miniAppCommandBoundary, MiniAppCommandBoundary.class, miniAppName);
//        //THEN The server response with status 2xx code

    }

    @Test
    @DisplayName("Create command by invoke with invalid path")
    public void testSuccessfulPostOfCommandEntityByInvokeFuncInvalidPath() {

        // GIVEN The server is up
        // database is up
        // user exists in database
        // object exists in database
        String email = "demo@gmail.com";
        createUser(email, admin);

        UserDetails userDetails = new UserDetails().setName("name").setPhoneNum("052-5762230");


        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("key", userDetails);

        changeRole(superappRole, email);
        SuperAppObjectBoundary superAppObjectBoundary = this.help_PostObjectBoundary(new ObjectId(),
                "UserDetails",
                "alias",
                null,
                true,
                new Location(0.0, 0.0),
                new CreatedBy().setUserId(new UserId().setEmail(email).setSuperapp(this.springApplicationName)),
                objectDetails);

        //WHEN A POST request is made to the path "/superapp/MINIAPP/{miniAppName}?async={asyncFlag}"

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(null)
                .setCommand("command")
                .setTargetObject(new TargetObject().setObjectId(new ObjectId(this.springApplicationName, idForCommandWithoutTarget)))
                .setInvocationTimestamp(null)
                .setInvokedBy( new InvokedBy().setUserId(superAppObjectBoundary.getCreatedBy().getUserId()))
                .setCommandAttributes(new HashMap<>());



        changeRole(miniappRole, email);

        //THEN The server response with status 4xx code
        assertThatThrownBy(() ->  this.restTemplate
                .postForObject(this.baseUrl + "/superapp/miniappp/{miniAppName}?async={asyncFlag}"
                                , commandBoundary
                                , MiniAppCommandBoundary.class
                                , MiniAppNames.DATING.name()
                                , null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.NOT_FOUND.value());
        //this.restTemplate.postForEntity((this.baseUrl + "/superapp/miniapp/{miniAppName}"), miniAppCommandBoundary, MiniAppCommandBoundary.class, "miniAppName");

    }

    @Test
    @DisplayName("Create command entity by invoke when command not exists")
    public void testSuccessfulPostOfCommandEntityByInvokeFuncCommandNotExists() {
//        UserBoundary userBoundary = createUser();
//        SuperAppObjectBoundary superAppObjectBoundary = createObject(userBoundary.getUserId().getEmail());
//        //GIVEN The server is up
//        //AND
//        //database is up
//        //AND
//        // user exists in database
//        //AND
//        // object exists in database
//
//        //WHEN A POST request is made to the path
//        CommandId commandId = new CommandId();
//        String command = "NOT_EXISTS_COMMAND";
//        TargetObject targetObject = new TargetObject().setObjectId(superAppObjectBoundary.getObjectId());
//        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setSuperapp(springApplicationName).setEmail(userBoundary.getUserId().getEmail()));
//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put("demo", "demo");
//        MiniAppCommandBoundary miniAppCommandBoundary = new MiniAppCommandBoundary()
//                .setCommand(command)
//                .setCommandId(commandId)
//                .setTargetObject(targetObject)
//                .setInvokedBy(invokedBy)
//                .setCommandAttributes(attributes);
//
//
//        //THEN The server response with status 2xx code
//        this.restTemplate.postForObject((this.baseUrl + "/superapp/miniapp/{miniAppName}"), miniAppCommandBoundary, MiniAppCommandBoundary.class, MiniAppNames.EVENT);
    }

    @Test
    @DisplayName("Create command by invoke with command=null")
    public void testSuccessfulPostOfCommandEntityByInvokeFuncWithCommandNullValue() {
//        UserBoundary userBoundary = createUser();
//        SuperAppObjectBoundary superAppObjectBoundary = createObject(userBoundary.getUserId().getEmail());
//        //GIVEN The server is up
//        //AND
//        //database is up
//        //AND
//        // user exists in database
//        //AND
//        // object exists in database
//
//        //WHEN A POST request is made to the path
//        CommandId commandId = new CommandId();
//        String command = null;
//        TargetObject targetObject = new TargetObject().setObjectId(superAppObjectBoundary.getObjectId());
//        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setSuperapp(springApplicationName).setEmail(userBoundary.getUserId().getEmail()));
//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put("demo", "demo");
//        MiniAppCommandBoundary miniAppCommandBoundary = new MiniAppCommandBoundary()
//                .setCommand(command)
//                .setCommandId(commandId)
//                .setTargetObject(targetObject)
//                .setInvokedBy(invokedBy)
//                .setCommandAttributes(attributes);

    }


    @Test
    @DisplayName("Create command entity with no existing targetObject in database")
    public void testSuccessfulPostOfCommandEntityByInvokeFuncTargetObjectNotExisting() {

    }

}
