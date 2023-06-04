package superapp.logic.mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import superapp.data.*;
import superapp.logic.MiniAppCommandWithAsyncSupport;
import superapp.logic.boundaries.*;
import superapp.logic.utils.convertors.CommandConvertor;
import superapp.logic.utils.validators.BoundaryValidator;
import superapp.logic.utils.validators.EntitiesValidator;
import superapp.logic.utils.validators.ValidatorHelper;
import superapp.miniapps.MiniAppNames;
import superapp.miniapps.command.CommandInvoker;
import superapp.miniapps.command.InvalidCommand;
import superapp.miniapps.command.MiniAppsCommand;


import java.util.*;

@Service
public class MiniAppCommandManagerMongoDB implements MiniAppCommandWithAsyncSupport {

    private String springApplicationName;
    private ObjectMapper jackson;
    private JmsTemplate jmsTemplate;
    private final MiniAppCommandCrud miniAppCommandCrud;
    private final CommandInvoker commandInvoker;
    private final RBAC accessControl;
    private final BoundaryValidator boundaryValidator;
    private final EntitiesValidator entitiesValidator;
    private final CommandConvertor commandConvertor;


    @Autowired
    public MiniAppCommandManagerMongoDB(MiniAppCommandCrud miniAppCommandCrud,
                                        CommandInvoker commandInvoker,
                                        CommandConvertor commandConvertor,
                                        BoundaryValidator boundaryValidator,
                                        EntitiesValidator entitiesValidator,
                                        RBAC accessControl) {

        this.miniAppCommandCrud = miniAppCommandCrud;
        this.commandInvoker = commandInvoker;
        this.commandConvertor = commandConvertor;
        this.accessControl = accessControl;
        this.boundaryValidator = boundaryValidator;
        this.entitiesValidator = entitiesValidator;
    }

