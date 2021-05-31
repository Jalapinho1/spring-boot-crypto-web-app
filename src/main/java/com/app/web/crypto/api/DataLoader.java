package com.app.web.crypto.api;

import com.app.web.crypto.api.model.Role;
import com.app.web.crypto.api.model.RoleName;
import com.app.web.crypto.api.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataLoader implements ApplicationRunner {

    private RoleRepository roleRepository;

    @Autowired
    public DataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void run(ApplicationArguments args) throws Exception {
        Optional<Role> userRole = roleRepository.findByName(RoleName.ROLE_USER);
        if (!userRole.isPresent())
            roleRepository.save(new Role(RoleName.ROLE_USER));

        Optional<Role> adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN);
        if (!adminRole.isPresent())
            roleRepository.save(new Role(RoleName.ROLE_ADMIN));
    }
}