package superapp.logic.utils.convertors.impl;

import org.springframework.stereotype.Component;
import superapp.data.UserEntity;
import superapp.data.UserRole;
import superapp.logic.boundaries.UserBoundary;
import superapp.logic.boundaries.UserId;
import superapp.logic.mongo.BadRequestException;
import superapp.logic.utils.convertors.ConvertIdsHelper;
import superapp.logic.utils.convertors.UserConvertor;


@Component
public class ConvertUser implements UserConvertor {


    @Override
    public UserBoundary toBoundary(UserEntity entity) {

        UserId userId = userIdToBoundary(entity.getUserID());
        String userRole = userRoleToStr(entity.getRole());

        return new UserBoundary()
                .setUserId(userId)
                .setUsername(entity.getUserName())
                .setRole(userRole)
                .setAvatar(entity.getAvatar());
    }

    @Override
    public UserEntity toEntity(UserBoundary boundary) {

        String userId = userIdToEntity(boundary.getUserId());
        UserRole userRole = strToUserRole(boundary.getRole());

        return new UserEntity()
                .setUserID(userId)
                .setUserName(boundary.getUsername())
                .setRole(userRole)
                .setAvatar(boundary.getAvatar());
    }

    @Override
    public String userIdToEntity(UserId userId) {
        return ConvertIdsHelper.concatenateIds(new String[]{ userId.getSuperapp(), userId.getEmail() });
    }

    @Override
    public UserId userIdToBoundary(String userId) {

        String[] str = ConvertIdsHelper.splitConcretedIds(userId);
        return new UserId(str[0],str[1]);
    }

    @Override
    public String userRoleToStr(UserRole userRole) {
        try {
            return userRole.name();
        } catch (Exception e){
            throw new BadRequestException("Role not found");
        }

    }

    @Override
    public UserRole strToUserRole(String userRole) {
        try {
            return UserRole.valueOf(userRole);
        } catch (Exception e){
            throw new BadRequestException("Role not found");
        }
    }





}
