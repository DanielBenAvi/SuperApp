package superapp.logic;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import superapp.data.MiniAppNames;
import superapp.data.entities.MiniAppCommandEntity;
import superapp.logic.boundaries.CommandId;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.command.Commands;


@Service
public class MiniAppCommandManager implements MiniAppCommandService {

	private Map<String, MiniAppCommandEntity> dataBaseMockup;
	private String superAppName;


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
    public void init(){
        this.dataBaseMockup = Collections.synchronizedMap(new HashMap<>());
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
		String [] ids = new String[] {commandId.getSuperapp(), commandId.getMiniapp(), commandId.getInternalCommandId()};
		cmdEntity.setCommandId(ConvertHelp.concatenateIds(ids));

		cmdEntity.setCommandAttributes(cmdBoundary.getCommandAttributes());
		cmdEntity.setInvocationTimestamp(cmdBoundary.getInvocationTimestamp());
		cmdEntity.setTargetObject(ConvertHelp.targetObjBoundaryToStr(cmdBoundary.getTargetObject()));
		cmdEntity.setInvokedBy(ConvertHelp.invokedByBoundaryToStr(cmdBoundary.getInvokedBy()));
		
		return cmdEntity;
	}

	 /**
	  * This method create the MiniAppCommandEntity, execute the command
	  * and return an object of command result.
	  *
	  * @param command MiniAppCommandBoundary
	  * @return
	  */
	@Override
	public Object invokeCommand(MiniAppCommandBoundary command) {

		MiniAppCommandBoundary commandBoundary = command;

		if (commandBoundary == null)
			throw new RuntimeException("MiniAppCommandBoundary object cant be null");

		String miniappName = commandBoundary.getCommandId().getMiniapp();
		try {
			MiniAppNames.valueOf(miniappName);
		} catch (Exception e) {
			throw new RuntimeException("cant invoke command of miniapp " + miniappName );
		}

		// TODO: for future check if targetObject existing, UserRole, and UserID.

		// init values of commandId, timestamp
		commandBoundary.getCommandId().setSuperapp(superAppName);
		commandBoundary.getCommandId().setInternalCommandId(UUID.randomUUID().toString());
		commandBoundary.setInvocationTimestamp(new Date());

		MiniAppCommandEntity commandEntity = convertToEntity(commandBoundary);

		// execute command
		Object commandResult;
		Commands cmdToExecute = Commands.valueOf(commandEntity.getCommand());

		switch (cmdToExecute) {
			case DO_SOMETHING:
				commandResult = "command " + cmdToExecute + " successfully executed";
			case SEND_MESSAGE:
				// TODO: for future, extract the command attr and execute on targetObject
				commandResult = "command " + cmdToExecute + " successfully executed";
			default:
				commandResult = "command " + cmdToExecute + " not recognized";
		}

		// save execute command to database
		dataBaseMockup.put(commandEntity.getCommandId(), commandEntity);

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
				.stream().map(this::convertToBoundary).toList();
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


		List<MiniAppCommandBoundary> miniappCommands = this.dataBaseMockup
														.values()
														.stream()
														.map(this::convertToBoundary)
														.toList();

		miniappCommands.stream().filter(cmd -> cmd.getCommandId().getMiniapp().equals(miniAppName));

		if (miniappCommands.isEmpty())
			throw new RuntimeException("could not find command of " + miniAppName + " mini app");

		return  miniappCommands;

    }

	 /**
	  * This method delete all command history.
	  *
	  */
	@Override
	public void deleteAllCommands() {
		this.dataBaseMockup.clear();
	}

}
