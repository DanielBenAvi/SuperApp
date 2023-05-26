package superapp.logic;

import superapp.logic.boundaries.MiniAppCommandBoundary;

import java.util.List;

public interface MiniAppCommandWithPaging extends MiniAppCommandService{

    public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName,String userSuperapp, String userEmail, int size, int page);
    public List<MiniAppCommandBoundary> getAllCommands(String userSuperapp, String userEmail, int size, int page);
    public void deleteAllCommands(String userSuperapp, String userEmail);

}
