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
import superapp.logic.boundaries.InvokedBy;
import superapp.logic.boundaries.TargetObject;
import superapp.miniapps.MiniAppNames;
import superapp.logic.ConvertHelp;
import superapp.logic.boundaries.CommandId;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.miniapps.command.CommandInvoker;
import superapp.miniapps.command.InvalidCommand;
import superapp.miniapps.command.MiniAppsCommand;


import java.util.*;

@Service
public class MiniAppCommandManagerMongoDB implements MiniAppCommandWithAsyncSupport {
    private String idForCommandWithoutTarget = "a114f4a4-2be4-43ad-90f7-9822e8867d5e";

    private ObjectMapper jackson;
    private JmsTemplate jmsTemplate;
    private final MiniAppCommandCrud miniAppCommandCrud;
    private final UserCrud userCrud;
    private final ObjectCrud objectCrud;
    private final CommandInvoker commandInvoker;
    private RBAC accessControl;

    private String springApplicationName;

    @Autowired
    public MiniAppCommandManagerMongoDB(MiniAppCommandCrud miniAppCommandCrud, UserCrud userCrud,
                                        ObjectCrud objectCrud, CommandInvoker commandInvoker,
                                        RBAC accessControl) {
        this.miniAppCommandCrud = miniAppCommandCrud;
        this.userCrud = userCrud;
        this.objectCrud = objectCrud;
        this.commandInvoker = commandInvoker;
        this.accessControl = accessControl;
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

    /**
     * This method init the database mockup with thread safe collection
     */
    @PostConstruct
    public void init() {
        System.err.println("****** " + this.getClass().getName() + " service initiated");
        this.jackson = new ObjectMapper();
    }


    private boolean isValidInvokedBy(InvokedBy invokedBy) {
        // check if invokedBy is null
        if (invokedBy == null)
            return false;

        // check if invokedBy fields are null
        if (invokedBy.getUserId() == null)
            return false;

        return invokedBy.getUserId().getEmail() != null &&
                !invokedBy.getUserId().getEmail().isEmpty() &&
                invokedBy.getUserId().getSuperapp() != null &&
                !invokedBy.getUserId().getSuperapp().isEmpty();
    }

    private boolean isValidTargetObject(TargetObject targetObject) {
        // check if targetObject is null
        if (targetObject == null) return false;

        // check if targetObject fields are null

        // check if targetObject fields are null
        if (targetObject.getObjectId() == null) return false;

        return targetObject.getObjectId().getInternalObjectId() != null &&
                !targetObject.getObjectId().getInternalObjectId().isEmpty() &&
                targetObject.getObjectId().getSuperapp() != null &&
                !targetObject.getObjectId().getSuperapp().isEmpty();
    }


    @Override
    public Object invokeCommand(MiniAppCommandBoundary commandBoundary) {

        InvokedBy invokedBy = commandBoundary.getInvokedBy();
        if (!isValidInvokedBy(invokedBy))
            throw new BadRequestException("invoked by is not valid");

        String userId = ConvertHelp.concatenateIds(new String[]{
                invokedBy.getUserId().getSuperapp(), invokedBy.getUserId().getEmail()});

        UserEntity user = this.userCrud
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("user " + userId + " not found in database"));

        // check permission
        if (!accessControl.hasPermission(userId, "invokeCommand"))
            throw new UnauthorizedRequestException("user " + userId + " with role " + user.getRole() + " has no permission to invokeCommand");

        // validate target object
        if (!isValidTargetObject(commandBoundary.getTargetObject()))
            throw new BadRequestException("target object is not valid");

        // validate object exist or is a default object
        // todo: create default object for all commands
        SuperAppObjectEntity targetObjectEntity = new SuperAppObjectEntity();
        String objectId = ConvertHelp.objectIdBoundaryToStr(commandBoundary.getTargetObject().getObjectId());

        if (this.objectCrud.existsById(objectId)) {

            targetObjectEntity = this.objectCrud
                    .findById(objectId)
                    .orElseThrow(() -> new NotFoundException("target object not exist in data base"));

            // validate object exist is not active:false
            if (!targetObjectEntity.getActive())
                throw new NotFoundException(" target object id " + targetObjectEntity.getObjectId() + "not found - active:false");

        }
        else if (!commandBoundary.getTargetObject().getObjectId().getInternalObjectId().equals(idForCommandWithoutTarget)
                || !commandBoundary.getTargetObject().getObjectId().getSuperapp().equals(this.springApplicationName)) {

            throw new NotFoundException("target object not exist in data base");
        }


        if (commandBoundary.getCommand() == null)
            throw new BadRequestException("command cant be null");

        // set command id
        commandBoundary
                .getCommandId()
                .setSuperapp(springApplicationName);
        commandBoundary
                .getCommandId()
                .setInternalCommandId(UUID.randomUUID().toString());

        // set invocation timestamp
        commandBoundary
                .setInvocationTimestamp(new Date());

        /////////////////////// execute command ///////////////////////
        Object resultObjectOfCommand;

        MiniAppNames miniappName = MiniAppNames.strToMiniAppName(commandBoundary.getCommandId().getMiniapp());

        MiniAppsCommand.commands commandsToExecute = MiniAppsCommand.strToCommand(commandBoundary.getCommand());


        if (miniappName.equals(MiniAppNames.UNKNOWN)) {
            // TODO : is align with the spec?
            resultObjectOfCommand =
                    new InvalidCommand(miniappName + " miniapp name: "
                            + commandBoundary.getCommandId().getMiniapp() + " not supported");

        }
        else if (commandsToExecute.equals(MiniAppsCommand.commands.UNKNOWN_COMMAND)) {
            resultObjectOfCommand =
                    new InvalidCommand(miniappName + " miniapp command: "
                            + commandBoundary.getCommand() + " not supported");

        }
        else {
            resultObjectOfCommand = commandInvoker
                    .create(commandsToExecute)
                    .execute(commandBoundary);

        }


        // convert to entity
        MiniAppCommandEntity commandEntity = convertToEntity(commandBoundary);

        // save command to database
        this.miniAppCommandCrud.save(commandEntity);

        // put command result object into map
        Map<String, Object> commandResult = new HashMap<>();
        commandResult.put(commandEntity.getCommandId(), resultObjectOfCommand);

        return commandResult;
    }


