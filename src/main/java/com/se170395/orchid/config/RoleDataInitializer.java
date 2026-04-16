package com.se170395.orchid.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pojo.Role;
import repository.RoleRepository;

import java.util.UUID;

@Component
public class RoleDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RoleDataInitializer.class);

    private final RoleRepository roleRepository;

    public RoleDataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        initRoleIfMissing("ROLE_ADMIN");
        initRoleIfMissing("ROLE_USER");
    }

    private void initRoleIfMissing(String roleName) {
        Role existing = roleRepository.findByRoleName(roleName);
        if (existing != null) {
            log.info("Role '{}' already exists with id={}", roleName, existing.getId());
            return;
        }

        Role role = new Role(UUID.randomUUID().toString(), roleName);
        roleRepository.save(role);
        log.info("Created default role '{}' with id={}", roleName, role.getId());
    }
}

