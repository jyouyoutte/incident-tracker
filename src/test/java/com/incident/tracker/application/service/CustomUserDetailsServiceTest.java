package com.incident.tracker.application.service;

import com.incident.tracker.domain.model.Role;
import com.incident.tracker.domain.model.User;
import com.incident.tracker.domain.port.UserRepositoryPort;
import com.incident.tracker.infrastructure.security.service.CustomUserDetailsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Nested
    @DisplayName("Méthode loadUserByUsername")
    class LoadUserByUsername {

        @Test
        @DisplayName("Doit retourner UserDetails quand l'utilisateur existe")
        void shouldReturnUserDetailsWhenUserExists() {
            var user = new User();
            user.setId(1L);
            user.setUsername("bruno");
            user.setPassword("secret");
            user.setRole("ROLE_USER");
            user.setRoles(List.of(new Role(1L, "ROLE_USER"))) ;

            Mockito.when(userRepositoryPort.findByUsername("bruno")).thenReturn(Optional.of(user));

            UserDetails userDetails = customUserDetailsService.loadUserByUsername("bruno");

            Assertions.assertThat(userDetails).isNotNull();
            Assertions.assertThat(userDetails.getUsername()).isEqualTo("bruno");
            Assertions.assertThat(userDetails.getPassword()).isEqualTo("secret");
            Assertions.assertThat(userDetails.getAuthorities()).hasSize(1);
            Assertions.assertThat(userDetails.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
        }

        @Test
        @DisplayName("Doit lancer UsernameNotFoundException quand l'utilisateur n'existe pas")
        void shouldThrowWhenUserNotFound() {
            Mockito.when(userRepositoryPort.findByUsername("unknown")).thenReturn(Optional.empty());

            Assertions.assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("unknown"))
                    .isExactlyInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("User not found with username: unknown");
        }
    }
}

