package superapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import superapp.data.UserDetails;
import superapp.logic.boundaries.*;
import superapp.miniapps.MiniAppNames;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommandTestSet extends BaseTestSet {


    private final String admin = "ADMIN";
    private final String miniappRole = "MINIAPP_USER";
    private final String superappRole = "SUPERAPP_USER";

    private UserBoundary createUser(String email, String role) {

        String username = "demo";
        String avatar = "demo";
        return help_PostUserBoundary(email, role, username, avatar);
    }

    private Object createCommand(String email, String role, String miniAppName, String command,
                               TargetObject targetObject, Map<String, Object> commandAttributes) {

        // control of role
        help_PutUserBoundary(new UserBoundary().setRole(role), email);

        InvokedBy invokedBy = new InvokedBy()
                .setUserId(new UserId().setSuperapp(this.springApplicationName).setEmail(email));


        return this.help_PostCommandBoundary(miniAppName, new CommandId(), command, targetObject, null,
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
        createUser(email, admin);

        //GIVEN The server is up
        //AND db is up
        //AND command objects exist in db

        String miniAppName = "MARKETPLACE";
        String command = "command ";
        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand));

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
                .setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand));

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
                .setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand));

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
    @DisplayName("Get command history of specific miniapp that not exist in our miniapp")
    public void testGetCommandHistoryOfSpecificMiniappNotInOurMiniapp() {

        // GIVEN The server is up
        // database is up
        // db contain commands objects of any 2 miniapps exists and 1 command of unknown miniapp


        String email = "demo@gmail.com";
        createUser(email, admin);

        String command = "command ";
        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand));

        createCommand(email, miniappRole, "BLA", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "DATING", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "EVENT", command, targetObject, new HashMap<>());
        //  WHEN A GET request is made to the path "superapp/admin/miniapp/{BLA}"

        //  THEN The server response with status 2xx code
        changeRole(admin, email);

        assertThat(help_GetSpecificMiniappCommands("BLA", this.springApplicationName, email, null, null))
                .isNotNull()
                .isNotEmpty().hasSize(1);

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
                .setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand));

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
                .setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand));

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
                .setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand));

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
    @DisplayName("Invoke command successfully")
    public void testSuccessfulPostOfCommandEntityByInvokeFunc() {
        //GIVEN The server is up
        //database is up
        // user exists in database
        // object exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        UserDetails userDetails = new UserDetails().setName("Yosef").setPhoneNum("052-5762230");
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("key", userDetails);

        changeRole(superappRole, email);

        this.help_PostObjectBoundary(new ObjectId(),
                "USER_DETAILS",
                "alias",
                null,
                true,
                new Location(0.0, 0.0),
                new CreatedBy().setUserId(new UserId().setEmail(email).setSuperapp(this.springApplicationName)),
                objectDetails);

        //WHEN A POST request is made to the path

        String command = "GET_USER_DETAILS_BY_EMAIL";

        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand));

        createCommand(email, miniappRole, "DATING", command, targetObject, new HashMap<>());

        //THEN The server response with status 2xx code and command stored in db
        // and return SuperAppObjectBoundary with UserDetails in object details

        changeRole(admin, email);
        assertThat(help_GetAllMiniappCommands(this.springApplicationName, email, null, null))
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);


        // TODO - complete - solution for extract data from map
