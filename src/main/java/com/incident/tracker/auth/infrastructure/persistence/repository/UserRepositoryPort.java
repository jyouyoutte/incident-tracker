package com.incident.tracker.auth.infrastructure.persistence.repository;

import com.incident.tracker.auth.infrastructure.persistence.entity.UserEntity;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> saveUser(UserEntity user);
}
