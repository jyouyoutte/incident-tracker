package com.incident.tracker.auth.infrastructure.persistence.repository;

import com.incident.tracker.auth.infrastructure.persistence.entity.RoleEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RoleRepositoryAdapter implements RoleRepositoryPort {
    private final RoleJpaRepository roleRepository;

    public RoleRepositoryAdapter(RoleJpaRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<RoleEntity> findByName(String username) {
        return roleRepository.findByName(username);
    }

}
