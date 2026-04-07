package com.incident.tracker.auth.domain.port;

import com.incident.tracker.auth.infrastructure.persistence.entity.User;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByUsername(String username);
    Optional<User> saveUser(User user);
}
