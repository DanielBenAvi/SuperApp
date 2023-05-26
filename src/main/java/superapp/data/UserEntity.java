package superapp.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document (collection = "USERS")
public class UserEntity {

    @Id private String userID;
    private UserRole role;
    private String userName;
    private String avatar;

    public UserEntity() {}

    public UserEntity(String email, UserRole role, String userName, String avatar) {
        this.userID = email;
        this.role = role;
        this.userName = userName;
        this.avatar = avatar;
    }

    public String getUserID() {
        return userID;
    }

    public UserEntity setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    public UserRole getRole() {
        return role;
    }

    public UserEntity setRole(UserRole role) {
        this.role = role;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public UserEntity setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public UserEntity setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }
}
