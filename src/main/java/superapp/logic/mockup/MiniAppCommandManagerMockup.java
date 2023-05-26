package superapp.logic.mockup;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import superapp.logic.MiniAppCommandService;
import superapp.logic.utils.convertors.CommandConvertor;
import superapp.miniapps.MiniAppNames;
import superapp.data.MiniAppCommandEntity;
import superapp.logic.boundaries.CommandId;
import superapp.logic.boundaries.MiniAppCommandBoundary;


//@Service
public class MiniAppCommandManagerMockup implements MiniAppCommandService {

    private Map<String, MiniAppCommandEntity> dataBaseMockup;
    private String superAppName;
    private final CommandConvertor commandConvertor;

    @Autowired
    public MiniAppCommandManagerMockup (CommandConvertor commandConvertor) {
        this.commandConvertor = commandConvertor;
    }
    /**
     * This method injects a configuration value of spring.
     *
     * @param springApplicationName String
     */
    @Value("${spring.application.name:defaultAppName}")
    public void setApplicationName(String springApplicationName) {
        this.superAppName = springApplicationName;
    }

    /**
     * This method init the database mockup with thread safe collection
     */
    @PostConstruct
    public void init() {
        this.dataBaseMockup = Collections.synchronizedMap(new HashMap<>());
    }


    /**
     * This method create the MiniAppCommandEntity, execute the command
     * and return an object of command result.
     *
     * @param command MiniAppCommandBoundary
     * @return Object
     */
    @Override
    public Object invokeCommand(MiniAppCommandBoundary command) {

        if (command == null)
            throw new RuntimeException("MiniAppCommandBoundary object cant be null");

        String miniappName = command.getCommandId().getMiniapp();
        try {
            MiniAppNames.valueOf(miniappName);
        } catch (Exception e) {
            throw new RuntimeException("cant invoke command of miniapp " + miniappName);
        }



        // init values of commandId, timestamp
        command.getCommandId().setSuperapp(superAppName);
            command.getCommandId().setInternalCommandId(UUID.randomUUID().toString());
        command.setInvocationTimestamp(new Date());

        MiniAppCommandEntity commandEntity = this.commandConvertor.toEntity(command);

        // execute command
        Map<String, Object> commandResult = new HashMap<>();
        String cmdToExecute = commandEntity.getCommand();

        if (cmdToExecute == null) {
            commandResult.put(miniappName, "command cannot be null");
            return commandResult;
        }

        switch (cmdToExecute) {
            case "DO_SOMETHING" -> commandResult.put(miniappName, "command " + cmdToExecute + " successfully executed");

//                this.dataBaseMockup.put(commandEntity.getCommandId(), commandEntity);
            case "SEND_MESSAGE" -> commandResult.put(miniappName, "command " + cmdToExecute + " successfully executed");

//                this.dataBaseMockup.put(commandEntity.getCommandId(), commandEntity);
            default -> commandResult.put(miniappName, "command " + cmdToExecute + " not recognized");
        }
            this.dataBaseMockup.put(commandEntity.getCommandId(), commandEntity);

        return commandResult;
    }

    /**
     * This method exports commands history of all miniapps
     * as a List.
     *
     * @return List<MiniAppCommandBoundary>
     */
    @Override
    public List<MiniAppCommandBoundary> getAllCommands() {
        return this.dataBaseMockup.values()
                .stream().map(this.commandConvertor::toBoundary).toList();
    }

    /**
     * This method exports commands history of a specific miniapp
     * as a List.
     *
     * @param miniAppName String
     * @return miniappCommands List<MiniAppCommandBoundary>
     */
    @Override
    public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName) {

        try {
            MiniAppNames.valueOf(miniAppName);
        } catch (Exception e) {
            throw new RuntimeException("does not recognise miniapp");
        }

        List<MiniAppCommandBoundary> commandBoundaryList = new ArrayList<>();

        for (Map.Entry<String, MiniAppCommandEntity> entry : this.dataBaseMockup.entrySet()) {

            CommandId commandId = this.commandConvertor.commandIdToBoundary(entry.getKey());
            if (commandId.getMiniapp().equals(miniAppName)) {
                commandBoundaryList.add(this.commandConvertor.toBoundary(entry.getValue()));
            }
        }

        return commandBoundaryList;

    }

    /**
     * This method delete all command history.
     */
    @Override
    public void deleteAllCommands() {
        this.dataBaseMockup.clear();
    }

}
