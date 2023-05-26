package superapp.logic.utils.convertors;

import superapp.data.UserEntity;
import superapp.data.UserRole;
import superapp.logic.boundaries.UserBoundary;
import superapp.logic.boundaries.UserId;

public interface UserConvertor {

    public UserBoundary toBoundary(UserEntity entity);
    public UserEntity toEntity(UserBoundary boundary);

    /** User **/
    public String userIdToEntity(UserId userId);
    public UserId userIdToBoundary(String userId);
    public String userRoleToStr(UserRole userRole);
    public UserRole strToUserRole(String userRole);
}
