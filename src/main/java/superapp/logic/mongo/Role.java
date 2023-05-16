package superapp.logic.mongo;

import java.util.HashSet;
import java.util.Set;

class Role {
    private String userRoleName;
    private Set<String> permissions;


    public Role(String userRoleName) {
        this.userRoleName = userRoleName;
        this.permissions = new HashSet<>();
    }

    public String getUserRoleName() {
        return userRoleName;
    }

    public Role setUserRoleName(String userRoleName) {
        this.userRoleName = userRoleName;
        return this;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public Role setPermissions(Set<String> permissions) {
        this.permissions = permissions;
        return this;
    }


}