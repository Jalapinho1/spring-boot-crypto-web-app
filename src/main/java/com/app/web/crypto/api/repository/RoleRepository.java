package com.app.web.crypto.api.repository;

import com.app.web.crypto.api.model.Role;
import com.app.web.crypto.api.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
