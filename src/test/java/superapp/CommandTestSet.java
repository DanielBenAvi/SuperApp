package superapp;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
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
public class CommandTestSet {
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
        this.restTemplate.delete(this.baseUrl + "/superapp/admin/miniapp");
    }


    public void ObjectsInsertion(){
        CommandId commandId1 = new CommandId(springApplicationName, "Dating", "1");
        CommandId commandId2 = new CommandId(springApplicationName, "Marketplace", "2");


        String command1 = "Like a post";
        String command2 = "List item";


        ObjectId oID1 = new ObjectId(springApplicationName, "1");
        TargetObject targetObject1 = new TargetObject();
        targetObject1.setObjectId(oID1);

        ObjectId oID2 = new ObjectId(springApplicationName, "2");
        TargetObject targetObject2 = new TargetObject();
        targetObject1.setObjectId(oID2);


        String email1 = "demo1@gmail.com";
        String email2 = "demo2@gmail.com";

        InvokedBy invokedBy1 = new InvokedBy();
        UserID user1 = new UserID();
        user1.setEmail(email1).setSuperapp(springApplicationName);
        invokedBy1.setUserId(user1);

        InvokedBy invokedBy2 = new InvokedBy();
        UserID user2 = new UserID();
        user1.setEmail(email2).setSuperapp(springApplicationName);
        invokedBy2.setUserId(user2);


        Map<String, Object> commandAttributes = new HashMap<String, Object>();
        commandAttributes.put("Dating", user1);
        commandAttributes.put("Marketplace", user2);

        MiniAppCommandBoundary commandBoundary1 = new MiniAppCommandBoundary();
        commandBoundary1.setCommand(command1).setCommandId(commandId1).setInvokedBy(invokedBy1).setTargetObject(targetObject1).setCommandAttributes(commandAttributes).setInvocationTimestamp(new Date());
        this.restTemplate.postForObject(this.baseUrl + "/superapp/admin/miniapp/{miniAppName}", commandBoundary1, MiniAppCommandBoundary.class, "Dating");

        MiniAppCommandBoundary commandBoundary2 = new MiniAppCommandBoundary();
        commandBoundary2.setCommand(command2).setCommandId(commandId2).setInvokedBy(invokedBy2).setTargetObject(targetObject2).setCommandAttributes(commandAttributes).setInvocationTimestamp(new Date());
        this.restTemplate.postForObject(this.baseUrl + "/superapp/admin/miniapp/{miniAppName}", commandBoundary2, MiniAppCommandBoundary.class, "Marketplace");
    }

    public MiniAppCommandBoundary[] getAllMiniappBoundary(){
        return this.restTemplate.getForObject(this.baseUrl + "/superapp/admin/miniapp", MiniAppCommandBoundary[].class);
    }

    public MiniAppCommandBoundary[] getSpecificMiniappBoundary(){
        return this.restTemplate.getForObject(this.baseUrl + "/superapp/admin/miniapp/{miniAppName}", MiniAppCommandBoundary[].class, "Events");

    }

    @Test
    @DisplayName("Delete all commands from full DB")
    public void testSuccessfulDeleteAllFullDB() {
        //GIVEN The server is up
        //AND db is up
        //AND command objects exist in db
        ObjectsInsertion();
        //WHEN a delete request is made to the path /superapp/admin/miniapp
        //THEN the db response with the status 2xx code
        //AND commands objects no longer exist

        this.restTemplate.delete((this.baseUrl + "/superapp/admin/miniapp"));

        MiniAppCommandBoundary[] commands = getAllMiniappBoundary();

        assertThat(commands).isEmpty();

    }



    @Test
    @DisplayName("Delete all commands from empty DB")
    public void testSuccessfulDeleteAllEmptyDB() {
        //GIVEN The server is up
        //AND db is up
        //AND commands objects don't exist in db
        this.restTemplate.delete((this.baseUrl + "/superapp/admin/miniapp"));
        //WHEN a delete request is made to the path /superapp/admin/miniapp
        MiniAppCommandBoundary[] commands = getAllMiniappBoundary();
        //THEN the db response with the status 2xx code
        //AND commands objects no longer exist
        assertThat(commands).isEmpty();

    }


    @Test
    @DisplayName("Delete all commands history with illegal path")
    public void testSuccessfulDeleteOfAllHistoryCommandsIllegalPath() {
//        GIVEN The server is up
//        AND
//        database is up
//        AND
//        any commands objects exists in database

        //WHEN illegal path of delete request is made
        //THEN The server response with status 4xx code
        //AND
        //commands objects are not deleted

        ObjectsInsertion();

        this.restTemplate.delete((this.baseUrl + "/superapp/admin/MiniApp"));
        MiniAppCommandBoundary[] commands = getAllMiniappBoundary();
        assertThat(commands).isNotEmpty();
        //TODO:FIX EXCEPTION
    }

    @Test
    @DisplayName("Get command history of specific miniapp - no history")
    public void testSuccessfulGetCommandHistoryOfSpecificMiniappNoHistory() {
//        GIVEN The server is up
//        AND
//        database is up
//        AND
//        commands objects of a chosen ""miniAppName"" does NOT exists in database
//        AND
//        commands objects of any other miniapps exists in database
        ObjectsInsertion();

//        WHEN A GET request is made to the path
//        "superapp/admin/miniapp/{miniAppName}"
        MiniAppCommandBoundary[] command = getSpecificMiniappBoundary();
//       THEN The server response with status 2xx code
        assertThat(command).isEmpty();
    }

    @Test
    @DisplayName("Get command history of specific miniapp")
    public void testSuccessfulGetCommandHistoryOfSpecificMiniapp() {

    }

    @Test
    @DisplayName("Get all commands history of specific miniapp with illegal path")
    public void testSuccessfulGetAllHistoryCommandsSpecificMiniappIllegalPath() {

    }


    @Test
    @DisplayName("Get all commands history with non existing specific miniapp name")
    public void testSuccessfulGetAllHistoryCommandsNonExistingSpecificMiniappName() {

    }

    @Test
    @DisplayName("Get command history of  miniapps")
    public void testSuccessfulGetCommandHistoryOfMiniapps() {

    }


    @Test
    @DisplayName("Get command history of  miniapps when db is empty")
    public void testSuccessfulGetCommandHistoryOfMiniappsDbEmpty() {

    }

    @Test
    @DisplayName("Get all commands history with illegal path")
    public void testSuccessfulGetAllHistoryCommandsIllegalPath() {

    }

    @Test
    @DisplayName("Create command entity by invoke func and the return object")
    public void testSuccessfulPostOfCommandEntityByInvokeFunc() {
    //TODO:FIX VOID
    }

    @Test
    @DisplayName("Create command entity by invoke with invalid path")
    public void testSuccessfulPostOfCommandEntityByInvokeFuncInvalidPath() {

    }

    @Test
    @DisplayName("Create command entity by invoke with invalid JSON structure")
    public void testSuccessfulPostOfCommandEntityByInvokeFuncInvalidJSONstructure() {

    }

    @Test
    @DisplayName("Create command entity by invoke with miniapp name in path that not listed in our miniapps")
    public void testSuccessfulPostOfCommandEntityByInvokeFuncSpecificMiniappNameNotInListOfMiniapps() {

    }

    @Test
    @DisplayName("Create command entity by invoke with miniappCommandBoundary=null")
    public void testSuccessfulPostOfCommandEntityByInvokeFuncWithMiniappBoundaryNullValue() {

    }

    @Test
    @DisplayName("Create command entity by invoke when command not exists")
    public void testSuccessfulPostOfCommandEntityByInvokeFuncCommandNotExists() {

    }

    @Test
    @DisplayName("Create command entity by invoke with command=null")
    public void testSuccessfulPostOfCommandEntityByInvokeFuncWithCommandNullValue() {

    }


    @Test
    @DisplayName("Create command entity with no existing targetObject in database")
    public void testSuccessfulPostOfCommandEntityByInvokeFuncTargetObjectNotExisting() {

    }

}
