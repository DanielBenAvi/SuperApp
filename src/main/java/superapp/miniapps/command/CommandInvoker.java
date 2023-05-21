package superapp.miniapps.command;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.miniapps.command.datingimpl.*;
import superapp.miniapps.command.eventImpl.*;


@Component
public class CommandInvoker {

    // Commands dating
    private DatingLikeProfileCommand datingLikeProfile;
    private DatingUnmatchProfileCommand datingUnmatch;
    private DatingUnlikeProfileCommand datingUnlikeProfile;
    private DatingGetProfileCommand datingGetProfile;
    private DatingGetPotentialDatesCommand datingGetPotentialDates;
    private DatingGetMatchesCommand datingGetMatches;
    private DatingGetLikesCommand datingGetLikes;
    private DatingEditProfileCommand datingEditProfile;
    private DatingDeactivateProfileCommand datingDeactivateProfile;
    private DatingCreateProfileCommand datingCreateProfile;
    private DatingActivateProfileCommand datingActivateProfile;

    private EventGetMyEventsCommand eventGetMyEventsCommand;

    private EventJoinEventCommand eventJoinEventCommand;
    private EventLeaveEventCommand eventLeaveEventCommand;
    private EventSearchEventByName eventSearchEventByName;
    private EventSearchEventByDate eventSearchEventByDate;
    private EventSearchEventByLocation eventSearchEventByLocation;
    private EventSearchEventByPreferences eventSearchEventByPreferences;

    private GetUserDetailsCommand getUserDetailsCommand;
    // Events command

    @Autowired
    public CommandInvoker(DatingLikeProfileCommand datingLikeProfile,
                          DatingUnmatchProfileCommand datingUnmatch,
                          DatingUnlikeProfileCommand datingUnlikeProfile,
                          DatingGetProfileCommand datingGetProfile,
                          DatingGetPotentialDatesCommand datingGetPotentialDates,
                          DatingGetMatchesCommand datingGetMatches,
                          DatingGetLikesCommand datingGetLikes,
                          DatingEditProfileCommand datingEditProfile,
                          DatingDeactivateProfileCommand datingDeactivateProfile,
                          DatingCreateProfileCommand datingCreateProfile,
                          DatingActivateProfileCommand datingActivateProfile,
                          EventGetMyEventsCommand eventGetMyEventsCommand,
                          EventJoinEventCommand eventJoinEventCommand,
                          EventLeaveEventCommand eventLeaveEventCommand,
                          EventSearchEventByName eventSearchEventByName,
                          EventSearchEventByDate eventSearchEventByDate,
                          EventSearchEventByLocation eventSearchEventByLocation,
                          EventSearchEventByPreferences eventSearchEventByPreferences,
                          GetUserDetailsCommand getUserDetailsCommand) {

        this.datingLikeProfile = datingLikeProfile;
        this.datingUnmatch = datingUnmatch;
        this.datingUnlikeProfile = datingUnlikeProfile;
        this.datingGetProfile = datingGetProfile;
        this.datingGetPotentialDates = datingGetPotentialDates;
        this.datingGetMatches = datingGetMatches;
        this.datingGetLikes = datingGetLikes;
        this.datingEditProfile = datingEditProfile;
        this.datingDeactivateProfile = datingDeactivateProfile;
        this.datingCreateProfile = datingCreateProfile;
        this.datingActivateProfile = datingActivateProfile;
        this.eventGetMyEventsCommand = eventGetMyEventsCommand;
        this.eventJoinEventCommand = eventJoinEventCommand;
        this.eventLeaveEventCommand = eventLeaveEventCommand;
        this.eventSearchEventByName = eventSearchEventByName;
        this.eventSearchEventByDate = eventSearchEventByDate;
        this.eventSearchEventByLocation = eventSearchEventByLocation;
        this.eventSearchEventByPreferences = eventSearchEventByPreferences;
        this.getUserDetailsCommand = getUserDetailsCommand;
    }


    @PostConstruct
    public void init() {
        System.err.println("****** " + this.getClass().getName() + " initiated");
    }

    public MiniAppsCommand create(MiniAppsCommand.commands commandCode, Object... params) {


        return switch (commandCode) {
            case LIKE_PROFILE -> datingLikeProfile;
            case UNLIKE_PROFILE -> datingUnlikeProfile;
            case UNMATCH_PROFILE -> datingUnmatch;
            case ACTIVATE_PROFILE -> datingActivateProfile;
            case DEACTIVATE_PROFILE -> datingDeactivateProfile;
            case CREATE_PROFILE -> datingCreateProfile;
            case EDIT_PROFILE -> datingEditProfile;
            case GET_PROFILE -> datingGetProfile;
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
            default -> null; // create default command?
        };

    }
}
