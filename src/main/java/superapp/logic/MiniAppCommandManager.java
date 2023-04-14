package superapp.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import superapp.data.entities.MiniAppCommandEntity;
import superapp.logic.boundaries.MiniAppCommandBoundary;

public class MiniAppCommandManager implements MiniAppCommandService {

	
	private Map<String, MiniAppCommandEntity> dataBaseMockup;
	
	public MiniAppCommandManager() {
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
	
	@Override
	//TODO empty json
	public Object invokeCommand(MiniAppCommandBoundary command) {
		Map<String, MiniAppCommandEntity> defualtJSON = new HashMap<String, MiniAppCommandEntity>();
		if(command.equals(null))
			defualtJSON.put(command.toString(), null);
		return null;
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