//        CommandId commandId = Arrays
//                .stream(help_GetSpecificMiniappCommands("DATING", this.springApplicationName, email, null, null))
//                .findAny()
//                .get()
//                .getCommandId();
//
//        Object boundary = ((Map<String, Object>) commandRes)
//                .get(ConvertHelp.concatenateIds(new String[] {commandId.getSuperapp(), commandId.getMiniapp(), commandId.getInternalCommandId()}));
//
//        assertThat(boundary)
//                .isNotNull()
//                .usingRecursiveComparison()
//                .isEqualTo(postedObject);

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
                "USER_DETAILS",
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
                .setTargetObject(new TargetObject().setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand)))
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

        //GIVEN The server is up
        //database is up
        // user exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        //WHEN A POST request is made to the path
        String command = "NOT_EXISTS_COMMAND";

        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand));

        Object commandRes = createCommand(email, miniappRole, "DATING", command, targetObject, new HashMap<>());

        //THEN The server response with status 2xx code and command stored in db

        changeRole(admin, email);
        assertThat(help_GetAllMiniappCommands(this.springApplicationName, email, null, null))
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        assertThat(commandRes)
                .isNotNull()
                .isInstanceOf(Map.class);

    }

    @Test
    @DisplayName("Create command by invoke with command : null")
    public void testSuccessfulPostOfCommandEntityByInvokeFuncWithCommandNullValue() {

        //GIVEN The server is up
        //database is up
        // user exists in database

        //WHEN A POST request is made to the path
        String email = "demo@gmail.com";
        createUser(email, admin);

        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand));

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(null)
                .setCommand(null)
                .setTargetObject(targetObject)
                .setInvocationTimestamp(null)
                .setInvokedBy(new InvokedBy().setUserId( new UserId(this.springApplicationName, email)))
                .setCommandAttributes(new HashMap<>());


        // THEN
        // response with bad request status
        changeRole(miniappRole, email);


        assertThatThrownBy(() ->  this.restTemplate
                                        .postForObject(this.baseUrl + "/superapp/miniapp/{miniAppName}?async={asyncFlag}"
                                                , commandBoundary
                                                , Object.class
                                                , "EVENT"
                                                , null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());

        changeRole(admin, email);
        assertThat(help_GetAllMiniappCommands(this.springApplicationName, email, null, null))
                .isNotNull()
                .isEmpty();

    }

    @Test
    @DisplayName("Create command with non existing targetObject in database")
    public void testSuccessfulInvokeCommandOnNonExistingTargetObject() {
        // GIVEN The server is up
        // database is up
        // user exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        //WHEN A POST request is made to the path
        String existingCommand = "LIKE_PROFILE";

        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, "ASDSRTGWERGTEWRF"));

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(null)
                .setCommand(existingCommand)
                .setTargetObject(targetObject)
                .setInvocationTimestamp(null)
                .setInvokedBy(new InvokedBy().setUserId( new UserId(this.springApplicationName, email)))
                .setCommandAttributes(new HashMap<>());

        //THEN The server response with status 4xx code
        assertThatThrownBy(() ->  this.restTemplate
                .postForObject(this.baseUrl + "/superapp/miniapp/{miniAppName}?async={asyncFlag}"
                        , commandBoundary
                        , MiniAppCommandBoundary.class
                        , MiniAppNames.DATING.name()
                        , null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Create command with targetObject null")
    public void testSuccessfulInvokeCommandOnTargetObjectNull() {
        // GIVEN The server is up
        // database is up
        // user exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        //WHEN A POST request is made to the path
        String existingCommand = "LIKE_PROFILE";

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(null)
                .setCommand(existingCommand)
                .setTargetObject(null)
                .setInvocationTimestamp(null)
                .setInvokedBy(new InvokedBy().setUserId( new UserId(this.springApplicationName, email)))
                .setCommandAttributes(new HashMap<>());

        //THEN The server response with status 4xx code
        assertThatThrownBy(() ->  this.restTemplate
                .postForObject(this.baseUrl + "/superapp/miniapp/{miniAppName}?async={asyncFlag}"
                        , commandBoundary
                        , MiniAppCommandBoundary.class
                        , MiniAppNames.DATING.name()
                        , null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Create command with null internal object id in targetObject")
    public void testSuccessfulInvokeCommandWithTargetObjectWithNullInternalObjectId() {
        // GIVEN The server is up
        // database is up
        // user exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        //WHEN A POST request is made to the path
        String existingCommand = "LIKE_PROFILE";

        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, null));

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(null)
                .setCommand(existingCommand)
                .setTargetObject(targetObject)
                .setInvocationTimestamp(null)
                .setInvokedBy(new InvokedBy().setUserId( new UserId(this.springApplicationName, email)))
                .setCommandAttributes(new HashMap<>());

        //THEN The server response with status 4xx code
        assertThatThrownBy(() ->  this.restTemplate
                .postForObject(this.baseUrl + "/superapp/miniapp/{miniAppName}?async={asyncFlag}"
                        , commandBoundary
                        , MiniAppCommandBoundary.class
                        , MiniAppNames.DATING.name()
                        , null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Create command with empty internal object id in targetObject")
    public void testSuccessfulInvokeCommandWithTargetObjectWithEmptyInternalObjectId() {
        // GIVEN The server is up
        // database is up
        // user exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        //WHEN A POST request is made to the path
        String existingCommand = "LIKE_PROFILE";

        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, ""));

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(null)
                .setCommand(existingCommand)
                .setTargetObject(targetObject)
                .setInvocationTimestamp(null)
                .setInvokedBy(new InvokedBy().setUserId( new UserId(this.springApplicationName, email)))
                .setCommandAttributes(new HashMap<>());

        //THEN The server response with status 4xx code
        assertThatThrownBy(() ->  this.restTemplate
                .postForObject(this.baseUrl + "/superapp/miniapp/{miniAppName}?async={asyncFlag}"
                        , commandBoundary
                        , MiniAppCommandBoundary.class
                        , MiniAppNames.DATING.name()
                        , null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Create command with null spring application name in targetObject")
    public void testSuccessfulInvokeCommandWithTargetObjectWithNullSpringApplicationName() {
        // GIVEN The server is up
        // database is up
        // user exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        //WHEN A POST request is made to the path
        String existingCommand = "LIKE_PROFILE";

        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(null, this.internalObjectIdForCommand));

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(null)
                .setCommand(existingCommand)
                .setTargetObject(targetObject)
                .setInvocationTimestamp(null)
                .setInvokedBy(new InvokedBy().setUserId( new UserId(this.springApplicationName, email)))
                .setCommandAttributes(new HashMap<>());

        //THEN The server response with status 4xx code
        assertThatThrownBy(() ->  this.restTemplate
                .postForObject(this.baseUrl + "/superapp/miniapp/{miniAppName}?async={asyncFlag}"
                        , commandBoundary
                        , MiniAppCommandBoundary.class
                        , MiniAppNames.DATING.name()
                        , null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Create command with empty spring application name in targetObject")
    public void testSuccessfulInvokeCommandWithTargetObjectWithEmptySpringApplicationName() {
        // GIVEN The server is up
        // database is up
        // user exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        //WHEN A POST request is made to the path
        String existingCommand = "LIKE_PROFILE";

        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId("", this.internalObjectIdForCommand));

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(null)
                .setCommand(existingCommand)
                .setTargetObject(targetObject)
                .setInvocationTimestamp(null)
                .setInvokedBy(new InvokedBy().setUserId( new UserId(this.springApplicationName, email)))
                .setCommandAttributes(new HashMap<>());

        //THEN The server response with status 4xx code
        assertThatThrownBy(() ->  this.restTemplate
                .postForObject(this.baseUrl + "/superapp/miniapp/{miniAppName}?async={asyncFlag}"
                        , commandBoundary
                        , MiniAppCommandBoundary.class
                        , MiniAppNames.DATING.name()
                        , null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Invoke command with invoke by null")
    public void testSuccessfulInvokeCommandWithInvokeByNull() {
        // GIVEN The server is up
        // database is up
        // user exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        //WHEN A POST request is made to the path
        String existingCommand = "LIKE_PROFILE";

        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand));

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(null)
                .setCommand(existingCommand)
                .setTargetObject(targetObject)
                .setInvocationTimestamp(null)
                .setInvokedBy(null)
                .setCommandAttributes(new HashMap<>());

        //THEN The server response with status 4xx code
        assertThatThrownBy(() ->  this.restTemplate
                .postForObject(this.baseUrl + "/superapp/miniapp/{miniAppName}?async={asyncFlag}"
                        , commandBoundary
                        , MiniAppCommandBoundary.class
                        , MiniAppNames.DATING.name()
                        , null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Invoke command with invoke by with null spring application name")
    public void testSuccessfulInvokeCommandWithInvokeByNullWithNullSpringApplicationName() {
        // GIVEN The server is up
        // database is up
        // user exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        //WHEN A POST request is made to the path
        String existingCommand = "LIKE_PROFILE";

        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand));

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(null)
                .setCommand(existingCommand)
                .setTargetObject(targetObject)
                .setInvocationTimestamp(null)
                .setInvokedBy(new InvokedBy().setUserId( new UserId(null, email)))
                .setCommandAttributes(new HashMap<>());

        //THEN The server response with status 4xx code
        assertThatThrownBy(() ->  this.restTemplate
                .postForObject(this.baseUrl + "/superapp/miniapp/{miniAppName}?async={asyncFlag}"
                        , commandBoundary
                        , MiniAppCommandBoundary.class
                        , MiniAppNames.DATING.name()
                        , null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Invoke command with invoke by with empty spring application name")
    public void testSuccessfulInvokeCommandWithInvokeByWithEmptySpringApplicationName() {
        // GIVEN The server is up
        // database is up
        // user exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        //WHEN A POST request is made to the path
        String existingCommand = "LIKE_PROFILE";

        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand));

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(null)
                .setCommand(existingCommand)
                .setTargetObject(targetObject)
                .setInvocationTimestamp(null)
                .setInvokedBy(new InvokedBy().setUserId( new UserId("", email)))
                .setCommandAttributes(new HashMap<>());

        //THEN The server response with status 4xx code
        assertThatThrownBy(() ->  this.restTemplate
                .postForObject(this.baseUrl + "/superapp/miniapp/{miniAppName}?async={asyncFlag}"
                        , commandBoundary
                        , MiniAppCommandBoundary.class
                        , MiniAppNames.DATING.name()
                        , null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Invoke command with invoke by with null email")
    public void testSuccessfulInvokeCommandWithInvokeByWithNullEmail() {
        // GIVEN The server is up
        // database is up
        // user exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        //WHEN A POST request is made to the path
        String existingCommand = "LIKE_PROFILE";

        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand));

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(null)
                .setCommand(existingCommand)
                .setTargetObject(targetObject)
                .setInvocationTimestamp(null)
                .setInvokedBy(new InvokedBy().setUserId( new UserId(this.springApplicationName, null)))
                .setCommandAttributes(new HashMap<>());

        //THEN The server response with status 4xx code
        assertThatThrownBy(() ->  this.restTemplate
                .postForObject(this.baseUrl + "/superapp/miniapp/{miniAppName}?async={asyncFlag}"
                        , commandBoundary
                        , MiniAppCommandBoundary.class
                        , MiniAppNames.DATING.name()
                        , null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Invoke command with invoke by with empty email")
    public void testSuccessfulInvokeCommandWithInvokeByWithEmptyEmail() {
        // GIVEN The server is up
        // database is up
        // user exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        //WHEN A POST request is made to the path
        String existingCommand = "LIKE_PROFILE";

        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand));

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(null)
                .setCommand(existingCommand)
                .setTargetObject(targetObject)
                .setInvocationTimestamp(null)
                .setInvokedBy(new InvokedBy().setUserId( new UserId(this.springApplicationName, "")))
                .setCommandAttributes(new HashMap<>());

        //THEN The server response with status 4xx code
        assertThatThrownBy(() ->  this.restTemplate
                .postForObject(this.baseUrl + "/superapp/miniapp/{miniAppName}?async={asyncFlag}"
                        , commandBoundary
                        , MiniAppCommandBoundary.class
                        , MiniAppNames.DATING.name()
                        , null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Invoke command with MiniAppName null")
    public void testSuccessfulInvokeCommandWithNullMiniAppName() {
        // GIVEN The server is up
        // database is up
        // user exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        //WHEN A POST request is made to the path
        String existingCommand = "LIKE_PROFILE";

        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand));

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(null)
                .setCommand(existingCommand)
                .setTargetObject(targetObject)
                .setInvocationTimestamp(null)
                .setInvokedBy(new InvokedBy().setUserId( new UserId(this.springApplicationName, email)))
                .setCommandAttributes(new HashMap<>());

        //THEN The server response with status 4xx code
        assertThatThrownBy(() ->  this.restTemplate
                .postForObject(this.baseUrl + "/superapp/miniapp/{miniAppName}?async={asyncFlag}"
                        , commandBoundary
                        , MiniAppCommandBoundary.class
                        , null
                        , null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Invoke command with MiniAppName empty")
    public void testSuccessfulInvokeCommandWithEmptyMiniAppName() {
        // GIVEN The server is up
        // database is up
        // user exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        //WHEN A POST request is made to the path
        String existingCommand = "LIKE_PROFILE";

        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand));

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(null)
                .setCommand(existingCommand)
                .setTargetObject(targetObject)
                .setInvocationTimestamp(null)
                .setInvokedBy(new InvokedBy().setUserId( new UserId(this.springApplicationName, email)))
                .setCommandAttributes(new HashMap<>());

        //THEN The server response with status 4xx code
        assertThatThrownBy(() ->  this.restTemplate
                .postForObject(this.baseUrl + "/superapp/miniapp/{miniAppName}?async={asyncFlag}"
                        , commandBoundary
                        , MiniAppCommandBoundary.class
                        , ""
                        , null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Invoke command with MiniAppName not exist")
    public void testSuccessfulInvokeCommandWitNotExistMiniAppName() {
        // GIVEN The server is up
        // database is up
        // user exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        //WHEN A POST request is made to the path
        String existingCommand = "LIKE_PROFILE";

        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand));

        Object commandRes = createCommand(email, miniappRole, "AAAA", existingCommand, targetObject, new HashMap<>());

        //THEN The server response with status 2xx code
        changeRole(admin, email);
        assertThat(help_GetAllMiniappCommands(this.springApplicationName, email, null, null))
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        assertThat(commandRes)
                .isNotNull()
                .isInstanceOf(Map.class);
    }

    @Test
    @DisplayName("Invoke command on object with active is false")
    public void testSuccessfulInvokeCommandWithObjectWithActiveIsFalse() {
        // GIVEN The server is up
        // database is up
        // user exists in database

        String email = "demo@gmail.com";
        createUser(email, superappRole);

        SuperAppObjectBoundary object =  help_PostObjectBoundary(null, "type", "alias", null, false, null,
                new CreatedBy().setUserId( new UserId(this.springApplicationName, email)), null);

        //WHEN A POST request is made to the path
        String existingCommand = "LIKE_PROFILE";

        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, object.getObjectId().getInternalObjectId()));

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(null)
                .setCommand(existingCommand)
                .setTargetObject(targetObject)
                .setInvocationTimestamp(null)
                .setInvokedBy(new InvokedBy().setUserId( new UserId(this.springApplicationName, email)))
                .setCommandAttributes(new HashMap<>());

        //THEN The server response with status 4xx code
        assertThatThrownBy(() ->  this.restTemplate
                .postForObject(this.baseUrl + "/superapp/miniapp/{miniAppName}?async={asyncFlag}"
                        , commandBoundary
                        , MiniAppCommandBoundary.class
                        , MiniAppNames.DATING.name()
                        , null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Get all commands with valid and invalid pagination values")
    public void testPaginationSupportInGetAllCommands(){
        //  GIVEN The server is up
        // database is up
        // 10 commands objects of any  miniapps exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        String command = "command ";
        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand));

        createCommand(email, miniappRole, "DATING", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "DATING", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "MARKETPLACE", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "MARKETPLACE", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "MARKETPLACE", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "EVENT", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "EVENT", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "EVENT", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "DATING", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "DATING", command, targetObject, new HashMap<>());
        //  WHEN A GET request is made to the path "superapp/admin/miniapp/{miniAppName}"

        //  THEN The server response with status 2xx code
        changeRole(admin, email);
        assertThat(help_GetAllMiniappCommands( this.springApplicationName, email, "4", "0"))
                .isNotNull()
                .isNotEmpty()
                .hasSize(4);

        assertThat(help_GetAllMiniappCommands( this.springApplicationName, email, "4", "1"))
                .isNotNull()
                .isNotEmpty()
                .hasSize(4);

        assertThat(help_GetAllMiniappCommands( this.springApplicationName, email, "4", "2"))
                .isNotNull()
                .isNotEmpty()
                .hasSize(2);

        assertThat(help_GetAllMiniappCommands( this.springApplicationName, email, "4", "3"))
                .isNotNull()
                .isEmpty();

        //  WHEN A GET request is made to the path "superapp/admin/miniapp/{miniAppName}" with invalid pagination values
        //  THEN The server response with status 4xx code
        assertThatThrownBy(() -> help_GetAllMiniappCommands( this.springApplicationName, email, "0", "4"))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());

        assertThatThrownBy(() -> help_GetAllMiniappCommands( this.springApplicationName, email, "-1", "4"))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());

        assertThatThrownBy(() -> help_GetAllMiniappCommands( this.springApplicationName, email, "?", "4"))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());

        assertThatThrownBy(() -> help_GetAllMiniappCommands( this.springApplicationName, email, "1.2", "4"))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());

        assertThatThrownBy(() -> help_GetAllMiniappCommands( this.springApplicationName, email, "4", "-1"))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());

        assertThatThrownBy(() -> help_GetAllMiniappCommands( this.springApplicationName, email, "4", "?"))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());

        assertThatThrownBy(() -> help_GetAllMiniappCommands( this.springApplicationName, email, "4", "1.2"))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @Test
    @DisplayName("Get all commands for specific miniapp with valid and invalid pagination values")
    public void testPaginationSupportInGetAllCommandsForSpecificMiniapp(){
        //  GIVEN The server is up
        // database is up
        // 10 commands objects of any  miniapps exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        String command = "command ";
        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand));

        createCommand(email, miniappRole, "DATING", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "DATING", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "MARKETPLACE", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "MARKETPLACE", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "MARKETPLACE", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "EVENT", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "EVENT", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "EVENT", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "DATING", command, targetObject, new HashMap<>());
        createCommand(email, miniappRole, "DATING", command, targetObject, new HashMap<>());
        //  WHEN A GET request is made to the path "superapp/admin/miniapp/{miniAppName}"

        //  THEN The server response with status 2xx code
        changeRole(admin, email);
        assertThat(help_GetSpecificMiniappCommands("DATING", this.springApplicationName, email, "1", "3"))
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        assertThat(help_GetSpecificMiniappCommands("DATING", this.springApplicationName, email, "100", "0"))
                .isNotNull()
                .isNotEmpty()
                .hasSize(4);


        assertThat(help_GetSpecificMiniappCommands("MARKETPLACE", this.springApplicationName, email, "2", "0"))
                .isNotNull()
                .isNotEmpty()
                .hasSize(2);
        assertThat(help_GetSpecificMiniappCommands("MARKETPLACE", this.springApplicationName, email, "2", "1"))
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        assertThat(help_GetSpecificMiniappCommands( "EVENT", this.springApplicationName, email, "3", "10"))
                .isNotNull()
                .isEmpty();

        //  WHEN A GET request is made to the path "superapp/admin/miniapp/{miniAppName}" with invalid pagination values
        //  THEN The server response with status 4xx code
        assertThatThrownBy(() -> help_GetSpecificMiniappCommands( "EVENT", this.springApplicationName, email, "0", "4"))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());

        assertThatThrownBy(() -> help_GetSpecificMiniappCommands( "EVENT",this.springApplicationName, email, "-1", "4"))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());

        assertThatThrownBy(() -> help_GetSpecificMiniappCommands( "EVENT",this.springApplicationName, email, "?", "4"))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());

        assertThatThrownBy(() -> help_GetSpecificMiniappCommands( "EVENT",this.springApplicationName, email, "1.2", "4"))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());

        assertThatThrownBy(() -> help_GetSpecificMiniappCommands( "EVENT",this.springApplicationName, email, "4", "-1"))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());

        assertThatThrownBy(() -> help_GetSpecificMiniappCommands( "EVENT",this.springApplicationName, email, "4", "?"))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());

        assertThatThrownBy(() -> help_GetSpecificMiniappCommands( "EVENT",this.springApplicationName, email, "4", "1.2"))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testAsyncCommandNotChangeTimestampAndId(){
        //GIVEN The server is up
        //database is up
        // user exists in database


        String email = "demo@gmail.com";
        createUser(email, admin);

        //WHEN A POST request is made to the path "/superapp/miniapp/{miniAppName}?async={true}"

        String command = "GET_USER"; // unknown command

        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId()
                        .setSuperapp(this.springApplicationName)
                        .setInternalObjectId(this.internalObjectIdForCommand));

        changeRole(miniappRole, email);

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(null)
                .setCommand(command)
                .setTargetObject(targetObject)
                .setInvocationTimestamp(null)
                .setInvokedBy(new InvokedBy().setUserId(new UserId(this.springApplicationName, email)))
                .setCommandAttributes(new HashMap<>());

        MiniAppCommandBoundary result = this.restTemplate
                .postForObject(
                        this.baseUrl + "/superapp/miniapp/{miniAppName}?async={asyncFlag}"
                        , commandBoundary
                        , MiniAppCommandBoundary.class
                        , "DATING"
                        , "true");

        assert result != null;

        CommandId commandIdBeforeExecution = result.getCommandId();

        Map<String, Object> statusJms = result.getCommandAttributes();

        changeRole(admin, email);
        assertThat(help_GetAllMiniappCommands(this.springApplicationName, email, null, null))
                .isNotNull()
                .isEmpty();

        assertThat(statusJms.get("status")).isNotNull().isEqualTo("in process");

        //THEN The server response with status 2xx code and command stored in db
        // and return SuperAppObjectBoundary with UserDetails in object details

        try {
            Thread.sleep(5000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        changeRole(admin, email);
        assertThat(help_GetAllMiniappCommands(this.springApplicationName, email, null, null))
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        List<MiniAppCommandBoundary> resultAfterExecution = List.of(this.help_GetAllMiniappCommands(this.springApplicationName, email, null, null));

        assertThat(commandIdBeforeExecution).usingRecursiveComparison().isEqualTo(resultAfterExecution.get(0).getCommandId());

    }


    @Test
    public void basicTestAsyncCommand(){
        //GIVEN The server is up
        //database is up
        // user exists in database
        // object exists in database

        String email = "demo@gmail.com";
        createUser(email, admin);

        UserDetails userDetails = new UserDetails().setName("Yosef").setPhoneNum("052-5762230");
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("key", userDetails);

        changeRole(superappRole, email);
        this.help_PostObjectBoundary(new ObjectId(),
                "USER_DETAILS",
                "alias",
                null,
                true,
                new Location(0.0, 0.0),
                new CreatedBy().setUserId(new UserId().setEmail(email).setSuperapp(this.springApplicationName)),
                objectDetails);

        //WHEN A POST request is made to the path

        String command = "GET_USER_DETAILS_BY_EMAIL";

        TargetObject targetObject = new TargetObject()
                .setObjectId(new ObjectId()
                        .setSuperapp(this.springApplicationName)
                        .setInternalObjectId(this.internalObjectIdForCommand));

        System.err.println(targetObject);
        changeRole(miniappRole, email);

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(null)
                .setCommand(command)
                .setTargetObject(targetObject)
                .setInvocationTimestamp(null)
                .setInvokedBy(new InvokedBy().setUserId(new UserId(this.springApplicationName, email)))
                .setCommandAttributes(new HashMap<>());

        MiniAppCommandBoundary result = this.restTemplate
                                        .postForObject(
                                            this.baseUrl + "/superapp/miniapp/{miniAppName}?async={asyncFlag}"
                                            , commandBoundary
                                            , MiniAppCommandBoundary.class
                                            , "DATING"
                                            , "true");

        assert result != null;
        Map<String, Object> statusJms = result.getCommandAttributes();

        changeRole(admin, email);
        assertThat(help_GetAllMiniappCommands(this.springApplicationName, email, null, null))
                .isNotNull()
                .isEmpty();

        assertThat(statusJms.get("status")).isNotNull().isEqualTo("in process");

        //THEN The server response with status 2xx code and command stored in db
        // and return SuperAppObjectBoundary with UserDetails in object details
        changeRole(miniappRole, email);
        try {
            Thread.sleep(5000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        changeRole(admin, email);
        assertThat(help_GetAllMiniappCommands(this.springApplicationName, email, null, null))
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

    }


    @Test
    public void testPermissionForInvokeWithSuperAppUserRole() {
        // GIVEN The server is up
        // database is up
        // user exists in database
        String email = "demo@gmail.com";
        UserBoundary user = createUser(email, superappRole);

        //WHEN A POST request is made to the path "/superapp/miniapp/{miniAppName}?async={asyncFlag}"

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(null)
                .setCommand("command")
                .setTargetObject(new TargetObject().setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand)))
                .setInvocationTimestamp(null)
                .setInvokedBy( new InvokedBy().setUserId(user.getUserId()))
                .setCommandAttributes(new HashMap<>());

        //THEN The server response with status 401 code
        assertThatThrownBy(() ->  this.restTemplate
                .postForObject(
                        this.baseUrl + "/superapp/miniapp/{miniAppName}?async={asyncFlag}"
                        , commandBoundary
                        , MiniAppCommandBoundary.class
                        , MiniAppNames.DATING.name()
                        , null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.UNAUTHORIZED.value());

    }

    @Test
    public void testPermissionForInvokeWithAdminUserRole() {
        // GIVEN The server is up
        // database is up
        // user exists in database
        String email = "demo@gmail.com";
        UserBoundary user = createUser(email, admin);

        //WHEN A POST request is made to the path "/superapp/miniapp/{miniAppName}?async={asyncFlag}"

        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary()
                .setCommandId(null)
                .setCommand("command")
                .setTargetObject(new TargetObject().setObjectId(new ObjectId(this.springApplicationName, this.internalObjectIdForCommand)))
                .setInvocationTimestamp(null)
                .setInvokedBy( new InvokedBy().setUserId(user.getUserId()))
                .setCommandAttributes(new HashMap<>());

        //THEN The server response with status 401 code
        assertThatThrownBy(() ->  this.restTemplate
                .postForObject(
                        this.baseUrl + "/superapp/miniapp/{miniAppName}?async={asyncFlag}"
                        , commandBoundary
                        , MiniAppCommandBoundary.class
                        , MiniAppNames.DATING.name()
                        , null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.UNAUTHORIZED.value());

    }

    @Test
    public void testPermissionForDeleteCommandsWithSuperAppUserRole() {
        // GIVEN The server is up
        // database is up
        // user exists in database
        String email = "demo@gmail.com";
        createUser(email, superappRole);

        // WHEN A POST request is made to the path "/superapp/miniapp/{miniAppName}?async={asyncFlag}"

        // THEN The server response with status 401 code
        assertThatThrownBy(() ->  this.restTemplate
                .delete(this.baseUrl + "/superapp/admin/miniapp?userSuperapp={superapp}&userEmail={email}",
                this.springApplicationName, email))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void testPermissionForGetAllCommandsWithSuperAppUserRole() {
        // GIVEN The server is up
        // database is up
        // user exists in database
        String email = "demo@gmail.com";
        createUser(email, superappRole);

        // WHEN A POST request is made to the path "/superapp/miniapp/{miniAppName}?async={asyncFlag}"

        // THEN The server response with status 401 code
        assertThatThrownBy(() ->  this.restTemplate
                .getForObject(this.baseUrl
                                + "/superapp/admin/miniapp?userSuperapp={superapp}&userEmail={email}&size={size}&page={page}",
                        MiniAppCommandBoundary[].class, this.springApplicationName, email, null, null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.UNAUTHORIZED.value());

    }

    @Test
    public void testPermissionForGetAllSpecificMiniAppCommandWithSuperAppUserRole() {
        // GIVEN The server is up
        // database is up
        // user exists in database
        String email = "demo@gmail.com";
        createUser(email, superappRole);

        //WHEN A POST request is made to the path "/superapp/miniapp/{miniAppName}?async={asyncFlag}"

        //THEN The server response with status 401 code
        assertThatThrownBy(() ->  this.restTemplate
                .getForObject(this.baseUrl
                                + "/superapp/admin/miniapp/{miniappName}?userSuperapp={superapp}&userEmail={email}&size={size}&page={page}",
                        MiniAppCommandBoundary[].class, "DATING", this.springApplicationName, email, null, null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.UNAUTHORIZED.value());

    }

    @Test
    public void testPermissionForDeleteCommandsWithMiniAppUserRole() {
        // GIVEN The server is up
        // database is up
        // user exists in database
        String email = "demo@gmail.com";
        createUser(email, miniappRole);

        // WHEN A POST request is made to the path "/superapp/miniapp/{miniAppName}?async={asyncFlag}"

        // THEN The server response with status 401 code
        assertThatThrownBy(() ->  this.restTemplate
                .delete(this.baseUrl + "/superapp/admin/miniapp?userSuperapp={superapp}&userEmail={email}",
                        this.springApplicationName, email))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void testPermissionForGetAllCommandsWithMiniAppUserRole() {
        // GIVEN The server is up
        // database is up
        // user exists in database
        String email = "demo@gmail.com";
        createUser(email, miniappRole);

        // WHEN A POST request is made to the path "/superapp/miniapp/{miniAppName}?async={asyncFlag}"

        //THEN The server response with status 401 code
        assertThatThrownBy(() ->  this.restTemplate
                .getForObject(this.baseUrl
                                + "/superapp/admin/miniapp?userSuperapp={superapp}&userEmail={email}&size={size}&page={page}",
                        MiniAppCommandBoundary[].class, this.springApplicationName, email, null, null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void testPermissionForGetAllSpecificMiniAppCommandWithMiniAppUserRole() {
        // GIVEN The server is up
        // database is up
        // user exists in database
        String email = "demo@gmail.com";
        createUser(email, miniappRole);

        //WHEN A POST request is made to the path "/superapp/miniapp/{miniAppName}?async={asyncFlag}"

        //THEN The server response with status 401 code
        assertThatThrownBy(() ->  this.restTemplate
                .getForObject(this.baseUrl
                                + "/superapp/admin/miniapp/{miniappName}?userSuperapp={superapp}&userEmail={email}&size={size}&page={page}",
                        MiniAppCommandBoundary[].class, "DATING", this.springApplicationName, email, null, null))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(e -> ((HttpClientErrorException) e ).getStatusCode().value())
                .isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
