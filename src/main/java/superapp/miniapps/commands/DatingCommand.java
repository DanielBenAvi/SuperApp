package superapp.miniapps.commands;

import superapp.logic.boundaries.MiniAppCommandBoundary;

public interface DatingCommand {
    int LIKE = 1;
    int UNKNOWN = 2;

    public Object execute(MiniAppCommandBoundary commandBoundary);
}
