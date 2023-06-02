package superapp.miniapps.command;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.miniapps.command.datingimpl.*;
import superapp.miniapps.command.eventImpl.*;
import superapp.miniapps.command.marketplaceimpl.*;


@Component
public class CommandInvoker {

    /******************** Dating Commands ********************/
    private DatingLikeProfileCommand datingLikeProfile;
    private DatingUnmatchProfileCommand datingUnmatch;
    private DatingGetPotentialDatesCommand datingGetPotentialDates;
    private DatingGetMatchesCommand datingGetMatches;
    private DatingGetLikesCommand datingGetLikes;

    /******************** Event Commands ********************/
    private EventGetMyEventsCommand eventGetMyEventsCommand;
    private EventJoinEventCommand eventJoinEventCommand;
    private EventLeaveEventCommand eventLeaveEventCommand;
    private EventSearchEventByName eventSearchEventByName;
    private EventSearchEventByDate eventSearchEventByDate;
    private EventSearchEventByPreferences eventSearchEventByPreferences;
    private EventGetEventsBaseOnPreferencesCommand eventGetEventsBaseOnPreferencesCommand;
    private EventGetCreatedByMeEventsCommand eventGetCreatedByMeEventsCommand;
    private EventGetAllFutureEventsCommand eventGetAllFutureEventsCommand;

    /******************** Marketplace Commands ********************/

    private SearchProductByCategory searchProductByCategory;
    private SearchProductByPrice searchProductByPrice;

    private GetAllMyProducts getAllMyProducts;

    private SearchProductByName searchProductByName;
    private GetProductsByPreferences getProductsByPreferences;
    /******************** General Commands ********************/
    private GetUserDetailsCommand getUserDetailsCommand;
    private DefaultCommand defaultCommand;

    @Autowired
    public CommandInvoker(DefaultCommand defaultCommand,
                          DatingLikeProfileCommand datingLikeProfile,
                          DatingUnmatchProfileCommand datingUnmatch,
                          DatingGetPotentialDatesCommand datingGetPotentialDates,
                          DatingGetMatchesCommand datingGetMatches,
                          DatingGetLikesCommand datingGetLikes,
                          EventGetMyEventsCommand eventGetMyEventsCommand,
                          EventJoinEventCommand eventJoinEventCommand,
                          EventLeaveEventCommand eventLeaveEventCommand,
                          EventSearchEventByName eventSearchEventByName,
                          EventSearchEventByDate eventSearchEventByDate,
                          EventSearchEventByPreferences eventSearchEventByPreferences,
                          GetUserDetailsCommand getUserDetailsCommand,
                          EventGetEventsBaseOnPreferencesCommand eventGetEventsBaseOnPreferencesCommand,
                          EventGetCreatedByMeEventsCommand eventGetCreatedByMeEventsCommand,
                          EventGetAllFutureEventsCommand eventGetAllFutureEventsCommand,
                          SearchProductByPrice searchProductByPrice,
                          SearchProductByCategory searchProductByCategory,
                          GetAllMyProducts getAllMyProducts,
                          SearchProductByName searchProductByName,
                          GetProductsByPreferences getProductsByPreferences) {


        this.datingLikeProfile = datingLikeProfile;
        this.datingUnmatch = datingUnmatch;
        this.datingGetPotentialDates = datingGetPotentialDates;
        this.datingGetMatches = datingGetMatches;
        this.datingGetLikes = datingGetLikes;
        this.eventGetMyEventsCommand = eventGetMyEventsCommand;
        this.eventJoinEventCommand = eventJoinEventCommand;
        this.eventLeaveEventCommand = eventLeaveEventCommand;
        this.eventSearchEventByName = eventSearchEventByName;
        this.eventSearchEventByDate = eventSearchEventByDate;
        this.eventSearchEventByPreferences = eventSearchEventByPreferences;
        this.getUserDetailsCommand = getUserDetailsCommand;
        this.eventGetEventsBaseOnPreferencesCommand = eventGetEventsBaseOnPreferencesCommand;
        this.eventGetCreatedByMeEventsCommand = eventGetCreatedByMeEventsCommand;
        this.eventGetAllFutureEventsCommand = eventGetAllFutureEventsCommand;
        this.searchProductByCategory = searchProductByCategory;
        this.searchProductByPrice = searchProductByPrice;
        this.getAllMyProducts = getAllMyProducts;
        this.searchProductByName = searchProductByName;
        this.getProductsByPreferences = getProductsByPreferences;
        this.defaultCommand = defaultCommand;
    }


    @PostConstruct
    public void init() {
        System.err.println("****** " + this.getClass().getName() + " initiated");
    }

    public MiniAppsCommand create(MiniAppsCommand.commands commandCode, Object... params) {

        return switch (commandCode) {
            case LIKE_PROFILE -> datingLikeProfile;
            case UNMATCH_PROFILE -> datingUnmatch;
            case GET_LIKES -> datingGetLikes;
            case GET_MATCHES -> datingGetMatches;
            case GET_POTENTIAL_DATES -> datingGetPotentialDates;
            case GET_MY_EVENTS -> eventGetMyEventsCommand;
            case JOIN_EVENT -> eventJoinEventCommand;
            case LEAVE_EVENT -> eventLeaveEventCommand;
            case SEARCH_EVENTS_BY_NAME -> eventSearchEventByName;
            case SEARCH_EVENTS_BY_DATE -> eventSearchEventByDate;
            case SEARCH_EVENTS_BY_PREFERENCES -> eventSearchEventByPreferences;
            case GET_USER_DETAILS_BY_EMAIL -> getUserDetailsCommand;
            case GET_EVENTS_BASED_ON_PREFERENCES -> eventGetEventsBaseOnPreferencesCommand;
            case GET_EVENTS_CREATED_BY_ME -> eventGetCreatedByMeEventsCommand;
            case GET_ALL_FUTURE_EVENTS -> eventGetAllFutureEventsCommand;
            case GET_ALL_MY_PRODUCTS -> getAllMyProducts;
            case GET_PRODUCTS_BY_PREFERENCES -> getProductsByPreferences;
            case SEARCH_PRODUCT_BY_NAME -> searchProductByName;
            case SEARCH_PRODUCT_BY_PRICE -> searchProductByPrice;
            case SEARCH_PRODUCT_BY_PREFERENCES -> searchProductByCategory;
            default -> defaultCommand;
        };
    }
}
