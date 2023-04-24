package superapp.dal.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import superapp.dal.UserRole;

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

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
