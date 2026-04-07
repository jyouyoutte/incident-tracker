package com.incident.tracker.auth.infrastructure.persistence.repository;

import com.incident.tracker.auth.infrastructure.persistence.entity.Role;
import com.incident.tracker.auth.domain.port.RoleRepositoryPort;
import org.springframework.stereotype.Repository;

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