    @Autowired
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
        this.jmsTemplate.setDeliveryDelay(3000L);
    }

    /**
     * This method injects a configuration value of spring.
     *
     * @param springApplicationName String
     */
    @Value("${spring.application.name:defaultAppName}")
    public void setApplicationName(String springApplicationName) {
        this.springApplicationName = springApplicationName;
    }

    @PostConstruct
    public void init() {
        System.err.println("****** " + this.getClass().getName() + " service initiated");
        this.jackson = new ObjectMapper();
    }


    /**
     * This methode execute the commands by commandInvoker
     *
     * @param commandBoundary
     * @return Object command result
     */
    @Override
    public Object invokeCommand(MiniAppCommandBoundary commandBoundary) {


        // validate command attributes, and init id and timestamp
        this.validateAndInitIdCommandBeforeExecute(commandBoundary);

        /////////////////////// execute command ///////////////////////

        return this.executeAndStoreCommand(commandBoundary);
    }



    private void validateAndInitIdCommandBeforeExecute(MiniAppCommandBoundary command) {

        // set command id and invocation timestamp
        CommandId commandId = command.getCommandId()
                .setInternalCommandId(UUID.randomUUID().toString())
                .setSuperapp(this.springApplicationName);

        command
                .setCommandId(commandId)
                .setInvocationTimestamp(new Date());

        // validation
        this.boundaryValidator.validateCommandBoundary(command);
        this.boundaryValidator.validateInvokedBy(command.getInvokedBy());
        this.boundaryValidator.validateTargetObject(command.getTargetObject());


        // validate that user exist and retrieve the user from database
        UserId userId = command.getInvokedBy().getUserId();
        UserEntity userEntity = this.entitiesValidator.validateExistingUser(userId.getSuperapp(), userId.getEmail());

        // validate target object exist in database
        ObjectId objectId = command.getTargetObject().getObjectId();
        SuperAppObjectEntity targetObject =
                this.entitiesValidator.validateExistingObject(objectId.getSuperapp(), objectId.getInternalObjectId());

        // validate object exist is not active:false
        if (targetObject.isActive())
            throw new NotFoundException("target object id " + targetObject + "not found - active:false");

        this.checkPermission(userEntity.getUserID(), "invokeCommand");
    }

    private Map<String, Object> executeAndStoreCommand(MiniAppCommandBoundary command) {

        Object resultObjectOfCommand;

        MiniAppNames miniappName = MiniAppNames.strToMiniAppName(command.getCommandId().getMiniapp());
        MiniAppsCommand.commands commandsToExecute = MiniAppsCommand.strToCommand(command.getCommand());

        if (miniappName.equals(MiniAppNames.UNKNOWN))
            resultObjectOfCommand = new InvalidCommand(miniappName + " miniapp name: "
                    + command.getCommandId().getMiniapp() + " not supported");
        else if (commandsToExecute.equals(MiniAppsCommand.commands.UNKNOWN_COMMAND))
            resultObjectOfCommand = new InvalidCommand(miniappName + " miniapp command: "
                    + command.getCommand() + " not supported");
        else
            resultObjectOfCommand = commandInvoker
                    .create(commandsToExecute)
                    .execute(command);

        // convert to entity
        MiniAppCommandEntity commandEntity = this.commandConvertor.toEntity(command);

        // save command to database
        this.miniAppCommandCrud.save(commandEntity);

        // put command result object into map
        Map<String, Object> commandResult = new HashMap<>();
        commandResult.put(commandEntity.getCommandId(), resultObjectOfCommand);

        return commandResult;
    }

    /**
     * This method retrieve all commands history of miniapp
     *
     * @param userSuperapp String
     * @param userEmail String
     * @param size int
     * @param page int
     * @return List MiniAppCommandBoundary
     */
    @Override
    public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName, String userSuperapp, String userEmail,
                                                              int size, int page) {

        // check miniApp name, page, size is valid
        ValidatorHelper.validateStringNotNullAndNotEmpty(miniAppName, "Mini App Name");
        ValidatorHelper.validatePage(page);
        ValidatorHelper.validateSize(size);

        // validate that user exist and retrieve the user from database
        UserEntity userEntity = this.entitiesValidator.validateExistingUser(userSuperapp, userEmail);

        this.checkPermission(userEntity.getUserID(), "getAllMiniAppCommands");


        PageRequest pageRequest = PageRequest
                .of(page, size, Sort.Direction.ASC,"invocationTimestamp", "targetObject", "commandId");

        return this.miniAppCommandCrud
                .findAllByCommandIdContaining(miniAppName, pageRequest)
                .stream()
                .map(this.commandConvertor::toBoundary)
                .toList();
    }

    /**
     * This method retrieve all commands history
     *
     * @param userSuperapp String
     * @param userEmail String
     * @param size int
     * @param page int
     * @return List MiniAppCommandBoundary
     */
    @Override
    public List<MiniAppCommandBoundary> getAllCommands(String userSuperapp, String userEmail, int size, int page) {


        ValidatorHelper.validatePage(page);
        ValidatorHelper.validateSize(size);


        // validate that user exist and retrieve the user from database
        UserEntity userEntity = this.entitiesValidator.validateExistingUser(userSuperapp, userEmail);

        this.checkPermission(userEntity.getUserID(), "getAllCommands");

        PageRequest pageRequest = PageRequest
                .of(page, size, Sort.Direction.ASC,"invocationTimestamp", "targetObject", "commandId");

        return this.miniAppCommandCrud
                .findAll(pageRequest)
                .stream()
                .map(this.commandConvertor::toBoundary)
                .toList();
    }


    /**
     * This method delete all commands, only admin has permission
     *
     * @param userSuperapp String
     * @param userEmail String
     */
    @Override
    public void deleteAllCommands(String userSuperapp, String userEmail) {

        // validate that user exist and retrieve the user from database
        UserEntity userEntity = this.entitiesValidator.validateExistingUser(userSuperapp, userEmail);

        this.checkPermission(userEntity.getUserID(), "deleteAllCommands");

        this.miniAppCommandCrud.deleteAll();
    }

    private void checkPermission(String userId, String operationName) {
        // check role permission
        if (this.accessControl.hasPermission(userId, operationName))
            throw new UnauthorizedRequestException("User " + userId + " has no permission to " + operationName);
    }


    /**** Support Async *****/
    @Override
    public Object asyncHandle(MiniAppCommandBoundary command) {

        this.validateAndInitIdCommandBeforeExecute(command);

        if (command.getCommandAttributes() == null) {
            command.setCommandAttributes(new HashMap<>());
        }

        command.getCommandAttributes().put("status", "in process");
        try {

            String json = this.jackson.writeValueAsString(command);
            System.err.println("*** sending: " + json);
            this.jmsTemplate.convertAndSend("commandsQueue", json);

            return command;

        } catch (Exception e) {

            System.err.println("in handler : " + e.getMessage());
            throw new RuntimeException(e);
        }

    }

    @JmsListener(destination = "commandsQueue")
    public void handleCommand(String json) {

        try {
            this.executeAndStoreCommand(
                    commandConvertor.toBoundary(
                            commandConvertor.toEntity(
                                    this.setStatus(
                                            this.jackson
                                                    .readValue(json, MiniAppCommandBoundary.class), "remotely-accepted"))));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private MiniAppCommandBoundary setStatus(MiniAppCommandBoundary miniapp, String status) {

        if (miniapp.getCommandAttributes() == null) {
            miniapp.setCommandAttributes(new HashMap<>());
        }
        miniapp.getCommandAttributes().put("status", status);

        return miniapp;
    }


    /**** Deprecated methods *****/
    @Deprecated
    @Override
    public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName) {
        throw new DeprecatedRequestException("cannot enter a deprecated function");
        //
        //        // Check if the miniApp name is valid
        //        if (!validMiniAppName(miniAppName)) throw new BadRequestException("MiniApp name is not valid");
        //
        //        // Create a list of commands
        //        List<MiniAppCommandBoundary> commandBoundaryList = new ArrayList<>();
        //
        //        // Get all commands from the database and filter by miniApp name
        //        this.miniAppCommandCrud.findAll().forEach(commandEntity -> {
        //            if (commandEntity.getCommandId().contains(miniAppName)) {
        //                commandBoundaryList.add(convertToBoundary(commandEntity));
        //            }
        //        });
        //
        //        return commandBoundaryList;
    }

    @Override
    @Deprecated
    public List<MiniAppCommandBoundary> getAllCommands() {
        throw new DeprecatedRequestException("cannot enter a deprecated function");
        //return this.miniAppCommandCrud.findAll().stream().map(this::convertToBoundary).toList();
    }

    @Override
    @Deprecated
    public void deleteAllCommands() {
        throw new DeprecatedRequestException("cannot enter deprecated function");
        //this.miniAppCommandCrud.deleteAll();
    }

}