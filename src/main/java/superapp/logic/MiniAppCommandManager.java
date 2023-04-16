package superapp.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import superapp.data.entities.MiniAppCommandEntity;
import superapp.logic.boundaries.MiniAppCommandBoundary;

 @Service
public class MiniAppCommandManager implements MiniAppCommandService {

	
	private Map<String, MiniAppCommandEntity> dataBaseMockup;
	private String superAppName;
	
	
    //injects a configuration value of spring
    @Value("${spring.application.name:defaultAppName}")
    public void setApplicationName(String springApplicationName) {
        this.superAppName = springApplicationName;
    }
      
    //init the database mockup
    @PostConstruct
    public void init(){
        this.dataBaseMockup = Collections.synchronizedMap(new HashMap<>());
        System.err.println("****** "+ this.superAppName);
    }
	
	public MiniAppCommandBoundary convertToBoundry(MiniAppCommandEntity commandEntity) {
		MiniAppCommandBoundary cmd = new MiniAppCommandBoundary();
		cmd.setCommandId(ConvertHelp.convertStrToCmd(commandEntity.getCommandId()));
		cmd.setCommand(commandEntity.getCommand());
		cmd.setCommandAttributes(commandEntity.getCommandAttributes());
		cmd.setTargetObject(ConvertHelp.convertStrToTargetObject(commandEntity.getTargetObject()));
		cmd.setInvocationTimestamp(commandEntity.getInvocationTimestamp());
		cmd.setInvokedBy(ConvertHelp.convertStrToInvokedBy(commandEntity.getInvokedBy()));
		return cmd;
	}
	
	public MiniAppCommandEntity convertToEntity(MiniAppCommandBoundary cmdBoundary) {
		MiniAppCommandEntity cmdEntity = new MiniAppCommandEntity();
		cmdEntity.setCommand(cmdBoundary.getCommand());
		cmdEntity.setCommandId(ConvertHelp.convertCmdIDtoStr(cmdBoundary.getCommandId()));
		cmdEntity.setCommandAttributes(cmdBoundary.getCommandAttributes());
		cmdEntity.setInvocationTimestamp(cmdBoundary.getInvocationTimestamp());
		cmdEntity.setTargetObject(ConvertHelp.convertTargetObjToStr(cmdBoundary.getTargetObject()));
		cmdEntity.setInvokedBy(ConvertHelp.convertInvokedByToStr(cmdBoundary.getInvokedBy()));
		
		return cmdEntity;
		
	}
	
	
	@Override
	//TODO empty json
	public Object invokeCommand(MiniAppCommandBoundary command) {
		Map<String, MiniAppCommandEntity> defualtJSON = new HashMap<String, MiniAppCommandEntity>();
		if(command.equals(null)) {
			defualtJSON.put(command.toString(), null);
			return null;
		}
		else {
			MiniAppCommandEntity cmd = new MiniAppCommandEntity();
			cmd  = convertToEntity(command);
			this.dataBaseMockup.put(cmd.getCommandId(), cmd);
			return cmd; 
		}
	}

	@Override
	public List<MiniAppCommandBoundary> getAllCommands() {
		return this.dataBaseMockup.values()
				.stream().map(this::convertToBoundry).toList();
	}

	@Override//Override
    public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName) {
        List<MiniAppCommandBoundary> specificCommands = new ArrayList<MiniAppCommandBoundary>();
        if(!this.dataBaseMockup.containsKey(miniAppName)) {
            System.err.println("there is not a mini app with that name. ");
        }
        else {
            for(Entry<String, MiniAppCommandEntity> entry: dataBaseMockup.entrySet())
            {
                if(entry.getKey().equalsIgnoreCase(miniAppName))
                    specificCommands.add(convertToBoundry(entry.getValue()));
            }
        }
        return specificCommands;
    }
	

	@Override
	public void deleteAllCommands() {
		this.dataBaseMockup.clear();
		
	}
	
}
