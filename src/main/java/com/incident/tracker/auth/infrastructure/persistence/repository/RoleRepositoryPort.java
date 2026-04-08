package com.incident.tracker.auth.infrastructure.persistence.repository;

import com.incident.tracker.auth.infrastructure.persistence.entity.RoleEntity;

import java.util.Optional;

public interface RoleRepositoryPort {
    Optional<RoleEntity> findByName(String username);
}