    @Override
    public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName, String userSuperapp, String userEmail,
                                                              int size, int page) {


        String userId = ConvertHelp.concatenateIds(new String[]{ userSuperapp, userEmail});

        // validate that user exist
        this.userCrud
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("user " + userId + " not found in database"));

        // check role permission
        if (!accessControl.hasPermission(userId, "getAllMiniAppCommands"))
            throw new UnauthorizedRequestException("user " + userId + " has no permission to getAllMiniAppCommands");

        // Check if the miniApp name is valid
        if (MiniAppNames.strToMiniAppName(miniAppName).equals(MiniAppNames.UNKNOWN))
            throw new BadRequestException("MiniApp name is not valid");

        PageRequest pageRequest = PageRequest
                .of(page, size, Sort.Direction.ASC,"invocationTimestamp", "targetObject", "commandId");

        return this.miniAppCommandCrud
                .findAllByCommandIdContaining(miniAppName, pageRequest)
                .stream()
                .map(this::convertToBoundary)
                .toList();
    }


    @Override
    public List<MiniAppCommandBoundary> getAllCommands(String userSuperapp, String userEmail, int size, int page) {

        String userId = ConvertHelp.concatenateIds(new String[]{ userSuperapp, userEmail});

        // validate that user exist
        this.userCrud
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("user " + userId + " not found in database"));

        // check role permission
        if (!accessControl.hasPermission(userId, "getAllCommands"))
            throw new UnauthorizedRequestException("user " + userId + " has no permission to getAllCommands");


        PageRequest pageRequest = PageRequest
                .of(page, size, Sort.Direction.ASC,"invocationTimestamp", "targetObject", "commandId");

        return this.miniAppCommandCrud
                .findAll(pageRequest)
                .stream()
                .map(this::convertToBoundary)
                .toList();
    }


    @Override
    public void deleteAllCommands(String userSuperapp, String userEmail) {
        ConvertHelp.checkIfUserAdmin(userCrud,userSuperapp,userEmail);
        this.miniAppCommandCrud.deleteAll();
    }



    /**
     * This method convert MiniAppCommand Entity to Boundary
     *
     * @param commandEntity MiniAppCommandEntity
     * @return commandBoundary MiniAppCommandBoundary
     */
    public MiniAppCommandBoundary convertToBoundary(MiniAppCommandEntity commandEntity) {
        MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary();
        commandBoundary.setCommandId(ConvertHelp.strCommandIdToBoundary(commandEntity.getCommandId()));
        commandBoundary.setCommand(commandEntity.getCommand());
        commandBoundary.setCommandAttributes(commandEntity.getCommandAttributes());
        commandBoundary.setTargetObject(ConvertHelp.strTargetObjectToBoundary(commandEntity.getTargetObject()));
        commandBoundary.setInvocationTimestamp(commandEntity.getInvocationTimestamp());
        commandBoundary.setInvokedBy(ConvertHelp.strInvokedByToBoundary(commandEntity.getInvokedBy()));

        return commandBoundary;
    }

    /**
     * This method convert MiniAppCommand Boundary to Entity.
     *
     * @param cmdBoundary MiniAppCommandBoundary
     * @return cmdEntity MiniAppCommandEntity
     */
    public MiniAppCommandEntity convertToEntity(MiniAppCommandBoundary cmdBoundary) {

        MiniAppCommandEntity cmdEntity = new MiniAppCommandEntity();

        cmdEntity.setCommand(cmdBoundary.getCommand());

        CommandId commandId = cmdBoundary.getCommandId();
        String[] ids = new String[]{commandId.getSuperapp(), commandId.getMiniapp(), commandId.getInternalCommandId()};
        cmdEntity.setCommandId(ConvertHelp.concatenateIds(ids));

        cmdEntity.setCommandAttributes(cmdBoundary.getCommandAttributes());
        cmdEntity.setInvocationTimestamp(cmdBoundary.getInvocationTimestamp());
        cmdEntity.setTargetObject(ConvertHelp.targetObjBoundaryToStr(cmdBoundary.getTargetObject()));
        cmdEntity.setInvokedBy(ConvertHelp.invokedByBoundaryToStr(cmdBoundary.getInvokedBy()));

        return cmdEntity;
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

    @Override
    public Object asyncHandle(MiniAppCommandBoundary miniAppCommandBoundary) {

        miniAppCommandBoundary
                .getCommandId()
                .setInternalCommandId(UUID.randomUUID()
                        .toString()).setSuperapp(this.springApplicationName);

        miniAppCommandBoundary.setInvocationTimestamp(new Date());

        if (miniAppCommandBoundary.getCommandAttributes() == null) {
            miniAppCommandBoundary.setCommandAttributes(new HashMap<>());
        }

        miniAppCommandBoundary.getCommandAttributes().put("status", "in process");
        try {

            String json = this.jackson.writeValueAsString(miniAppCommandBoundary);
            System.err.println("*** sending: " + json);
            this.jmsTemplate.convertAndSend("commandsQueue", json);

            return miniAppCommandBoundary;

        } catch (Exception e) {
            System.err.println("in handler : " + e.getMessage());

            throw new RuntimeException(e);
        }

    }

    @JmsListener(destination = "commandsQueue")
    public void handleCommand(String json) {

        try {
            this.invokeCommand(
                    this.convertToBoundary(
                            this.convertToEntity(
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
}