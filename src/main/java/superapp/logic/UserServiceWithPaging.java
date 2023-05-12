package superapp.logic;

import superapp.logic.boundaries.UserBoundary;

import java.util.List;

public interface UserServiceWithPaging extends UsersService{
    public List<UserBoundary> getAllUsers(String superapp, String email, int size, int page);
    public void deleteAllUsers(String superapp, String email);
}
