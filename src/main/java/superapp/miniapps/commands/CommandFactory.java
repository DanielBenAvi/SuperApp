package superapp.miniapps.commands;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.UserCrud;
import superapp.logic.ObjectsService;
import superapp.logic.mongo.ObjectManagerMongoDB;
import superapp.logic.mongo.UserManagerMongoDB;

import static superapp.miniapps.commands.DatingCommand.LIKE;

@Component
public class CommandFactory {
    private final ObjectCrud objectCrud;
    private final UserCrud userCrud;
    private final ObjectsService objectsService;
    private final UserManagerMongoDB userRepository;
    private final ObjectManagerMongoDB objectRepository;

    // Commands
    private DatingLikeCommand likeCommand;


    @Autowired
    public CommandFactory(ObjectCrud objectCrud, UserCrud userCrud, ObjectsService objectsService,
                          UserManagerMongoDB userRepository, ObjectManagerMongoDB objectRepository, DatingLikeCommand likeCommand) {

        this.objectCrud = objectCrud;
        this.userCrud = userCrud;
        this.objectsService = objectsService;
        this.userRepository = userRepository;
        this.objectRepository = objectRepository;
        this.likeCommand = likeCommand;
    }

    @Autowired
    public void setDatingCommand(DatingLikeCommand likeCommand) {
        this.likeCommand = likeCommand;
    }

    @PostConstruct
    public void init() {
        System.err.println("Commands initiated");
    }

    public DatingCommand create(int commandCode, Object... params) {
        switch (commandCode)
        {
            case LIKE:
                return likeCommand;
            default:
                return null;
        }
    }
}
