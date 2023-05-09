package superapp.logic.mongo;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import superapp.data.MiniAppCommandCrud;
import superapp.data.MiniAppCommandEntity;
import superapp.data.ObjectCrud;
import superapp.data.UserCrud;
import superapp.logic.ConvertHelp;
import superapp.logic.MiniAppCommandService;
import superapp.logic.boundaries.CommandId;
import superapp.logic.boundaries.InvokedBy;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.boundaries.TargetObject;
import superapp.miniapps.MiniAppNames;
import superapp.miniapps.commands.CommandFactory;
import superapp.miniapps.commands.DatingCommand;
import superapp.miniapps.commands.InvalidCommand;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static superapp.miniapps.MiniAppNames.DATING;

@Service
public class MiniAppCommandManagerMongoDB implements MiniAppCommandService {

    private final MiniAppCommandCrud miniAppCommandCrud;
    private final UserCrud userCrud;
    private final ObjectCrud objectCrud;
    private String springApplicationName;


    private CommandFactory commandFactory;

    @Autowired
    public void setCommandFactory (CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }
    @Autowired
    public MiniAppCommandManagerMongoDB(MiniAppCommandCrud miniAppCommandCrud, UserCrud userCrud, ObjectCrud objectCrud) {
        this.miniAppCommandCrud = miniAppCommandCrud;
        this.userCrud = userCrud;
        this.objectCrud = objectCrud;
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
        System.err.println("************ MiniAppCommandManagerMongoDB ************ ");
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

    @Override
    public Object invokeCommand(MiniAppCommandBoundary commandBoundary) {

        // not necessary validate
//        if (commandBoundary == null)
//            throw new BadRequestException("MiniAppCommandBoundary object cant be null");

        // set command id
        commandBoundary.getCommandId().setSuperapp(springApplicationName);
        commandBoundary.getCommandId().setInternalCommandId(UUID.randomUUID().toString());

        // set invocation timestamp
        commandBoundary.setInvocationTimestamp(new Date());

        try {
            validateCommand(commandBoundary);
        } catch (Exception exception){
            throw exception;
        }

        // convert to entity
        MiniAppCommandEntity commandEntity = convertToEntity(commandBoundary);


        /////////////////////// execute command ///////////////////////
        Object resultObjectOfCommand;
        String miniappName = commandBoundary.getCommandId().getMiniapp();
        switch (MiniAppNames.getStr(miniappName)) {

            case DATING:
                resultObjectOfCommand = executeDatingCommands(commandBoundary);
                break;
            case EVENT:
                resultObjectOfCommand = executeEventCommands(commandBoundary);
            case GROUP:
                resultObjectOfCommand = executeGroupCommands(commandBoundary);
                break;
            case MARKETPLACE:
                resultObjectOfCommand = executeMarketplaceCommands(commandBoundary);
                break;
            default:
                resultObjectOfCommand =
                        new InvalidCommand(MiniAppNames.UNKNOWN + " - " + miniappName + " not supported");
        }


        // save command to database
        this.miniAppCommandCrud.save(commandEntity);


        Map<String, Object> commandResult = new HashMap<>();
        commandResult.put(commandEntity.getCommandId(), resultObjectOfCommand);

        return commandResult;
    }

    private Object executeDatingCommands(MiniAppCommandBoundary commandBoundary) {

        Object resultObjectOfCommand;


        switch (commandBoundary.getCommand()) {

            case "LIKE":
                resultObjectOfCommand = commandFactory
                                            .create(DatingCommand.LIKE, commandBoundary.getTargetObject())
                                            .execute(commandBoundary);
                break;
            default:
                resultObjectOfCommand = new InvalidCommand("An message");
        }

        return resultObjectOfCommand;
    }

    private Object executeEventCommands(MiniAppCommandBoundary commandBoundary) {
        return null;
    }

    private Object executeGroupCommands(MiniAppCommandBoundary commandBoundary) {
        return null;
    }

    private Object executeMarketplaceCommands(MiniAppCommandBoundary commandBoundary) {
        return null;
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


        // TODO-validate miniapp-name not null
    }











    @Override
    public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName) {

        // Check if the miniApp name is valid
        if (!validMiniAppName(miniAppName)) throw new BadRequestException("MiniApp name is not valid");

        // Create a list of commands
        List<MiniAppCommandBoundary> commandBoundaryList = new ArrayList<>();

        // Get all commands from the database and filter by miniApp name
        this.miniAppCommandCrud.findAll().forEach(commandEntity -> {
            if (commandEntity.getCommandId().contains(miniAppName)) {
                commandBoundaryList.add(convertToBoundary(commandEntity));
            }
        });

        return commandBoundaryList;
    }

    @Override
    public List<MiniAppCommandBoundary> getAllCommands() {
        return this.miniAppCommandCrud.findAll().stream().map(this::convertToBoundary).toList();
    }

    @Override
    public void deleteAllCommands() {
        this.miniAppCommandCrud.deleteAll();
    }

    private boolean isValidCommandId(CommandId commandId) {
        // check if commandId is null
        if (commandId == null) return false;

        // check if commandId fields are null and not empty
        if (commandId.getSuperapp() == null || commandId.getSuperapp().isEmpty()) return false;

        // check if commandId fields are null and miniapp is valid
        if (commandId.getMiniapp() == null) return false;
        if (!validMiniAppName(commandId.getMiniapp())) return false;
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

        // check if user exist in database
        AtomicBoolean userExist = checkIfUserExists(invokedBy);

        if (!userExist.get()) throw new NotFoundException("user id is not valid");

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
        if (targetObject.getObjectId() == null) return false;

        if (targetObject.getObjectId().getInternalObjectId() == null) return false;

        if (targetObject.getObjectId().getSuperapp() == null) return false;

        AtomicBoolean objectExist = checkIfObjectExists(targetObject);
        if (!objectExist.get())
            throw new NotFoundException("object id is not valid");

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

}
