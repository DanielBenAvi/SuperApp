package superapp.miniapps.command;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.miniapps.command.datingimpl.*;
import superapp.miniapps.command.eventImpl.EventGetMyEventsCommand;
import superapp.miniapps.command.eventImpl.EventJoinEventCommand;


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
                          EventJoinEventCommand eventJoinEventCommand
    ) {

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
    }


    @PostConstruct
    public void init() {
        System.err.println("****** " + this.getClass().getName() + " initiated");
    }

    public MiniAppsCommand create(MiniAppsCommand.commands commandCode, Object... params) {


        switch (commandCode) {
            case LIKE_PROFILE:
                return datingLikeProfile;
            case UNLIKE_PROFILE:
                return datingUnlikeProfile;
            case UNMATCH_PROFILE:
                return datingUnmatch;
            case ACTIVATE_PROFILE:
                return datingActivateProfile;
            case DEACTIVATE_PROFILE:
                return datingDeactivateProfile;
            case CREATE_PROFILE:
                return datingCreateProfile;
            case EDIT_PROFILE:
                return datingEditProfile;
            case GET_PROFILE:
                return datingGetProfile;
            case GET_LIKES:
                return datingGetLikes;
            case GET_MATCHES:
                return datingGetMatches;
            case GET_POTENTIAL_DATES:
                return datingGetPotentialDates;
            case GET_MY_EVENTS:
                return eventGetMyEventsCommand;
            case JOIN_EVENT:
                return eventJoinEventCommand;
            default:
                return null; // create default command?
        }

    }
}
