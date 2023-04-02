package superapp.logic;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import superapp.data.entities.UserEntity;
import superapp.logic.boundaries.UserBoundary;
import superapp.logic.boundaries.UserID;

import java.util.*;

public class UserManager implements UsersService{
    private String superappName;
    private Map<String, UserEntity> databaseMockup;

    /**
     * injects a configuration value of spring
     *
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
        this.databaseMockup = Collections.synchronizedMap(new HashMap<>());
        System.err.println("****** "+ this.superappName);
    }

    /**
     * converts user boundary to user entity
     * @param userBoundary boundary
     * @return userEntity
     *
     */
    private UserEntity boundaryToEntity(UserBoundary userBoundary) {
        UserEntity userEntity = new UserEntity();

        String email = userBoundary.getUserID().getEmail();
        String userID = ConvertHelp.userIdBoundaryToStr(new UserID(superappName,email),ConvertHelp.DELIMITER);

        userEntity.setUserID(userID);

        userEntity.setUserName(userBoundary.getUserName());

        userEntity.setAvatar(userBoundary.getAvatar());

        userEntity.setRole(userBoundary.getRole());

        return userEntity;
    }

    /**
     * converts user entity to user boundary
     * @param userEntity entity
     * @return userBoundary
     */
    private UserBoundary entityToBoundary(UserEntity userEntity) {
        UserBoundary userBoundary = new UserBoundary();
        UserID userID = ConvertHelp.strUserIdToBoundary(userEntity.getUserID(), ConvertHelp.DELIMITER);
        userBoundary.setUserID(userID);
        userBoundary.setUserName(userEntity.getUserName());
        userBoundary.setRole(userEntity.getRole());
        userBoundary.setAvatar(userEntity.getAvatar());

        return userBoundary;
    }

    /**
     * Creates new user boundary
     * @param userBoundary boundary
     * @return userEntity
     */
    @Override
    public UserBoundary createUser(UserBoundary userBoundary) {
        if (userBoundary == null){
            throw new RuntimeException("null UserBoundary can't be created");
        }

        if (userBoundary.getUserID().getEmail() == null){
            throw new RuntimeException("null Email can't be created");
        }

        if (userBoundary.getRole() == null){
            throw new RuntimeException("null Roll can't be created");
        }

        if (userBoundary.getUserName() == null){
            throw new RuntimeException("null UserName can't be created");
        }

        if (userBoundary.getAvatar() == null){
            throw new RuntimeException("null Avatar can't be created");
        }

        UserEntity userEntity = this.boundaryToEntity(userBoundary);


        this.databaseMockup.put(userEntity.getUserID(), userEntity);

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
        String userID = ConvertHelp.concatenateIds(new String[]{superappName,userEmail},ConvertHelp.DELIMITER);
        UserEntity userEntity = this.databaseMockup.get(userID);
        if (userEntity == null){
            return Optional.empty();
        }else {
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

        String userID = ConvertHelp.concatenateIds(new String[]{userSuperApp,userEmail},ConvertHelp.DELIMITER);

        UserEntity existing = this.databaseMockup.get(userID);

        if (existing == null){
            throw new RuntimeException("Couldn't find user by id: "+userID);
        }

        boolean dirtyFlag = false;

        if (update.getUserName() != null){
            existing.setUserName(update.getUserName());
            dirtyFlag = true;
        }

        if (update.getRole()!= null){
            existing.setRole(update.getRole());
            dirtyFlag = true;
        }

        if (update.getAvatar() != null){
            existing.setAvatar(update.getAvatar());
            dirtyFlag = true;
        }

        if (dirtyFlag){
            this.databaseMockup.put(userID, existing);
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
        return this.databaseMockup.values().stream().map(this::entityToBoundary).toList();
    }

    /**
     * delete all users from database
     */
    @Override
    public void deleteAllUsers() {
        this.databaseMockup.clear();
    }
}
