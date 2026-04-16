package service;

import pojo.Role;
import java.util.List;

public interface RoleService {
    Role getRole(String roleName);

    Role insertRole(Role role);

    Role getRoleById(String id);

    void deleteRole(Role role);

    Role updateRole(Role role);

    Role updateRole(String id, Role role);
    void deleteRoleById(String id);
    List<Role> getAllRoles();
}
