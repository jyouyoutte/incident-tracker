package com.incident.tracker.auth.domain.port;

import com.incident.tracker.auth.infrastructure.persistence.entity.Role;

import java.util.Optional;

public interface RoleRepositoryPort {
    Optional<Role> findByName(String username);
}
