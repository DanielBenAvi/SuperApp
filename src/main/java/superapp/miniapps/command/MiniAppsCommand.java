package superapp.miniapps.command;

import superapp.logic.boundaries.MiniAppCommandBoundary;

public interface MiniAppsCommand {

    public Object execute(MiniAppCommandBoundary commandBoundary);

    enum commands {
        UNKNOWN_COMMAND,
        LIKE_PROFILE,
        UNLIKE_PROFILE,
        UNMATCH_PROFILE,
        ACTIVATE_PROFILE,
        DEACTIVATE_PROFILE,
        CREATE_PROFILE,
        EDIT_PROFILE,
        GET_PROFILE,
        GET_LIKES,
        GET_MATCHES,
        GET_POTENTIAL_DATES,
        GET_MY_EVENTS // describe each is must
    }
    static commands strToCommand(String command) {
        try {
            return commands.valueOf(command);
        }catch (Exception e){
            return commands.UNKNOWN_COMMAND;
        }
    }
}
