package com.incident.tracker.infrastructure.persistence;

import com.incident.tracker.domain.model.User;
import com.incident.tracker.infrastructure.persistence.auth.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@ActiveProfiles("test")
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;



    @Test
    void shouldFindByUsernameWhenUserExists() {
        User user = new User();
        user.setName("Bruno User");
        user.setUsername("bruno");
        user.setPassword("bruno");

        userRepository.saveAndFlush(user);
        Optional<User> found = userRepository.findByUsername("bruno");

        Assertions.assertThat(found).isPresent();
        Assertions.assertThat(found.get().getUsername()).isEqualTo("bruno");
        Assertions.assertThat(found.get().getPassword()).isEqualTo("bruno");
    }

    @Test
    void shouldReturnEmptyWhenUsernameNotFound() {
        Optional<User> found = userRepository.findByUsername("nope");

        Assertions.assertThat(found).isNotPresent();
    }
}

