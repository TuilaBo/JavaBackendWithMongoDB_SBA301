package repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pojo.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
    Role findByRoleName(String name);
}
