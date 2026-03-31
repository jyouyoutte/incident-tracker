package com.incident.tracker.infrastructure.persistence.auth;

import com.incident.tracker.domain.model.Role;
import com.incident.tracker.domain.port.auth.RoleRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleRepositoryJpa implements RoleRepositoryPort {
    private final RoleRepository roleRepository;

    public RoleRepositoryJpa(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> findByName(String username) {
        return roleRepository.findByName(username);
    }

}
