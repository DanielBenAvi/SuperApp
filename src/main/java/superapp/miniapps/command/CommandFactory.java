package superapp.miniapps.command;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.UserCrud;
import superapp.logic.ObjectsServiceWithPaging;
import superapp.logic.mongo.ObjectManagerMongoDB;
import superapp.logic.mongo.UserManagerMongoDB;
import superapp.miniapps.command.datingimpl.*;


@Component
public class CommandFactory {
    private final ObjectCrud objectCrud;
    private final UserCrud userCrud;
    private final ObjectsServiceWithPaging objectsService;
    private final UserManagerMongoDB userRepository;
    private final ObjectManagerMongoDB objectRepository;

    // Commands
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


    @PostConstruct
    public void setDatingCommand(DatingLikeProfileCommand datingLikeProfile,
                          DatingUnmatchProfileCommand datingUnmatch,
                          DatingUnlikeProfileCommand datingUnlikeProfile,
                          DatingGetProfileCommand datingGetProfile,
                          DatingGetPotentialDatesCommand datingGetPotentialDates,
                          DatingGetMatchesCommand datingGetMatches,
                          DatingGetLikesCommand datingGetLikes,
                          DatingEditProfileCommand datingEditProfile,
                          DatingDeactivateProfileCommand datingDeactivateProfile,
                          DatingCreateProfileCommand datingCreateProfile,
                          DatingActivateProfileCommand datingActivateProfile) {
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
    }

    @Autowired
    public CommandFactory(ObjectCrud objectCrud, UserCrud userCrud, ObjectsServiceWithPaging objectsService,
                          UserManagerMongoDB userRepository, ObjectManagerMongoDB objectRepository) {

        this.objectCrud = objectCrud;
        this.userCrud = userCrud;
        this.objectsService = objectsService;
        this.userRepository = userRepository;
        this.objectRepository = objectRepository;
    }


    @PostConstruct
    public void init() {
        System.err.println("****** All commands initiated");
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
            default:
                return null; // create default command?
        }

    }
}
