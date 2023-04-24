package superapp.logic.mongo;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import superapp.dal.UserCrud;
import superapp.dal.entities.UserEntity;
import superapp.logic.ConvertHelp;
import superapp.logic.UsersService;
import superapp.logic.boundaries.UserBoundary;
import superapp.logic.boundaries.UserID;

import java.util.*;

@Service
public class UserManagerMongoDB implements UsersService {

    private String superappName;
    private UserCrud usersCrudDB;

    /**
     * constructor
     * @param usersCrudDB
     */
    @Autowired
    public UserManagerMongoDB(UserCrud usersCrudDB) {
        this.usersCrudDB = usersCrudDB;
    }

    /**
     * injects a configuration value of spring
     */
    @Value("${spring.application.name:defaultAppName}")
    public void setApplicationName(String springApplicationName) {
        this.superappName = springApplicationName;
    }

    /**
     * init the database mockup
     */
    @PostConstruct
    public void init(){
        System.err.println("****** " + this.getClass().getName() + " initiated");
    }

    /**
     * converts user boundary to user entity
     * @param userBoundary boundary
     * @return userEntity
     *
     */
    private UserEntity boundaryToEntity(UserBoundary userBoundary) {
        UserEntity userEntity = new UserEntity();

        String email = userBoundary.getUserId().getEmail();
        String userID = ConvertHelp.userIdBoundaryToStr(new UserID(superappName,email));

        userEntity.setUserID(userID);

        userEntity.setUserName(userBoundary.getUsername());

        userEntity.setAvatar(userBoundary.getAvatar());

        userEntity.setRole(ConvertHelp.strToUserRole(userBoundary.getRole()));


        return userEntity;
    }

    /**
     * converts user entity to user boundary
     * @param userEntity entity
     * @return userBoundary
     */
    private UserBoundary entityToBoundary(UserEntity userEntity) {

        UserBoundary userBoundary = new UserBoundary();

        // crate a userID object
        UserID userID = ConvertHelp.strUserIdToBoundary(userEntity.getUserID());

        // set the userID object
        userBoundary.setUserId(userID);

        // set the rest of the fields
        userBoundary.setUsername(userEntity.getUserName());
        userBoundary.setRole(ConvertHelp.userRoleToStr(userEntity.getRole()));
        userBoundary.setAvatar(userEntity.getAvatar());

        return userBoundary;
    }

    /**
     * Creates new user entity from user boundary
     * @param userBoundary boundary
     * @return userBoundary
     */
    @Override
    public UserBoundary createUser(UserBoundary userBoundary) {

        if (userBoundary == null){
            throw new RuntimeException("null UserBoundary can't be created");
        }

        if (userBoundary.getUserId().getEmail() == null){
            throw new RuntimeException("null Email can't be created");
        }

        if (userBoundary.getRole() == null){
            throw new RuntimeException("null Roll can't be created");
        }

        if (userBoundary.getUsername() == null){
            throw new RuntimeException("null UserName can't be created");
        }

        if (userBoundary.getAvatar() == null){
            throw new RuntimeException("null Avatar can't be created");
        }

        UserEntity userEntity = this.boundaryToEntity(userBoundary);

        // check if user already exist
        if (this.usersCrudDB.existsById(userEntity.getUserID()))
            throw new RuntimeException("User already exists: " + userEntity.getUserID());

        this.usersCrudDB.save(userEntity);

        return this.entityToBoundary(userEntity);
    }


    /**
     * login with specific user
     * @param userSuperApp app name
     * @param userEmail user email
     * @return Optional of UserBoundary
     */
    @Override
    public Optional<UserBoundary> login(String userSuperApp, String userEmail) {


        String userID = ConvertHelp.concatenateIds(new String[]{ userSuperApp,userEmail});

        UserEntity userEntity = this.usersCrudDB
                .findById(userID)
                .orElseThrow(() -> new RuntimeException("Couldn't find user by id  " + userID));

        if (userEntity == null){
            return Optional.empty();
        } else {
            UserBoundary userBoundary = this.entityToBoundary(userEntity);
            return Optional.of(userBoundary);
        }
    }

    /**
     * update the user
     * checks if attributes changed and update if needed
     * @param userSuperApp user app name
     * @param userEmail user mail
     * @param update user boundary with change attributes
     * @return userBoundary
     */
    @Override
    public UserBoundary updateUser(String userSuperApp, String userEmail, UserBoundary update) {

        String userID = ConvertHelp.concatenateIds(new String[]{userEmail, userSuperApp});

        UserEntity existing = this.usersCrudDB
                .findById(userID)
                .orElseThrow(() -> new RuntimeException("Couldn't find user by id  " + userID));

        //todo is necessary?
//        if (existing == null){
//            throw new RuntimeException("Couldn't find user by id: "+userID);
//        }

        boolean dirtyFlag = false;

        if (update.getUsername() != null){
            existing.setUserName(update.getUsername());
            dirtyFlag = true;
        }

        if (update.getRole()!= null){
            existing.setRole(ConvertHelp.strToUserRole(update.getRole()));
            dirtyFlag = true;
        }

        if (update.getAvatar() != null){
            existing.setAvatar(update.getAvatar());
            dirtyFlag = true;
        }

        if (dirtyFlag){
            existing = this.usersCrudDB.save(existing);
            //this.databaseMockup.put(userID, existing);
        }

        return this.entityToBoundary(existing);
    }

    /**
     * Get all users from database
     *
     * @return list of all user boundaries
     */
    @Override
    public List<UserBoundary> getAllUsers() {
        return this.usersCrudDB
                .findAll()
                .stream()
                .map(this::entityToBoundary)
                .toList();
    }

    /**
     * delete all users from database
     */
    @Override
    public void deleteAllUsers() {
        this.usersCrudDB.deleteAll();
    }

}
