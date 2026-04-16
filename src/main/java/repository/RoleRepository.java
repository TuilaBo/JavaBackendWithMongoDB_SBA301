package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pojo.Role;

public interface RoleRepository extends JpaRepository<Role, String> {
    Role findByRoleName(String name);
}
