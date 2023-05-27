package superapp.logic.mongo;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import superapp.data.UserCrud;
import superapp.data.UserEntity;
import superapp.logic.UserServiceWithPaging;
import superapp.logic.boundaries.UserBoundary;
import superapp.logic.utils.convertors.UserConvertor;
import superapp.logic.utils.validators.BoundaryValidator;
import superapp.logic.utils.validators.EntitiesValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@Validated
public class UserManagerMongoDB implements UserServiceWithPaging {

    private String superappName;

    private final UserCrud usersCrudDB;
    private final UserConvertor userConvertor;

    private final RBAC accessControl;
    private final BoundaryValidator boundaryValidator;
    private final EntitiesValidator entitiesValidator;


    @Autowired
    public UserManagerMongoDB(UserCrud usersCrudDB,
                              UserConvertor userConvertor,
                              RBAC accessControl,
                              BoundaryValidator boundaryValidator,
                              EntitiesValidator entitiesValidator) {

        this.usersCrudDB = usersCrudDB;
        this.userConvertor = userConvertor;
        this.accessControl = accessControl;
        this.boundaryValidator = boundaryValidator;
        this.entitiesValidator = entitiesValidator;
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
     * This method create a new user in database
     *
     * @param userBoundary UserBoundary
     * @return UserBoundary
     */
    @Override
    public UserBoundary createUser(UserBoundary userBoundary) {

        // init user id
        userBoundary.getUserId().setSuperapp(this.superappName);

        // validation
        this.boundaryValidator.validateUserBoundary(userBoundary, new HashSet<>());
        this.boundaryValidator.validateUserId(userBoundary.getUserId());


        UserEntity userEntity = this.userConvertor.toEntity(userBoundary);

        // check if user already exist in database
        if (this.usersCrudDB.existsById(userEntity.getUserID()))
            throw new ConflictRequestException("User with id " + userEntity.getUserID() + " already exists");


        userEntity = this.usersCrudDB.save(userEntity);

        return this.userConvertor.toBoundary(userEntity);
    }


    /**
     * This method login with specific user
     *
     * @param userSuperapp String
     * @param userEmail String
     * @return Optional - UserBoundary
     */
    @Override
    public Optional<UserBoundary> login(String userSuperapp, String userEmail) {

        // validate that user exist and retrieve the user from database
        UserEntity userEntity = this.entitiesValidator.validateExistingUser(userSuperapp, userEmail);
        this.checkPermission(userEntity.getUserID(), "login");

        return userEntity != null ? Optional.of(this.userConvertor.toBoundary(userEntity)) : Optional.empty();
    }

    /**
     * This method update user in database
     * checks if attributes changed and update if needed
     *
     * @param userSuperapp String
     * @param userEmail String
     * @param update UserBoundary
     * @return UserBoundary
     */
    @Override
    public UserBoundary updateUser(String userSuperapp, String userEmail, UserBoundary update) {

        // validate that user exist and retrieve the user from database
        UserEntity existing = this.entitiesValidator.validateExistingUser(userSuperapp, userEmail);

        this.checkPermission(existing.getUserID(), "updateUser");


        Set<String> ignoredProperties = new HashSet<>();
        ignoredProperties.add("userId");

        if (update.getUsername() != null)
            existing.setUserName(update.getUsername());
        else
            ignoredProperties.add("username");

        if (update.getRole() != null)
            existing.setRole(this.userConvertor.strToUserRole(update.getRole()));
        else
            ignoredProperties.add("role");


        if (update.getAvatar() != null)
            existing.setAvatar(update.getAvatar());
        else
            ignoredProperties.add("avatar");

        // validation
        this.boundaryValidator
                .validateUserBoundary(update, ignoredProperties);

        existing = this.usersCrudDB.save(existing);

        return this.userConvertor.toBoundary(existing);
    }


    /**
     * This method extract all users from database, only admin has permission
     *
     * @param userSuperapp String
     * @param userEmail String
     * @param size int
     * @param page int
     * @return List - UserBoundary
     */
    @Override
    public List<UserBoundary> getAllUsers(String userSuperapp, String userEmail, int size, int page) {

        // validate that user exist and retrieve the user from database
        UserEntity userEntity = entitiesValidator.validateExistingUser(userSuperapp, userEmail);

        checkPermission(userEntity.getUserID(), "getAllUsers");

        return this.usersCrudDB
                .findAll(PageRequest.of(page, size, Direction.ASC, "role", "userId"))
                .stream()
                .map(this.userConvertor::toBoundary)
                .toList();
    }


    /**
     * This method delete all user from database, only admin has permission
     *
     * @param userSuperapp String
     * @param userEmail String
     */
    @Override
    public void deleteAllUsers(String userSuperapp, String userEmail) {

        // validate that user exist and retrieve the user from database
        UserEntity userEntity = this.entitiesValidator.validateExistingUser(userSuperapp, userEmail);

        this.checkPermission(userEntity.getUserID(), "deleteAllUsers");

        this.usersCrudDB.deleteAll();
    }

    private void checkPermission(String userId, String operationName) {
        // check role permission
        if (!accessControl.hasPermission(userId, operationName))
            throw new UnauthorizedRequestException("User " + userId + " has no permission to " + operationName);
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
