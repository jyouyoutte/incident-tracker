package com.incident.tracker.auth.application;

import com.incident.tracker.auth.application.dto.AuthResponseDto;
import com.incident.tracker.auth.application.dto.UserDto;
import com.incident.tracker.auth.application.service.AuthServiceImpl;
import com.incident.tracker.auth.infrastructure.persistence.repository.RoleRepositoryPort;
import com.incident.tracker.auth.infrastructure.persistence.repository.UserRepositoryPort;
import com.incident.tracker.auth.infrastructure.persistence.entity.RoleEntity;
import com.incident.tracker.auth.infrastructure.persistence.entity.UserEntity;
import com.incident.tracker.auth.application.mapper.UserPersistenceMapper;
import com.incident.tracker.auth.infrastructure.security.exception.UserAlreadyExistsException;
import com.incident.tracker.auth.infrastructure.security.exception.UserNotCreatedException;
import com.incident.tracker.auth.infrastructure.security.provider.JwtTokenProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserPersistenceMapper mapper;

    @Mock
    private RoleRepositoryPort roleRepositoryPort;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("Should return UserDto when user exists")
    void shouldReturnUserDtoWhenUserExists() {
        var user = new UserEntity();
        user.setId(1L);
        user.setUsername("bruno");
        var dto = new UserDto();
        dto.setUsername("bruno");

        Mockito.when(userRepositoryPort.findByUsername("bruno")).thenReturn(Optional.of(user));
        Mockito.when(mapper.entityToDto(user)).thenReturn(dto);

        Optional<UserDto> result = authService.findByUsername("bruno");

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getUsername()).isEqualTo("bruno");
    }

    @Test
    @DisplayName("Should return empty when user not found")
    void shouldReturnEmptyWhenUserNotFound() {
        Mockito.when(userRepositoryPort.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<UserDto> result = authService.findByUsername("unknown");

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() {
        var dto = new UserDto();
        dto.setUsername("newuser");
        dto.setPassword("raw");
        dto.setRoles(List.of("ROLE_USER"));

        var entity = new UserEntity();
        entity.setUsername("newuser");

        var saved = new UserEntity();
        saved.setId(2L);
        saved.setUsername("newuser");

        Mockito.when(userRepositoryPort.findByUsername("newuser")).thenReturn(Optional.empty());
        // mock role lookup and mapping required by register()
        var roleEntity = new RoleEntity(null, "ROLE_USER");
        Mockito.when(roleRepositoryPort.findByName("ROLE_USER")).thenReturn(Optional.of(roleEntity));
        Mockito.when(passwordEncoder.encode("raw")).thenReturn("encoded");
        Mockito.when(mapper.dtoToEntity(Mockito.any(UserDto.class))).thenReturn(entity);
        Mockito.when(userRepositoryPort.saveUser(entity)).thenReturn(Optional.of(saved));
        Mockito.when(mapper.entityToDto(saved)).thenReturn(dto);

        Optional<UserDto> result = authService.register(dto);

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getUsername()).isEqualTo("newuser");
        Mockito.verify(passwordEncoder).encode("raw");
        Mockito.verify(userRepositoryPort).saveUser(entity);
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when registering an existing user")
    void shouldThrowWhenUserAlreadyExists() {
        var dto = new UserDto();
        dto.setUsername("exist");

        var existingUser = new UserEntity();
        existingUser.setUsername("exist");
        Mockito.when(userRepositoryPort.findByUsername("exist")).thenReturn(Optional.of(existingUser));
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
        dto.setRoles(List.of("ROLE_USER"));

        var entity = new UserEntity();
        entity.setUsername("willfail");

        Mockito.when(userRepositoryPort.findByUsername("willfail")).thenReturn(Optional.empty());
        // mock role lookup and mapping required by register()
        var roleEntity = new RoleEntity(null, "ROLE_USER");
        Mockito.when(roleRepositoryPort.findByName("ROLE_USER")).thenReturn(Optional.of(roleEntity));
        Mockito.when(passwordEncoder.encode("pwd")).thenReturn("enc");
        Mockito.when(mapper.dtoToEntity(Mockito.any(UserDto.class))).thenReturn(entity);
        Mockito.when(userRepositoryPort.saveUser(entity)).thenReturn(Optional.empty());

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

    @Test
    @DisplayName("Should persist user roles to users_roles when registering")
    void shouldPersistUserRolesWhenRegistering() {
        var dto = new UserDto();
        dto.setUsername("roleuser");
        dto.setPassword("pwd");
        dto.setRoles(List.of("ROLE_DEV"));

        // Prepare entity returned by mapper with roles populated
        var entity = new UserEntity();
        entity.setUsername("roleuser");
        entity.setPassword("encoded");
        entity.setRoles(List.of(new RoleEntity(null, "ROLE_DEV")));

        var saved = new UserEntity();
        saved.setId(10L);
        saved.setUsername("roleuser");
        saved.setRoles(entity.getRoles());

        Mockito.when(userRepositoryPort.findByUsername("roleuser")).thenReturn(Optional.empty());
        // mock role lookup and mapping required by register()
        var roleEntity = new RoleEntity(null, "ROLE_DEV");
        Mockito.when(roleRepositoryPort.findByName("ROLE_DEV")).thenReturn(Optional.of(roleEntity));
        Mockito.when(passwordEncoder.encode("pwd")).thenReturn("encoded");
        Mockito.when(mapper.dtoToEntity(Mockito.any(UserDto.class))).thenReturn(entity);
        Mockito.when(userRepositoryPort.saveUser(Mockito.any(UserEntity.class))).thenReturn(Optional.of(saved));
        var expectedDto = new UserDto();
        expectedDto.setUsername("roleuser");
        Mockito.when(mapper.entityToDto(saved)).thenReturn(expectedDto);

        Optional<UserDto> result = authService.register(dto);

        Assertions.assertThat(result).isPresent();

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        Mockito.verify(userRepositoryPort).saveUser(captor.capture());
        UserEntity captured = captor.getValue();

        Assertions.assertThat(captured.getRoles()).isNotNull();
        Assertions.assertThat(captured.getRoles()).hasSize(1);
        Assertions.assertThat(captured.getRoles()).anyMatch(r -> r.getName().equals("ROLE_DEV"));
    }
}


