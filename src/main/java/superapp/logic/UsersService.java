package superapp.logic;

import superapp.logic.boundaries.UserBoundary;

import java.util.List;
import java.util.Optional;

public interface UsersService {
    UserBoundary createUser(UserBoundary userBoundary);
    Optional<UserBoundary> login(String userSuperApp, String userEmail);
    UserBoundary updateUser(String userSuperApp, String userEmail, UserBoundary update);
    List<UserBoundary> getAllUsers();
    void deleteAllUsers();

}
