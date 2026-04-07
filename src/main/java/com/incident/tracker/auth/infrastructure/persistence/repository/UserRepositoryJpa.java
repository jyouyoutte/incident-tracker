package com.incident.tracker.auth.infrastructure.persistence.repository;

import com.incident.tracker.auth.infrastructure.persistence.entity.User;
import com.incident.tracker.auth.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryJpa implements UserRepositoryPort {
    private final UserRepository userRepository;

    public UserRepositoryJpa(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> saveUser(User user) {
        return Optional.of(userRepository.save(user));
    }
}
