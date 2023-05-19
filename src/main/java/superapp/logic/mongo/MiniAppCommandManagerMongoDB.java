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
import superapp.data.MiniAppCommandCrud;
import superapp.data.ObjectCrud;
import superapp.data.UserCrud;
import superapp.logic.ASYNCSupport;
import superapp.logic.boundaries.InvokedBy;
import superapp.logic.boundaries.TargetObject;
import superapp.miniapps.MiniAppNames;
import superapp.data.MiniAppCommandEntity;
import superapp.logic.ConvertHelp;
import superapp.logic.boundaries.CommandId;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.miniapps.command.CommandFactory;
import superapp.miniapps.command.InvalidCommand;
import superapp.miniapps.command.MiniAppsCommand;


import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class MiniAppCommandManagerMongoDB implements ASYNCSupport {

    private final MiniAppCommandCrud miniAppCommandCrud;
    private final UserCrud userCrud;
    private final ObjectCrud objectCrud;
    private final CommandFactory commandFactory;
    private ObjectMapper jackson;
    private JmsTemplate jmsTemplate;
    private String springApplicationName;

    @Autowired
    public MiniAppCommandManagerMongoDB(MiniAppCommandCrud miniAppCommandCrud, UserCrud userCrud,
                                        ObjectCrud objectCrud, CommandFactory commandFactory) {
        this.miniAppCommandCrud = miniAppCommandCrud;
        this.userCrud = userCrud;
        this.objectCrud = objectCrud;
        this.commandFactory = commandFactory;
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

    @Autowired
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
        this.jmsTemplate.setDeliveryDelay(3000L);
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
        commandBoundary.setAsyncFlag(false);
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

    @Override
    public Object invokeCommand(MiniAppCommandBoundary commandBoundary) {


        // TODO - add verification of user exist, targetObject etc.

        // set command id
        commandBoundary.getCommandId().setSuperapp(springApplicationName);
        commandBoundary.getCommandId().setInternalCommandId(UUID.randomUUID().toString());

        // set invocation timestamp
        commandBoundary.setInvocationTimestamp(new Date());

        try {
            validateCommand(commandBoundary);
        } catch (Exception exception) {
            throw exception;
        }


        /////////////////////// execute command ///////////////////////
        Object resultObjectOfCommand;

        MiniAppNames miniappName = MiniAppNames
                .strToMiniAppName(commandBoundary.getCommandId().getMiniapp());

        MiniAppsCommand.commands commandsToExecute;

        commandsToExecute = MiniAppsCommand.strToCommand(commandBoundary.getCommand());

        if (miniappName.equals(MiniAppNames.UNKNOWN)) {
            resultObjectOfCommand =
                    new InvalidCommand(miniappName + " miniapp name: "
                            + commandBoundary.getCommandId().getMiniapp() + " not supported");
        } else if (commandsToExecute.equals(MiniAppsCommand.commands.UNKNOWN_COMMAND)) {
            resultObjectOfCommand =
                    new InvalidCommand(miniappName + " miniapp command: "
                            + commandBoundary.getCommand() + " not supported");
        } else {
            resultObjectOfCommand = commandFactory
                    .create(commandsToExecute).execute(commandBoundary);
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


    private void validateCommand(MiniAppCommandBoundary command) {

        // validate invoked by
        if (!isValidInvokedBy(command.getInvokedBy()))
            throw new BadRequestException("invoked by is not valid");

        // validate target object
        if (!isValidTargetObject(command.getTargetObject()))
            throw new BadRequestException("target object is not valid");

        if (command.getCommand() == null)
            throw new BadRequestException("command cant be null");

        if(command.isAsyncFlag() == null){
            throw new BadRequestException("command cant be null");
        }

        // TODO-validate miniapp-name not null
    }

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
    public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName, String userSuperapp, String userEmail, int size, int page) {

        ConvertHelp.checkIfUserAdmin(userCrud, userSuperapp, userEmail);
        // Check if the miniApp name is valid
        if (!validMiniAppName(miniAppName)) throw new BadRequestException("MiniApp name is not valid");

        // Create a list of commands
        List<MiniAppCommandBoundary> commandBoundaryList = new ArrayList<>();

        // Get all commands from the database and filter by miniApp name
        this.miniAppCommandCrud.findAll(PageRequest.of(page, size, Sort.Direction.ASC, "invocationTimestamp"
                , "targetObject", "commandId")).forEach(commandEntity -> {
            if (commandEntity.getCommandId().contains(miniAppName)) {
                commandBoundaryList.add(convertToBoundary(commandEntity));
            }
        });

        return commandBoundaryList;


    }

    @Override
    @Deprecated
    public List<MiniAppCommandBoundary> getAllCommands() {
        throw new DeprecatedRequestException("cannot enter a deprecated function");
        //return this.miniAppCommandCrud.findAll().stream().map(this::convertToBoundary).toList();
    }

    @Override
    public List<MiniAppCommandBoundary> getAllCommands(String userSuperapp, String userEmail, int size, int page) {
        ConvertHelp.checkIfUserAdmin(userCrud, userSuperapp, userEmail);
        return this.miniAppCommandCrud.findAll(PageRequest.of(page, size, Sort.Direction.ASC, "invocationTimestamp"
                        , "targetObject", "commandId"))
                .stream().map(this::convertToBoundary).toList();
    }

    @Override
    @Deprecated
    public void deleteAllCommands() {
        throw new DeprecatedRequestException("cannot enter decrecated function");
        //this.miniAppCommandCrud.deleteAll();
    }


    @Override
    public void deleteAllCommands(String userSuperapp, String userEmail) {
        ConvertHelp.checkIfUserAdmin(userCrud, userSuperapp, userEmail);
        this.miniAppCommandCrud.deleteAll();
    }


    private boolean isValidCommandId(CommandId commandId) {
        // check if commandId is null
        if (commandId == null) return false;

        // check if commandId fields are null and not empty
        if (commandId.getSuperapp() == null || commandId.getSuperapp().isEmpty()) return false;

        // check if commandId fields are null
        if (commandId.getMiniapp() == null) return false;

        // check if commandId fields are null
        return commandId.getInternalCommandId() != null;
    }

    private boolean isValidInvokedBy(InvokedBy invokedBy) {
        // check if invokedBy is null
        if (invokedBy == null) return false;

        // check if invokedBy fields are null
        if (invokedBy.getUserId() == null) return false;

        if (invokedBy.getUserId().getEmail() == null) return false;

        if (invokedBy.getUserId().getSuperapp() == null) return false;

//        // check if user exist in database
//        AtomicBoolean userExist = checkIfUserExists(invokedBy);
//
//        if (!userExist.get()) throw new NotFoundException("user id is not valid");

        return true;
    }

    private AtomicBoolean checkIfUserExists(InvokedBy invokedBy) {
        AtomicBoolean userExist = new AtomicBoolean(false);
        this.userCrud.findAll().forEach(userEntity -> {
            if (userEntity.getUserID().equals(ConvertHelp.userIdBoundaryToStr(invokedBy.getUserId()))) {
                userExist.set(true);
            }
        });
        return userExist;
    }

    private boolean isValidTargetObject(TargetObject targetObject) {
        // check if targetObject is null
        if (targetObject == null) return false;

        // check if targetObject fields are null

        // check if targetObject fields are null
        if (targetObject.getObjectId() == null) return false;

        if (targetObject.getObjectId().getInternalObjectId() == null) return false;

        if (targetObject.getObjectId().getSuperapp() == null) return false;

//        AtomicBoolean objectExist = checkIfObjectExists(targetObject);
//        if (!objectExist.get())
//            throw new NotFoundException("object id is not valid");

        return true;
    }

    private AtomicBoolean checkIfObjectExists(TargetObject targetObject) {
        AtomicBoolean objectExist = new AtomicBoolean(false);
        this.objectCrud.findAll().forEach(objectEntity -> {
            if (objectEntity.getObjectId().equals(ConvertHelp.objectIdBoundaryToStr(targetObject.getObjectId()))) {
                objectExist.set(true);
            }
        });
        return objectExist;
    }

    private boolean validMiniAppName(String miniAppName) {
        try {
            MiniAppNames.valueOf(miniAppName);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public MiniAppCommandBoundary asyncHandle(MiniAppCommandBoundary miniAppCommandBoundary) {
        miniAppCommandBoundary.getCommandId().setInternalCommandId(UUID.randomUUID().toString());
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
            throw new RuntimeException(e);
        }
    }

    @JmsListener(destination = "commandsQueue")
    public void handleMessage(String json) {
        try {
            System.err.println(this.convertToBoundary(this.miniAppCommandCrud.save(
                    this.convertToEntity(
                            this.setStatus(
                                    this.jackson.readValue(json, MiniAppCommandBoundary.class),"remootly-accepted")))));
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

