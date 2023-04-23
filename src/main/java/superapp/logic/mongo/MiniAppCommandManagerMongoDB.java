package superapp.logic.mongo;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import superapp.dal.MiniAppCommandCrud;
import superapp.dal.MiniAppNames;
import superapp.dal.entities.MiniAppCommandEntity;
import superapp.logic.ConvertHelp;
import superapp.logic.MiniAppCommandService;
import superapp.logic.boundaries.CommandId;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.command.Commands;

import java.util.*;

@Service
public class MiniAppCommandManagerMongoDB implements MiniAppCommandService {

    private MiniAppCommandCrud miniAppCommandCrud;
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
        MiniAppCommandBoundary commandBoundary = command;

        if (commandBoundary == null) throw new RuntimeException("MiniAppCommandBoundary object cant be null");

        String miniappName = commandBoundary.getCommandId().getMiniapp();
        try {
            MiniAppNames.valueOf(miniappName);
        } catch (Exception e) {
            throw new RuntimeException("cant invoke command of miniapp " + miniappName);
        }

        // set command id
        commandBoundary.getCommandId().setSuperapp(springApplicationName);

        // set internal command id
        commandBoundary.getCommandId().setInternalCommandId(UUID.randomUUID().toString());

        // set invocation timestamp
        commandBoundary.setInvocationTimestamp(new Date());

        // convert to entity
        MiniAppCommandEntity commandEntity = convertToEntity(commandBoundary);

        // execute command
        Map<String, Object> commandResult = new HashMap<>();
        String cmdToExecute = commandEntity.getCommand();

        // check if command is null
        if (cmdToExecute == null) {
            commandResult.put(miniappName, "command cannot be null");
            return commandResult;
        }

        // save command to database
        this.miniAppCommandCrud.save(commandEntity);

        // check if command is not valid


        try {
            Commands.valueOf(cmdToExecute);
            commandResult.put(miniappName, Commands.UNKNOWN + " - " + cmdToExecute + " " + commandEntity.getCommandId() + " successfully executed");
        } catch (Exception e) {
            commandResult.put(miniappName, "command " + cmdToExecute + " " + commandEntity.getCommandId() + " not recognized");
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
}
