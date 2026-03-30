package com.incident.tracker.infrastructure.web;

import com.incident.tracker.application.dto.auth.AuthResponseDto;
import com.incident.tracker.application.dto.auth.UserDto;
import com.incident.tracker.application.service.AuthService;
import com.incident.tracker.infrastructure.web.vo.auth.AuthResponseVo;
import com.incident.tracker.infrastructure.web.vo.auth.UserVo;
import com.incident.tracker.mapper.AuthResponseMapper;
import com.incident.tracker.mapper.UserMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthResponseMapper authResponseMapper;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("Should return 200 and message when registration succeeds")
    void shouldReturnOkWhenRegisterSucceeds() {
        UserVo userVo = new UserVo("john", "pwd", "ROLE_USER");
        UserDto dto = new UserDto();
        dto.setUsername("john");
        dto.setPassword("pwd");
        dto.setRole("ROLE_USER");

        Mockito.when(userMapper.voToDto(Mockito.any(UserVo.class))).thenReturn(dto);
        Mockito.when(authService.register(Mockito.any(UserDto.class))).thenReturn(Optional.of(dto));

        ResponseEntity<?> response = authController.register(userVo);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isEqualTo("User registered successfully");
    }

    @Test
    @DisplayName("Should return 500 when registration fails")
    void shouldReturnInternalServerErrorWhenRegisterFails() {
        UserVo userVo = new UserVo("jane", "pwd", "ROLE_USER");
        UserDto dto = new UserDto();
        dto.setUsername("jane");

        Mockito.when(userMapper.voToDto(Mockito.any(UserVo.class))).thenReturn(dto);
        Mockito.when(authService.register(Mockito.any(UserDto.class))).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.register(userVo);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(response.getBody()).isEqualTo("Failed to register user");
    }

    @Test
    @DisplayName("Should return AuthResponseVo and 200 when login succeeds")
    void shouldReturnAuthResponseVoWhenLoginSucceeds() {
        UserVo userVo = new UserVo("alice", "pwd", "ROLE_USER");

        UserDto dto = new UserDto();
        dto.setUsername("alice");
        dto.setPassword("pwd");

        AuthResponseDto authResponseDto = new AuthResponseDto("token-123", "Bearer");
        AuthResponseVo authResponseVo = new AuthResponseVo("token-123", "Bearer");

        Mockito.when(userMapper.voToDto(Mockito.any(UserVo.class))).thenReturn(dto);
        Mockito.when(authService.login(Mockito.any(UserDto.class))).thenReturn(Optional.of(authResponseDto));
        Mockito.when(authResponseMapper.dtoToVo(Mockito.any(AuthResponseDto.class))).thenReturn(authResponseVo);

        ResponseEntity<?> response = authController.login(userVo);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isInstanceOf(AuthResponseVo.class);
        Assertions.assertThat((AuthResponseVo) response.getBody()).satisfies(vo -> {
            Assertions.assertThat(vo.token()).isEqualTo("token-123");
            Assertions.assertThat(vo.type()).isEqualTo("Bearer");
        });
    }

    @Test
    @DisplayName("Should return 401 when login fails")
    void shouldReturnUnauthorizedWhenLoginFails() {
        UserVo userVo = new UserVo("bob", "bad", "ROLE_USER");
        UserDto dto = new UserDto();
        dto.setUsername("bob");

        Mockito.when(userMapper.voToDto(Mockito.any(UserVo.class))).thenReturn(dto);
        Mockito.when(authService.login(Mockito.any(UserDto.class))).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.login(userVo);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        Assertions.assertThat(response.getBody()).isEqualTo("Invalid username or password");
    }

    @Test
    @DisplayName("Should throw NullPointerException if UserVo is null during registration")
    void shouldThrowWhenUserVoIsNullOnRegister() {
        Assertions.assertThatThrownBy(() -> authController.register(null))
                .isInstanceOf(NullPointerException.class);
    }
}

