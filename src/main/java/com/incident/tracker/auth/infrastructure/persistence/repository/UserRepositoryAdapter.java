package com.incident.tracker.auth.infrastructure.persistence.repository;

import com.incident.tracker.auth.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepositoryPort {
    private final UserJpaRepository userRepository;

    public UserRepositoryAdapter(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<UserEntity> saveUser(UserEntity user) {
        return Optional.of(userRepository.save(user));
    }
}
