package superapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;
import superapp.logic.boundaries.*;
import superapp.miniapps.MiniAppNames;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommandTestSet extends BaseTestSet {
    private UserBoundary createUser() {
        String email = "demo@gmail.com";
        String role = "ADMIN";
        String username = "demo";
        String avatar = "demo";
        return help_PostUserBoundary(email, role, username, avatar);
    }

    private SuperAppObjectBoundary createObject(String email) {
        ObjectId objectId = new ObjectId();
        String type = "demo type";
        String alias = "demo name";
        boolean active = true;
        Date createdTimestamp = new Date();
        Location location = new Location().setLat(1.0).setLng(1.0);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserId().setSuperapp(springApplicationName).setEmail(email));
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("demo", "demo");

        // post object
        return help_PostObjectBoundary(objectId, type, alias, createdTimestamp, active, location, createdBy, attributes);
    }

    private void createCommand() {
        // user
        UserBoundary userBoundary = createUser();
        // object
        SuperAppObjectBoundary superAppObjectBoundary = createObject(userBoundary.getUserId().getEmail());

        // command
        MiniAppNames miniAppName = MiniAppNames.EVENT;
        CommandId commandId = new CommandId();
        String command = "DO_SOMETHING";
        TargetObject targetObject = new TargetObject().setObjectId(superAppObjectBoundary.getObjectId());
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setSuperapp(springApplicationName).setEmail(userBoundary.getUserId().getEmail()));
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("demo", "demo");
        help_PostCommandBoundary(miniAppName, commandId, command, targetObject, new Date(), invokedBy, attributes);
    }

    @Test
    @DisplayName("Delete all commands from full DB")
    public void testSuccessfulDeleteAllFullDB() {
        createCommand();

        //GIVEN The server is up
        //AND db is up
        //AND command objects exist in db

        //WHEN a delete request is made to the path /superapp/admin/miniapp
        //THEN the db response with the status 2xx code
        //AND commands objects no longer exist

        this.restTemplate.delete((this.baseUrl + "/superapp/admin/miniapp"));

        assertThat(help_GetAllMiniappBoundary()).isEmpty();

    }


    @Test
    @DisplayName("Delete all commands from empty DB")
    public void testSuccessfulDeleteAllEmptyDB() {
        //GIVEN The server is up
        //AND db is up
        //AND commands objects don't exist in db
        this.restTemplate.delete(this.baseUrl + "/superapp/admin/miniapp");
        //WHEN a delete request is made to the path /superapp/admin/miniapp
        //THEN the db response with the status 2xx code
        //AND commands objects no longer exist
        assertThat(help_GetAllMiniappBoundary()).isEmpty();

    }


    @Test
    @DisplayName("Delete all commands history with illegal path")
    public void testSuccessfulDeleteOfAllHistoryCommandsIllegalPath() {
        createCommand();
//        GIVEN The server is up
//        AND
//        database is up
//        AND
//        any commands objects exists in database

        //WHEN illegal path of delete request is made
        //THEN The server response with status 4xx code
        //AND
        //commands objects are not deleted

        assertThatThrownBy(() -> this.restTemplate.delete((this.baseUrl + "/superapp/admin/NOT_VALID_MINIAPP")))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting("statusCode.4xxClientError");

        MiniAppCommandBoundary[] commands = help_GetAllMiniappBoundary();
        assertThat(commands).isNotEmpty();
    }

    @Test
    @DisplayName("Get command history of specific miniapp - no history")
    public void testSuccessfulGetCommandHistoryOfSpecificMiniappNoHistory() {
        createCommand();
//        GIVEN The server is up
//        AND
//        database is up
//        AND
//        commands objects of a chosen ""miniAppName"" does NOT exists in database
//        AND
//        commands objects of any other miniapps exists in database


//        WHEN A GET request is made to the path
//        "superapp/admin/miniapp/{miniAppName}"
        MiniAppCommandBoundary[] command = help_GetSpecificMiniappBoundary(MiniAppNames.MARKETPLACE);
//       THEN The server response with status 2xx code
        assertThat(command).isEmpty();
    }

    @Test
    @DisplayName("Get command history of specific miniapp")
    public void testSuccessfulGetCommandHistoryOfSpecificMiniapp() {
        createCommand();
//        GIVEN The server is up
//        AND
//        database is up
//        AND
//        commands objects of a chosen ""miniAppName"" does exists in database


//        WHEN A GET request is made to the path
//        "superapp/admin/miniapp/{miniAppName}"
        MiniAppCommandBoundary[] command = help_GetSpecificMiniappBoundary(MiniAppNames.EVENT);
//       THEN The server response with status 2xx code
        assertThat(command).isNotEmpty();
    }

    @Test
    @DisplayName("Get all commands history of specific miniapp with illegal path")
    public void testSuccessfulGetAllHistoryCommandsSpecificMiniappIllegalPath() {
        createCommand();
//        GIVEN The server is up
//        AND
//        database is up
//        AND
//        any commands objects exists in database

        //WHEN illegal path of GET request is made
        //THEN The server response with status 4xx code
        //AND
        //commands objects are not retrieved
        assertThatThrownBy(() -> this.restTemplate.getForObject((this.baseUrl + "/superapp/admin/miniapp/{miniAppName}"), MiniAppCommandBoundary[].class, "NOT_VALID_MINIAPP"))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting("statusCode.4xxClientError");
    }

    @Test
    @DisplayName("Get command history of  miniapps")
    public void testSuccessfulGetCommandHistoryOfMiniapps() {
        createCommand();

//        GIVEN The server is up
//        AND
//        database is up
//        AND
//        commands objects of a chosen ""miniAppName"" does exists in database
//        AND
//        commands objects of any other miniapps exists in database

//        WHEN A GET request is made to the path
//        "superapp/admin/miniapp"
        MiniAppCommandBoundary[] command = this.restTemplate.getForObject((this.baseUrl + "/superapp/admin/miniapp"), MiniAppCommandBoundary[].class);
//       THEN The server response with status 2xx code
        assertThat(command).isNotEmpty();
    }


    @Test
    @DisplayName("Get command history of  miniapps when db is empty")
    public void testSuccessfulGetCommandHistoryOfMiniappsDbEmpty() {
        //GIVEN The server is up
        //AND
        //database is up
        //AND
        //commands objects of a chosen ""miniAppName"" does NOT exists in database
        //AND
        //commands objects of any other miniapps does NOT exists in database

        //WHEN A GET request is made to the path
        //"superapp/admin/miniapp"
        MiniAppCommandBoundary[] command = this.restTemplate.getForObject((this.baseUrl + "/superapp/admin/miniapp"), MiniAppCommandBoundary[].class);
        //THEN The server response with status 2xx code

        assertThat(command).isNotNull().isEmpty();
    }


    @Test
    @DisplayName("Create command by invoke func and the return object")
    public void testSuccessfulPostOfCommandEntityByInvokeFunc() {
        UserBoundary userBoundary = createUser();
        SuperAppObjectBoundary superAppObjectBoundary = createObject(userBoundary.getUserId().getEmail());
        //GIVEN The server is up
        //AND
        //database is up
        //AND
        // user exists in database
        //AND
        // object exists in database

        //WHEN A POST request is made to the path
        MiniAppNames miniAppName = MiniAppNames.EVENT;
        CommandId commandId = new CommandId();
        String command = "DO_SOMETHING";
        TargetObject targetObject = new TargetObject().setObjectId(superAppObjectBoundary.getObjectId());
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setSuperapp(springApplicationName).setEmail(userBoundary.getUserId().getEmail()));
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("demo", "demo");
        MiniAppCommandBoundary miniAppCommandBoundary = new MiniAppCommandBoundary()
                .setCommand(command)
                .setCommandId(commandId)
                .setTargetObject(targetObject)
                .setInvokedBy(invokedBy)
                .setCommandAttributes(attributes);

        this.restTemplate.postForObject((this.baseUrl + "/superapp/miniapp/{miniAppName}"), miniAppCommandBoundary, MiniAppCommandBoundary.class, miniAppName);
        //THEN The server response with status 2xx code

    }

    @Test
    @DisplayName("Create command by invoke with invalid path")
    public void testSuccessfulPostOfCommandEntityByInvokeFuncInvalidPath() {
        UserBoundary userBoundary = createUser();
        SuperAppObjectBoundary superAppObjectBoundary = createObject(userBoundary.getUserId().getEmail());
        //GIVEN The server is up
        //AND
        //database is up
        //AND
        // user exists in database
        //AND
        // object exists in database

        //WHEN A POST request is made to the path
        CommandId commandId = new CommandId();
        String command = "DO_SOMETHING";
        TargetObject targetObject = new TargetObject().setObjectId(superAppObjectBoundary.getObjectId());
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setSuperapp(springApplicationName).setEmail(userBoundary.getUserId().getEmail()));
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("demo", "demo");
        MiniAppCommandBoundary miniAppCommandBoundary = new MiniAppCommandBoundary()
                .setCommand(command)
                .setCommandId(commandId)
                .setTargetObject(targetObject)
                .setInvokedBy(invokedBy)
                .setCommandAttributes(attributes);


        //THEN The server response with status 2xx code
        this.restTemplate.postForEntity((this.baseUrl + "/superapp/miniapp/{miniAppName}"), miniAppCommandBoundary, MiniAppCommandBoundary.class, "miniAppName");

    }


    @Test
    @DisplayName("Create command entity by invoke when command not exists")
    public void testSuccessfulPostOfCommandEntityByInvokeFuncCommandNotExists() {
        UserBoundary userBoundary = createUser();
        SuperAppObjectBoundary superAppObjectBoundary = createObject(userBoundary.getUserId().getEmail());
        //GIVEN The server is up
        //AND
        //database is up
        //AND
        // user exists in database
        //AND
        // object exists in database

        //WHEN A POST request is made to the path
        CommandId commandId = new CommandId();
        String command = "NOT_EXISTS_COMMAND";
        TargetObject targetObject = new TargetObject().setObjectId(superAppObjectBoundary.getObjectId());
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setSuperapp(springApplicationName).setEmail(userBoundary.getUserId().getEmail()));
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("demo", "demo");
        MiniAppCommandBoundary miniAppCommandBoundary = new MiniAppCommandBoundary()
                .setCommand(command)
                .setCommandId(commandId)
                .setTargetObject(targetObject)
                .setInvokedBy(invokedBy)
                .setCommandAttributes(attributes);


        //THEN The server response with status 2xx code
        this.restTemplate.postForObject((this.baseUrl + "/superapp/miniapp/{miniAppName}"), miniAppCommandBoundary, MiniAppCommandBoundary.class, MiniAppNames.EVENT);
    }

    @Test
    @DisplayName("Create command by invoke with command=null")
    public void testSuccessfulPostOfCommandEntityByInvokeFuncWithCommandNullValue() {
        UserBoundary userBoundary = createUser();
        SuperAppObjectBoundary superAppObjectBoundary = createObject(userBoundary.getUserId().getEmail());
        //GIVEN The server is up
        //AND
        //database is up
        //AND
        // user exists in database
        //AND
        // object exists in database

        //WHEN A POST request is made to the path
        CommandId commandId = new CommandId();
        String command = null;
        TargetObject targetObject = new TargetObject().setObjectId(superAppObjectBoundary.getObjectId());
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setSuperapp(springApplicationName).setEmail(userBoundary.getUserId().getEmail()));
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("demo", "demo");
        MiniAppCommandBoundary miniAppCommandBoundary = new MiniAppCommandBoundary()
                .setCommand(command)
                .setCommandId(commandId)
                .setTargetObject(targetObject)
                .setInvokedBy(invokedBy)
                .setCommandAttributes(attributes);


        //THEN The server response with status 4xx code
        assertThatThrownBy(() -> this.restTemplate.postForObject((this.baseUrl + "/superapp/miniapp/{miniAppName}"), miniAppCommandBoundary, MiniAppCommandBoundary.class, MiniAppNames.EVENT))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting("statusCode.4xxClientError");
    }


    @Test
    @DisplayName("Create command with no existing targetObject in database")
    public void testSuccessfulPostOfCommandEntityByInvokeFuncTargetObjectNotExisting() {
        UserBoundary userBoundary = createUser();
//        SuperAppObjectBoundary superAppObjectBoundary = createObject(userBoundary.getUserId().getEmail());
        //GIVEN The server is up
        //AND
        //database is up
        //AND
        // user exists in database
        //AND
        // object exists in database

        //WHEN A POST request is made to the path
        CommandId commandId = new CommandId();
        String command = null;
        TargetObject targetObject = new TargetObject().setObjectId(new ObjectId().setSuperapp(springApplicationName).setInternalObjectId(UUID.randomUUID().toString()));
        InvokedBy invokedBy = new InvokedBy().setUserId(new UserId().setSuperapp(springApplicationName).setEmail(userBoundary.getUserId().getEmail()));
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("demo", "demo");
        MiniAppCommandBoundary miniAppCommandBoundary = new MiniAppCommandBoundary()
                .setCommand(command)
                .setCommandId(commandId)
                .setTargetObject(targetObject)
                .setInvokedBy(invokedBy)
                .setCommandAttributes(attributes);


        //THEN The server response with status 4xx code
        assertThatThrownBy(() -> this.restTemplate.postForObject((this.baseUrl + "/superapp/miniapp/{miniAppName}"), miniAppCommandBoundary, MiniAppCommandBoundary.class, MiniAppNames.EVENT))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting("statusCode.4xxClientError");

    }

}
