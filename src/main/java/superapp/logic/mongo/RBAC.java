package superapp.logic.mongo;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.data.UserCrud;
import superapp.data.UserRole;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
public class RBAC {

    private UserCrud userCrudDB;
    private Map<String, Role> roles;

    @Autowired
    public RBAC(UserCrud userCrudDB) {

        this.userCrudDB = userCrudDB;

        this.roles = new HashMap<>();
        createRole(UserRole.ADMIN.toString());
        createRole(UserRole.SUPERAPP_USER.toString());
        createRole(UserRole.MINIAPP_USER.toString());

        roles.get(UserRole.ADMIN.toString())
                .setPermissions(
                        new HashSet<>(
                                Arrays.asList("deleteAllCommands",
                                                "deleteAllObjects",
                                                "deleteAllUsers",
                                                "getAllCommands",
                                                "getAllMiniAppCommands",
                                                "getAllUsers",
                                                "updateUser",
                                                "createUser",
                                                "login")
                        ));

        roles.get(UserRole.SUPERAPP_USER.toString())
                .setPermissions(
                        new HashSet<>(
                                Arrays.asList("getSpecificObject", //-include active=false
                                        "getAllObjects", //-include active=false
                                        "createObject",
                                        "updateObject",
                                        "addChild",
                                        "getChildren",// -include active=false
                                        "getParent", // -include active=false
                                        "updateUser",
                                        "createUser",
                                        "login",
                                        "getAllObjectsByType",
                                        "getAllObjectsByAlias",
                                        "getAllObjectsByLocation")
                        ));

        roles.get(UserRole.MINIAPP_USER.toString())
                .setPermissions(
                        new HashSet<>(
                                Arrays.asList("getSpecificObject", //-just active=true
                                        "getAllObjects", //-just active=true
                                        "getChildren", //-just active=true
                                        "getParent", //-just active=true
                                        "invokeCommand", //-just active=true
                                        "updateUser",
                                        "createUser",
                                        "login",
                                        "getAllObjectsByType",
                                        "getAllObjectsByAlias",
                                        "getAllObjectsByLocation")
                        ));

    }

    @PostConstruct
    public void init() {
        System.err.println("****** " + this.getClass().getName() + " service initiated");
    }

    private void createRole(String roleName) {
        Role role = new Role(roleName);
        roles.put(roleName, role);
    }

    public boolean hasPermission(String entityUserId, String permission) {

        String userRoleName = this.userCrudDB
                .findById(entityUserId).orElseThrow(() -> new NotFoundException()).getRole().name();

//        String userRoleName = this.userCrudDB
//                                            .findById(entityUserId)
//                                            .get()
//                                            .getRole()
//                                            .name();

        return roles
                .get(userRoleName)
                .getPermissions()
                .contains(permission);
    }


}

