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
        GET_POTENTIAL_DATES
    }
    //    int LIKE_PROFILE = 1;
//    int UNLIKE_PROFILE = 2;
//    int UNMATCH_PROFILE = 3;
//    int ACTIVATE_PROFILE = 4;
//    int DEACTIVATE_PROFILE = 5;
//    int CREATE_PROFILE = 6;
//    int EDIT_PROFILE = 7;
//    int GET_PROFILE = 8;
//    int GET_LIKES = 9;
//    int GET_MATCHES = 10;
//    int GET_POTENTIAL_DATES = 11;
    static commands strToCommand(String command) {
        try {
            return commands.valueOf(command);
        }catch (Exception e){
            return commands.UNKNOWN_COMMAND;
        }
    }
}
