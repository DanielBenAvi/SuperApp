package superapp.logic.mongo;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import superapp.dal.UserCrud;
import superapp.dal.UserRole;
import superapp.dal.entities.UserEntity;
import superapp.logic.ConvertHelp;
import superapp.logic.exeptions.UserBadRequestException;
import superapp.logic.exeptions.UserNotFoundException;
import superapp.logic.UsersService;
import superapp.logic.boundaries.UserBoundary;
import superapp.logic.boundaries.UserID;

import java.util.*;
import java.util.regex.Pattern;

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
        System.err.println("****** " + this.getClass().getName() + " service initiated");
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
     * This method creates-register new user entity from user boundary
     *
     * @param userBoundary UserBoundary
     * @return UserBoundary
     */
    @Override
    public UserBoundary createUser(UserBoundary userBoundary) {

        // this verifying never reached
//        if (userBoundary == null){
//            throw new RuntimeException("null UserBoundary can't be created");
//        }

        // validate UserBoundary attr
        if (!isValidEmail(userBoundary.getUserId().getEmail()))
            throw new UserBadRequestException("Email invalid");

        if (!isValidRole(userBoundary.getRole()))
            throw new UserBadRequestException("Role invalid");

        if (!isValidUserName(userBoundary.getUsername()))
            throw new UserBadRequestException("User name invalid");

        if (!isValidAvatar(userBoundary.getAvatar()))
            throw new UserBadRequestException("Avatar invalid");


        userBoundary.getUserId().setSuperapp(superappName);

        UserEntity userEntity = this.boundaryToEntity(userBoundary);

        // check if user already exist
        if (this.usersCrudDB.existsById(userEntity.getUserID()))
            throw new UserBadRequestException("User with id " + userEntity.getUserID() + " already exists");

        this.usersCrudDB.save(userEntity);

        return this.entityToBoundary(userEntity);
    }


    /**
     * This method login with specific user
     * @param userSuperApp app name
     * @param userEmail user email
     * @return Optional of UserBoundary
     */
    @Override
    public Optional<UserBoundary> login(String userSuperApp, String userEmail) {


        String userID = ConvertHelp.concatenateIds(new String[]{ userSuperApp, userEmail});

        UserEntity userEntity = this.usersCrudDB
                .findById(userID)
                .orElseThrow(() -> new UserNotFoundException("Couldn't find user by id  " + userID));

        if (userEntity == null) {
            return Optional.empty();
        }
        else {
            UserBoundary userBoundary = this.entityToBoundary(userEntity);
            return Optional.of(userBoundary);
        }

    }

    /**
     * This method update user entity in DB
     * checks if attributes changed and update if needed
     *
     * @param userSuperApp user app name
     * @param userEmail user mail
     * @param update user boundary with change attributes
     * @return userBoundary
     */
    @Override
    public UserBoundary updateUser(String userSuperApp, String userEmail, UserBoundary update) {

        String userID = ConvertHelp.concatenateIds(new String[]{userEmail, userSuperApp});

        // get user from DB and check if is null
        UserEntity existing = this.usersCrudDB
                .findById(userID)
                .orElseThrow(() -> new UserNotFoundException("Couldn't find user by id  " + userID));


        boolean dirtyFlag = false;

        if (update.getUsername() != null) {

            if (!isValidUserName(update.getUsername()))
                throw new UserBadRequestException("User name invalid");

            existing.setUserName(update.getUsername());
            dirtyFlag = true;
        }

        if (update.getRole()!= null) {

            if (!isValidRole(update.getRole()))
                throw new UserBadRequestException("Role invalid");

            existing.setRole(ConvertHelp.strToUserRole(update.getRole()));
            dirtyFlag = true;
        }

        if (update.getAvatar() != null) {

            if (!isValidAvatar(update.getAvatar()))
                throw new UserBadRequestException("Avatar invalid");

            existing.setAvatar(update.getAvatar());
            dirtyFlag = true;
        }


        if (dirtyFlag)
            existing = this.usersCrudDB.save(existing);

        return this.entityToBoundary(existing);
    }

    /**
     * This method return all users from database
     *
     * @return List<UserBoundary>
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
     * This method delete all users from database
     */
    @Override
    public void deleteAllUsers() {
        this.usersCrudDB.deleteAll();
    }



    /**
     * This method validate the username isn`t null or empty.
     *
     * @param username String
     * @return boolean
     */
    private boolean isValidUserName(String username) {

        if (username == null || username.isEmpty())
            return false;

        return true;
    }

    /**
     * This method validate the avatar isn`t null or empty.
     *
     * @param avatar String
     * @return boolean
     */
    private boolean isValidAvatar(String avatar) {

        if (avatar == null || avatar.isEmpty())
            return false;

        return true;
    }

    /**
     * This method validate the user role isn`t null, empty, or invalid role.
     *
     * @param role String
     * @return boolean
     */
    private boolean isValidRole(String role) {

        if (role == null || role.isEmpty())
            return false;

        try {
            UserRole.valueOf(role);
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * This method validate the email isn`t null, empty, or invalid email.
     *
     * @param email String
     * @return boolean
     */
    private boolean isValidEmail(String email) {


        if (email == null || email.isEmpty())
            return false;

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."
                + "[a-zA-Z0-9_+&*-]+)*@"
                + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
                + "A-Z]{2,7}$";

        Pattern emailPattern = Pattern.compile(emailRegex);

        // check email format
        if (!emailPattern.matcher(email).matches())
            return false;

        return true;
    }

}