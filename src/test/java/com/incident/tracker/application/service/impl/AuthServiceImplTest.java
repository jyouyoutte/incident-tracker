package com.incident.tracker.application.service.impl;

import com.incident.tracker.application.dto.auth.AuthResponseDto;
import com.incident.tracker.application.dto.auth.UserDto;
import com.incident.tracker.domain.model.User;
import com.incident.tracker.domain.port.UserRepositoryPort;
import com.incident.tracker.infrastructure.security.exception.UserAlreadyExistsException;
import com.incident.tracker.infrastructure.security.exception.UserNotCreatedException;
import com.incident.tracker.infrastructure.security.provider.JwtTokenProvider;
import com.incident.tracker.mapper.UserMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepositoryPort repositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("Should return UserDto when user exists")
    void shouldReturnUserDtoWhenUserExists() {
        var user = new User();
        user.setId(1L);
        user.setUsername("bruno");
        var dto = new UserDto();
        dto.setUsername("bruno");

        Mockito.when(repositoryPort.findByUsername("bruno")).thenReturn(Optional.of(user));
        Mockito.when(mapper.entityToDto(user)).thenReturn(dto);

        Optional<UserDto> result = authService.findByUsername("bruno");

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getUsername()).isEqualTo("bruno");
    }

    @Test
    @DisplayName("Should return empty when user not found")
    void shouldReturnEmptyWhenUserNotFound() {
        Mockito.when(repositoryPort.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<UserDto> result = authService.findByUsername("unknown");

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() {
        var dto = new UserDto();
        dto.setUsername("newuser");
        dto.setPassword("raw");

        var entity = new User();
        entity.setUsername("newuser");

        var saved = new User();
        saved.setId(2L);
        saved.setUsername("newuser");

        Mockito.when(repositoryPort.findByUsername("newuser")).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode("raw")).thenReturn("encoded");
        Mockito.when(mapper.dtoToEntity(Mockito.any(UserDto.class))).thenReturn(entity);
        Mockito.when(repositoryPort.saveUser(entity)).thenReturn(Optional.of(saved));
        Mockito.when(mapper.entityToDto(saved)).thenReturn(dto);

        Optional<UserDto> result = authService.register(dto);

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getUsername()).isEqualTo("newuser");
        Mockito.verify(passwordEncoder).encode("raw");
        Mockito.verify(repositoryPort).saveUser(entity);
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when registering an existing user")
    void shouldThrowWhenUserAlreadyExists() {
        var dto = new UserDto();
        dto.setUsername("exist");

        var existingUser = new User();
        existingUser.setUsername("exist");
        Mockito.when(repositoryPort.findByUsername("exist")).thenReturn(Optional.of(existingUser));
        Mockito.when(mapper.entityToDto(existingUser)).thenReturn(dto);

        Assertions.assertThatThrownBy(() -> authService.register(dto))
                .isExactlyInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("exist");
    }

    @Test
    @DisplayName("Should throw UserNotCreatedException when repository fails to save user")
    void shouldThrowWhenUserNotCreated() {
        var dto = new UserDto();
        dto.setUsername("willfail");
        dto.setPassword("pwd");

        var entity = new User();
        entity.setUsername("willfail");

        Mockito.when(repositoryPort.findByUsername("willfail")).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode("pwd")).thenReturn("enc");
        Mockito.when(mapper.dtoToEntity(Mockito.any(UserDto.class))).thenReturn(entity);
        Mockito.when(repositoryPort.saveUser(entity)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> authService.register(dto))
                .isExactlyInstanceOf(UserNotCreatedException.class)
                .hasMessageContaining("willfail");
    }

    @Test
    @DisplayName("Should return AuthResponseDto when login succeeds")
    void shouldReturnAuthResponseWhenLoginSucceeds() {
        var dto = new UserDto();
        dto.setUsername("alice");
        dto.setPassword("pwd");

        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetails principal = Mockito.mock(UserDetails.class);

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);
        Mockito.when(jwtTokenProvider.generateToken(principal)).thenReturn("token-xyz");

        Optional<AuthResponseDto> result = authService.login(dto);

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().token()).isEqualTo("token-xyz");
        Assertions.assertThat(result.get().type()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("Should return empty when authentication throws an exception")
    void shouldReturnEmptyWhenAuthenticationFails() {
        var dto = new UserDto();
        dto.setUsername("bob");
        dto.setPassword("bad");

        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenThrow(new RuntimeException("bad credentials"));

        Optional<AuthResponseDto> result = authService.login(dto);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when authentication is not authenticated")
    void shouldReturnEmptyWhenAuthenticationNotAuthenticated() {
        var dto = new UserDto();
        dto.setUsername("carol");
        dto.setPassword("pwd");

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        Mockito.when(authentication.isAuthenticated()).thenReturn(false);

        Optional<AuthResponseDto> result = authService.login(dto);

        Assertions.assertThat(result).isEmpty();
    }
}


