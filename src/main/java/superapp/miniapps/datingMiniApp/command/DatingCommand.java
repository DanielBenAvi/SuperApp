package superapp.miniapps.datingMiniApp.command;

import superapp.miniapps.command.ApplicationCommand;

public interface DatingCommand extends ApplicationCommand {

    int LIKE_PROFILE = 1;
    int UNLIKE_PROFILE = 2;
    int UNMATCH_PROFILE = 3;
    int ACTIVATE_PROFILE = 4;
    int DEACTIVATE_PROFILE = 5;
    int CREATE_PROFILE = 6;
    int EDIT_PROFILE = 7;
    int GET_PROFILE = 8;
    int GET_LIKES = 9;
    int GET_MATCHES = 10;
    int GET_POTENTIAL_DATES = 11;

}
