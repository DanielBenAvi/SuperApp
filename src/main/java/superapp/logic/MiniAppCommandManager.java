package superapp.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import superapp.data.entities.MiniAppCommandEntity;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.ConvertHelp;

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
	public Object invokeCommand(MiniAppCommandBoundary command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MiniAppCommandBoundary> getAllCommands() {
		return this.dataBaseMockup.values()
				.stream().map(this::convertToBoundry).toList();
	}

	@Override
	public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName) {
		boolean check = true;
		List<MiniAppCommandBoundary> specificCommands = new ArrayList<MiniAppCommandBoundary>();
		if(!this.dataBaseMockup.containsKey(miniAppName)) {
			check = false;
			System.err.println("there is not a mini app with that name. ");
		}		
		return null;
	}

	@Override
	public void deleteAllCommands() {
		this.dataBaseMockup.clear();
		
	}
	
}
