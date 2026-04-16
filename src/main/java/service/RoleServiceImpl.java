package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.Role;
import repository.RoleRepository;
import java.util.List;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role getRole(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    @Override
    public Role insertRole(Role role) {
        if (role != null && role.getRoleName() != null && !role.getRoleName().isEmpty()) {
           // Generate an id for String primary key if missing
           if (role.getId() == null || role.getId().trim().isEmpty()) {
               role.setId(UUID.randomUUID().toString());
           }
           return roleRepository.save(role);
        } else {
            throw new IllegalArgumentException("Role or role name cannot be null or empty");
        }
    }
    @Override
    public Role getRoleById(String id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + id));
    }

    @Override
    public Role updateRole(Role role) {
        if (role != null && role.getId() != null) {
            return roleRepository.save(role);
        } else {
            throw new IllegalArgumentException("Role or role ID cannot be null");
        }
    }

    @Override
    public Role updateRole(String id, Role role) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role with ID " + id + " does not exist"));
        existingRole.setRoleName(role.getRoleName());
        return roleRepository.save(existingRole);
    }

    @Override
    public void deleteRole(Role role) {
        roleRepository.delete(role);
    }

    @Override
    public void deleteRoleById(String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role with ID " + id + " does not exist"));
        roleRepository.delete(role);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
