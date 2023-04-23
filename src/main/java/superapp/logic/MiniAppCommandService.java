package superapp.logic;
import java.util.List;

import superapp.logic.boundaries.MiniAppCommandBoundary;

public interface MiniAppCommandService {
	
	public Object invokeCommand(MiniAppCommandBoundary command);
	public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName);
	public List<MiniAppCommandBoundary> getAllCommands();
	void deleteAllCommands();
}
