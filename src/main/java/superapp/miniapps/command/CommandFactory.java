package superapp.miniapps.command;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.UserCrud;
import superapp.logic.ObjectsService;
import superapp.logic.mongo.ObjectManagerMongoDB;
import superapp.logic.mongo.UserManagerMongoDB;
import superapp.miniapps.datingMiniApp.command.DatingCommand;
import superapp.miniapps.datingMiniApp.command.impl.DatingLikeProfileCommand;


@Component
public class CommandFactory {
    private final ObjectCrud objectCrud;
    private final UserCrud userCrud;
    private final ObjectsService objectsService;
    private final UserManagerMongoDB userRepository;
    private final ObjectManagerMongoDB objectRepository;

    // Commands
    private DatingLikeProfileCommand likeCommand;


    @Autowired
    public CommandFactory(ObjectCrud objectCrud, UserCrud userCrud, ObjectsService objectsService,
                          UserManagerMongoDB userRepository, ObjectManagerMongoDB objectRepository,
                          DatingLikeProfileCommand likeCommand) {

        this.objectCrud = objectCrud;
        this.userCrud = userCrud;
        this.objectsService = objectsService;
        this.userRepository = userRepository;
        this.objectRepository = objectRepository;
        this.likeCommand = likeCommand;
    }

    @Autowired
    public void setDatingCommand(DatingLikeProfileCommand likeCommand) {
        this.likeCommand = likeCommand;
    }

    @PostConstruct
    public void init() {
        System.err.println("****** All commands initiated");
    }

    public DatingCommand create(int commandCode, Object... params) {
        switch (commandCode)
        {
            case DatingCommand.LIKE_PROFILE:
                return likeCommand;
            default:
                return null;
        }
    }
}
