package superapp.logic;
import java.util.List;

import superapp.logic.boundaries.MiniAppCommandBoundary;

public interface MiniAppCommandService {
	
	public Object invokeCommand(MiniAppCommandBoundary command);
	@Deprecated
	public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName);
	@Deprecated
	public List<MiniAppCommandBoundary> getAllCommands();
	@Deprecated
	public void deleteAllCommands();
}
