package com.incident.tracker.auth.infrastructure.persistence;

import com.incident.tracker.auth.infrastructure.persistence.entity.UserEntity;
import com.incident.tracker.auth.infrastructure.persistence.repository.UserJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@ActiveProfiles("test")
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserJpaRepository userRepository;



    @Test
    void shouldFindByUsernameWhenUserExists() {
        UserEntity user = new UserEntity();
        user.setName("Bruno User");
        user.setUsername("bruno");
        user.setPassword("bruno");

        userRepository.saveAndFlush(user);
        Optional<UserEntity> found = userRepository.findByUsername("bruno");

        Assertions.assertThat(found).isPresent();
        Assertions.assertThat(found.get().getUsername()).isEqualTo("bruno");
        Assertions.assertThat(found.get().getPassword()).isEqualTo("bruno");
    }

    @Test
    void shouldReturnEmptyWhenUsernameNotFound() {
        Optional<UserEntity> found = userRepository.findByUsername("nope");

        Assertions.assertThat(found).isNotPresent();
    }
}

