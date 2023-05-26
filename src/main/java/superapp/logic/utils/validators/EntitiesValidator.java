package superapp.logic.utils.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.data.UserCrud;
import superapp.data.UserEntity;
import superapp.logic.mongo.NotFoundException;
import superapp.logic.utils.convertors.ConvertIdsHelper;

@Component
public class EntitiesValidator {

    private UserCrud usersCrud;
    private ObjectCrud objectCrud;

    @Autowired
    public EntitiesValidator(UserCrud usersCrud, ObjectCrud objectCrud) {
        this.usersCrud = usersCrud;
        this.objectCrud = objectCrud;
    }

    public UserEntity validateExistingUser(String userSuperapp, String userEmail) {
        String userId = ConvertIdsHelper.concatenateIds(new String[]{userSuperapp, userEmail});

        return this.usersCrud
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User id " + userId + " not exist in database"));
    }
}
