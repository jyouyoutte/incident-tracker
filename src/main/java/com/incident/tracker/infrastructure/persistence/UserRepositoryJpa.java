package com.incident.tracker.infrastructure.persistence;

import com.incident.tracker.domain.model.User;
import com.incident.tracker.domain.port.UserRepositoryPort;
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
