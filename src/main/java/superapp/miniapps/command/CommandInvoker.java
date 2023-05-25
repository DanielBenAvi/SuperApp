package superapp.miniapps.command;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.miniapps.command.datingimpl.*;
import superapp.miniapps.command.eventImpl.*;


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
    private EventSearchEventByLocation eventSearchEventByLocation;
    private EventSearchEventByPreferences eventSearchEventByPreferences;
    private EventGetEventsBaseOnPreferencesCommand eventGetEventsBaseOnPreferencesCommand;
    private EventGetCreatedByMeEventsCommand eventGetCreatedByMeEventsCommand;
    private EventGetAllFutureEventsCommand eventGetAllFutureEventsCommand;
    /******************** General Commands ********************/
    private GetUserDetailsCommand getUserDetailsCommand;

    @Autowired
    public CommandInvoker(DatingLikeProfileCommand datingLikeProfile,
                          DatingUnmatchProfileCommand datingUnmatch,
                          DatingGetPotentialDatesCommand datingGetPotentialDates,
                          DatingGetMatchesCommand datingGetMatches,
                          DatingGetLikesCommand datingGetLikes,
                          EventGetMyEventsCommand eventGetMyEventsCommand,
                          EventJoinEventCommand eventJoinEventCommand,
                          EventLeaveEventCommand eventLeaveEventCommand,
                          EventSearchEventByName eventSearchEventByName,
                          EventSearchEventByDate eventSearchEventByDate,
                          EventSearchEventByLocation eventSearchEventByLocation,
                          EventSearchEventByPreferences eventSearchEventByPreferences,
                          GetUserDetailsCommand getUserDetailsCommand,
                          EventGetEventsBaseOnPreferencesCommand eventGetEventsBaseOnPreferencesCommand,
                          EventGetCreatedByMeEventsCommand eventGetCreatedByMeEventsCommand,
                          EventGetAllFutureEventsCommand eventGetAllFutureEventsCommand) {

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
        this.eventSearchEventByLocation = eventSearchEventByLocation;
        this.eventSearchEventByPreferences = eventSearchEventByPreferences;
        this.getUserDetailsCommand = getUserDetailsCommand;
        this.eventGetEventsBaseOnPreferencesCommand = eventGetEventsBaseOnPreferencesCommand;
        this.eventGetCreatedByMeEventsCommand = eventGetCreatedByMeEventsCommand;
        this.eventGetAllFutureEventsCommand = eventGetAllFutureEventsCommand;
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
            case SEARCH_EVENTS_BY_LOCATION -> eventSearchEventByLocation;
            case SEARCH_EVENTS_BY_DATE -> eventSearchEventByDate;
            case SEARCH_EVENTS_BY_PREFERENCES -> eventSearchEventByPreferences;
            case GET_USER_DETAILS_BY_EMAIL -> getUserDetailsCommand;
            case GET_EVENTS_BASED_ON_PREFERENCES -> eventGetEventsBaseOnPreferencesCommand;
            case GET_EVENTS_CREATED_BY_ME -> eventGetCreatedByMeEventsCommand;
            case GET_ALL_FUTURE_EVENTS -> eventGetAllFutureEventsCommand;
            default -> null; // TODO create default command?
        };

    }
}
