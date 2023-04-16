package superapp.logic;

import java.util.List;
import org.springframework.stereotype.Service;
import superapp.logic.boundaries.MiniAppCommandBoundary;


public interface MiniAppCommandService {
	
	public Object invokeCommand(MiniAppCommandBoundary command);
	public List<MiniAppCommandBoundary> getAllCommands();
	public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName);
	public void deleteAllCommands();
}
