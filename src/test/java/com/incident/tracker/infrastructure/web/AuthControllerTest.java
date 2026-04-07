package com.incident.tracker.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incident.tracker.auth.application.dto.AuthResponseDto;
import com.incident.tracker.auth.application.dto.UserDto;
import com.incident.tracker.auth.application.service.AuthService;
import com.incident.tracker.auth.infrastructure.security.provider.JwtTokenProvider;
import com.incident.tracker.auth.infrastructure.security.service.CustomUserDetailsService;
import com.incident.tracker.auth.infrastructure.web.controller.AuthController;
import com.incident.tracker.auth.infrastructure.web.vo.AuthResponseVo;
import com.incident.tracker.auth.infrastructure.web.vo.UserVo;
import com.incident.tracker.mapper.AuthResponseMapper;
import com.incident.tracker.mapper.UserMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private AuthResponseMapper authResponseMapper;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Should return 200 and message when registration succeeds")
    void shouldReturnOkWhenRegisterSucceeds() throws Exception {
        UserVo userVo = new UserVo("john", "pwd",   List.of("ROLE_USER"));
        UserDto dto = new UserDto();
        dto.setUsername("john");
        dto.setPassword("pwd");
        dto.setRoles(List.of("ROLE_USER"));
        dto.setId(42L);
        dto.setRoles(List.of("ROLE_USER"));

        var returnedResponseVo = new UserVo("john", List.of("ROLE_USER"));

        Mockito.when(userMapper.voToDto(Mockito.any(UserVo.class))).thenReturn(dto);
        Mockito.when(authService.register(Mockito.any(UserDto.class))).thenReturn(Optional.of(dto));
        Mockito.when(userMapper.dtoToVo(Mockito.any(UserDto.class))).thenReturn(returnedResponseVo);

        String requestBody = objectMapper.writeValueAsString(userVo);
        var result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn();

        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value());
        String content = result.getResponse().getContentAsString();
        var responseVo = objectMapper.readValue(content, UserVo.class);
        Assertions.assertThat(responseVo.username()).isEqualTo("john");
        Assertions.assertThat(responseVo.roles()).containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("Should return 500 when registration fails")
    void shouldReturnInternalServerErrorWhenRegisterFails() throws Exception {
        UserVo userVo = new UserVo("jane", "pwd", List.of("ROLE_USER"));
        UserDto dto = new UserDto();
        dto.setUsername("jane");

        Mockito.when(userMapper.voToDto(Mockito.any(UserVo.class))).thenReturn(dto);
        Mockito.when(authService.register(Mockito.any(UserDto.class))).thenReturn(Optional.empty());

        String requestBody = objectMapper.writeValueAsString(userVo);
        var result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn();

        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());

        String content = result.getResponse().getContentAsString();
        String contentType = result.getResponse().getContentType();

        // Accept either plain text message or JSON error object {"code":..., "message":...}
        String message = content;
        if (contentType != null && contentType.contains("application/json")) {
            try {
                var node = objectMapper.readTree(content);
                message = node.path("message").asText(null);
            } catch (Exception e) {
                // fallback to raw content
                message = content;
            }
        }

        if (!(message != null && (message.contains("Failed to register user") || message.contains("User with username")))) {
            throw new AssertionError("Unexpected response body: " + content);
        }
    }

    @Test
    @DisplayName("Should return AuthResponseVo and 200 when login succeeds")
    void shouldReturnAuthResponseVoWhenLoginSucceeds() throws Exception {
        UserVo userVo = new UserVo("alice", "pwd", List.of("ROLE_USER"));

        UserDto dto = new UserDto();
        dto.setUsername("alice");
        dto.setPassword("pwd");

        AuthResponseDto authResponseDto = new AuthResponseDto("token-123", "Bearer");
        AuthResponseVo authResponseVo = new AuthResponseVo("token-123", "Bearer");

        Mockito.when(userMapper.voToDto(Mockito.any(UserVo.class))).thenReturn(dto);
        Mockito.when(authService.login(Mockito.any(UserDto.class))).thenReturn(Optional.of(authResponseDto));
        Mockito.when(authResponseMapper.dtoToVo(Mockito.any(AuthResponseDto.class))).thenReturn(authResponseVo);

        String requestBody = objectMapper.writeValueAsString(userVo);
        var result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn();

        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        String content = result.getResponse().getContentAsString();
        AuthResponseVo returned = objectMapper.readValue(content, AuthResponseVo.class);
        Assertions.assertThat(returned.token()).isEqualTo("token-123");
        Assertions.assertThat(returned.type()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("Should return 401 when login fails")
    void shouldReturnUnauthorizedWhenLoginFails() throws Exception {
        UserVo userVo = new UserVo("bob", "bad", List.of("ROLE_USER"));
        UserDto dto = new UserDto();
        dto.setUsername("bob");

        Mockito.when(userMapper.voToDto(Mockito.any(UserVo.class))).thenReturn(dto);
        Mockito.when(authService.login(Mockito.any(UserDto.class))).thenReturn(Optional.empty());

        String requestBody = objectMapper.writeValueAsString(userVo);
        var result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn();

        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        Assertions.assertThat(result.getResponse().getContentAsString()).isEqualTo("Invalid username or password");
    }
}
