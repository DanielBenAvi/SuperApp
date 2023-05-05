package superapp.logic.mongo;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import superapp.data.MiniAppCommandCrud;
import superapp.logic.boundaries.InvokedBy;
import superapp.logic.boundaries.TargetObject;
import superapp.logic.excptions.BadRequestException;
import superapp.miniapps.MiniAppNames;
import superapp.data.MiniAppCommandEntity;
import superapp.logic.ConvertHelp;
import superapp.logic.MiniAppCommandService;
import superapp.logic.boundaries.CommandId;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.command.Commands;

import java.util.*;

@Service
public class MiniAppCommandManagerMongoDB implements MiniAppCommandService {

    private final MiniAppCommandCrud miniAppCommandCrud;
    private String springApplicationName;

    @Autowired
    public MiniAppCommandManagerMongoDB(MiniAppCommandCrud miniAppCommandCrud) {
        this.miniAppCommandCrud = miniAppCommandCrud;
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
    public Object invokeCommand(MiniAppCommandBoundary command) {

        if (command == null) throw new BadRequestException("MiniAppCommandBoundary object cant be null");

        // set command id
        command.getCommandId().setSuperapp(springApplicationName);

        // set internal command id
        command.getCommandId().setInternalCommandId(UUID.randomUUID().toString());

        // is valid command id
        if (!isValidCommandId(command.getCommandId()))
            throw new BadRequestException("command id is not valid");

        // is valid invoked by
        if (!isValidInvokedBy(command.getInvokedBy()))
            throw new BadRequestException("invoked by is not valid");

        // is valid target object
        if (!isValidTargetObject(command.getTargetObject()))
            throw new BadRequestException("target object is not valid");

        if (command.getCommand() == null)
            throw new BadRequestException("command is not valid");

        // set invocation timestamp
        command.setInvocationTimestamp(new Date());

        // convert to entity
        MiniAppCommandEntity commandEntity = convertToEntity(command);

        /////////////////////// execute command ///////////////////////
        Map<String, Object> commandResult = new HashMap<>();
        String cmdToExecute = commandEntity.getCommand();

        // check if command is null
        if (cmdToExecute == null) {
            commandResult.put(commandEntity.getCommandId(), "command cannot be null");
            return commandResult;
        }

        // save command to database
        this.miniAppCommandCrud.save(commandEntity);

        // check if command is not valid
        try {
            Commands.valueOf(cmdToExecute);
            commandResult.put(commandEntity.getCommandId(), cmdToExecute + " successfully executed");
        } catch (Exception e) {
            commandResult.put(commandEntity.getCommandId(), Commands.UNKNOWN + " - " + cmdToExecute + " not recognized");
            return commandResult;
        }

        return commandResult;
    }

    @Override
    public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName) {

        // Check if the miniApp name is valid
        try {
            MiniAppNames.valueOf(miniAppName);
        } catch (Exception e) {
            throw new RuntimeException("MiniApp name is not valid");
        }

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
        try {
            MiniAppNames.valueOf(commandId.getMiniapp());
        } catch (BadRequestException e) {
            return false;
        }
        // check if commandId fields are null
        return commandId.getInternalCommandId() != null;
    }

    private boolean isValidInvokedBy(InvokedBy invokedBy) {
        // check if invokedBy is null
        if (invokedBy == null) return false;

        // check if invokedBy fields are null
        if (invokedBy.getUserId() == null) return false;

        // todo: check if userId is valid (need to check with user service)


        return true;
    }

    private boolean isValidTargetObject(TargetObject targetObject) {
        // check if targetObject is null
        if (targetObject == null) return false;

        // check if targetObject fields are null

        // check if targetObject fields are null
        if (targetObject.getObjectId() == null) return false;

        if (targetObject.getObjectId().getInternalObjectId() == null) return false;

        if (targetObject.getObjectId().getSuperapp() == null) return false;

        // todo: check if objectId is valid (need to check with user service)

        return true;
    }
}
