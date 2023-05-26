package superapp.logic.mongo;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import superapp.data.UserCrud;
import superapp.data.UserEntity;
import superapp.data.UserRole;
import superapp.logic.UserServiceWithPaging;
import superapp.logic.boundaries.UserBoundary;
import superapp.logic.utils.convertors.ConvertIdsHelper;
import superapp.logic.utils.convertors.UserConvertor;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserManagerMongoDB implements UserServiceWithPaging {

    private String superappName;
    private final UserCrud usersCrudDB;
    private final UserConvertor userConvertor;
    private RBAC accessControl;


    @Autowired
    public UserManagerMongoDB(UserCrud usersCrudDB,
                              UserConvertor userConvertor,
                              RBAC accessControl) {

        this.usersCrudDB = usersCrudDB;
        this.userConvertor = userConvertor;
        this.accessControl = accessControl;
    }

    /**
     * injects a configuration value of spring
     */
    @Value("${spring.application.name:defaultAppName}")
    public void setApplicationName(String springApplicationName) {
        this.superappName = springApplicationName;
    }


    @PostConstruct
    public void init(){
        System.err.println("****** " + this.getClass().getName() + " service initiated");
    }


    /**
     * This method creates-register new user entity from user boundary
     *
     * @param userBoundary UserBoundary
     * @return UserBoundary
     */
    @Override
    public UserBoundary createUser(UserBoundary userBoundary) {

        // validate UserBoundary attr
        if (!isValidEmail(userBoundary.getUserId().getEmail()))
            throw new BadRequestException("Email invalid");

        if (!isValidRole(userBoundary.getRole()))
            throw new BadRequestException("Role invalid");

        if (!isValidUserName(userBoundary.getUsername()))
            throw new BadRequestException("User name invalid");

        if (!isValidAvatar(userBoundary.getAvatar()))
            throw new BadRequestException("Avatar invalid");


        userBoundary.getUserId().setSuperapp(superappName);

        UserEntity userEntity = this.userConvertor.toEntity(userBoundary);

        // check if user already exist
        if (this.usersCrudDB.existsById(userEntity.getUserID()))
            throw new ConflictRequestException("User with id " + userEntity.getUserID() + " already exists");

        this.usersCrudDB.save(userEntity);

        return this.userConvertor.toBoundary(userEntity);
    }


    /**
     * This method login with specific user
     * @param userSuperApp app name
     * @param userEmail user email
     * @return Optional of UserBoundary
     */
    @Override
    public Optional<UserBoundary> login(String userSuperApp, String userEmail) {


        String userID = ConvertIdsHelper.concatenateIds(new String[]{ userSuperApp, userEmail});

        UserEntity userEntity = this.usersCrudDB
                .findById(userID)
                .orElseThrow(() -> new NotFoundException("Couldn't find user by id  " + userID));

        if (userEntity == null) {
            return Optional.empty();
        }
        else {
            UserBoundary userBoundary = this.userConvertor.toBoundary(userEntity);
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

        String userID = ConvertIdsHelper.concatenateIds(new String[]{userEmail, userSuperApp});

        // get user from DB and check if is null
        UserEntity existing = this.usersCrudDB
                .findById(userID)
                .orElseThrow(() -> new NotFoundException("Couldn't find user by id  " + userID));


        boolean dirtyFlag = false;
        if (update.getUsername() != null) {

            if (!isValidUserName(update.getUsername()))
                throw new BadRequestException("User name invalid");

            existing.setUserName(update.getUsername());
            dirtyFlag = true;
        }

        if (update.getRole()!= null) {

            if (!isValidRole(update.getRole()))
                throw new BadRequestException("Role invalid");

            existing.setRole(this.userConvertor.strToUserRole(update.getRole()));
            dirtyFlag = true;
        }

        if (update.getAvatar() != null) {

            if (!isValidAvatar(update.getAvatar()))
                throw new BadRequestException("Avatar invalid");

            existing.setAvatar(update.getAvatar());
            dirtyFlag = true;
        }


        if (dirtyFlag)
            existing = this.usersCrudDB.save(existing);

        return this.userConvertor.toBoundary(existing);
    }


    @Override
    public List<UserBoundary> getAllUsers(String userSuperapp, String userEmail, int size, int page) {

        String userId = ConvertIdsHelper.concatenateIds(new String[]{ userSuperapp, userEmail});
        // check role permission
        if (!accessControl.hasPermission(userId, "getAllUsers"))
            throw new UnauthorizedRequestException("user " + userId + " has no permission to getAllUsers");

        return this.usersCrudDB
                .findAll(PageRequest.of(page, size, Direction.ASC, "role", "userId"))
                .stream()
                .map(this.userConvertor::toBoundary)
                .toList();
    }


    @Override
    public void deleteAllUsers(String userSuperapp, String userEmail) {

        String userId = ConvertIdsHelper.concatenateIds(new String[]{ userSuperapp, userEmail});
        // check role permission
        if (!accessControl.hasPermission(userId, "deleteAllUsers"))
            throw new UnauthorizedRequestException("user " + userId + " has no permission to deleteAllUsers");

        this.usersCrudDB.deleteAll();
    }

    /**
     * This method validate the username isn`t null or empty.
     *
     * @param username String
     * @return boolean
     */
    private boolean isValidUserName(String username) {

        return username != null && !username.isEmpty();
    }


    /**
     * This method validate the avatar isn`t null or empty.
     *
     * @param avatar String
     * @return boolean
     */
    private boolean isValidAvatar(String avatar) {

        return avatar != null && !avatar.isEmpty();
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


        if (email == null)
            return false;

        String emailRegex = "^[a-zA-Z0-9+&*-]+(?:\\."
                + "[a-zA-Z0-9+&*-]+)*@"
                + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
                + "A-Z]{2,7}$";

        Pattern emailPattern = Pattern.compile(emailRegex);

        // check email format
        return emailPattern.matcher(email).matches();
    }


    /**** Deprecated methods *****/
    @Deprecated
    @Override
    public void deleteAllUsers() {
        throw new DeprecatedRequestException("do not use deprecated function");
        // this.usersCrudDB.deleteAll();
    }

    @Override
    @Deprecated
    public List<UserBoundary> getAllUsers() {
        throw new DeprecatedRequestException("do not use deprecated function");
//        return this.usersCrudDB
//                .findAll()
//                .stream()
//                .map(this::entityToBoundary)
//                .toList();
    }
}
