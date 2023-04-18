package superapp.logic;
import java.util.List;

import superapp.logic.boundaries.MiniAppCommandBoundary;

public interface MiniAppCommandService {
	
	Object invokeCommand(MiniAppCommandBoundary command);
	List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName);
	List<MiniAppCommandBoundary> getAllCommands();
	void deleteAllCommands();
}
