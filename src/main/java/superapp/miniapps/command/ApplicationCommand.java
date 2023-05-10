package superapp.miniapps.command;

import superapp.logic.boundaries.MiniAppCommandBoundary;

public interface ApplicationCommand {

    int UNKNOWN_COMMAND = 0;

    public Object execute(MiniAppCommandBoundary commandBoundary);

}
