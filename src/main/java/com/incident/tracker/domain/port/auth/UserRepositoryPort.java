package com.incident.tracker.domain.port.auth;

import com.incident.tracker.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByUsername(String username);
    Optional<User> saveUser(User user);
}
