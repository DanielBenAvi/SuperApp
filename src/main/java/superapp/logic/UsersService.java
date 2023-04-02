package superapp.logic;

import superapp.logic.boundaries.UserBoundary;

import java.util.List;

public interface UsersService {
    public UserBoundary createUser(UserBoundary userBoundary);

    public UserBoundary login(String userSuperApp, String userEmail);

    public UserBoundary updateUser(String userSuperApp, String userEmail, UserBoundary update);

    public List<UserBoundary> getAllUsers();

    public void deleteAllUsers();

}
