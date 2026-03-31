package com.incident.tracker.domain.port.auth;

import com.incident.tracker.domain.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepositoryPort {
    Optional<Role> findByName(String username);
}
