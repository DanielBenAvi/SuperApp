package superapp.miniapps.command;

import superapp.logic.boundaries.MiniAppCommandBoundary;

public interface MiniAppsCommand {

    public Object execute(MiniAppCommandBoundary commandBoundary);

    enum commands {
        // describe each is must
        UNKNOWN_COMMAND,
        LIKE_PROFILE,
        UNMATCH_PROFILE,
        GET_LIKES,
        GET_MATCHES,
        GET_POTENTIAL_DATES,
        GET_MY_EVENTS,
        JOIN_EVENT,
        LEAVE_EVENT,
        SEARCH_EVENTS_BY_NAME,
        SEARCH_EVENTS_BY_DATE,
        SEARCH_EVENTS_BY_PREFERENCES,
        GET_USER_DETAILS_BY_EMAIL,
        GET_EVENTS_BASED_ON_PREFERENCES,
        GET_EVENTS_CREATED_BY_ME,
        GET_ALL_FUTURE_EVENTS,

        SEARCH_PRODUCT_BY_PRICE,
        SEARCH_PRODUCT_BY_PREFERENCES,
        GET_ALL_MY_PRODUCTS,
        SEARCH_PRODUCT_BY_NAME,
        GET_PRODUCTS_BY_PREFERENCES,

    }

    static commands strToCommand(String command) {
        try {
            return commands.valueOf(command);
        } catch (Exception e) {
            return commands.UNKNOWN_COMMAND;
        }
    }
}
