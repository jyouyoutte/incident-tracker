package com.incident.tracker.infrastructure.web;

import com.incident.tracker.application.dto.auth.AuthResponseDto;
import com.incident.tracker.application.dto.auth.UserDto;
import com.incident.tracker.application.service.AuthService;
import com.incident.tracker.infrastructure.web.vo.auth.AuthResponseVo;
import com.incident.tracker.infrastructure.web.vo.auth.UserVo;
import com.incident.tracker.mapper.AuthResponseMapper;
import com.incident.tracker.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AuthService authService;
    private final UserMapper userMapper;
    private final AuthResponseMapper authResponseMapper;

    public AuthController(AuthService authService, UserMapper userMapper, AuthResponseMapper authResponseMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
        this.authResponseMapper = authResponseMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserVo userVo) {
        logger.info("HTTP POST /api/auth/register - Registering new user with username={}", userVo.username());
        Optional<UserDto> newUser = authService.register(userMapper.voToDto(userVo));
        if(newUser.isPresent()){
            // Map DTO to a response VO that contains id and roles (no password)
            var responseVo = userMapper.dtoToVo(newUser.get());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseVo);
        }else {
            // Return structured error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new com.incident.tracker.application.dto.error.ErrorResponseDto("SERVER_ERROR", "Failed to register user"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserVo userVo) {
        logger.info("HTTP GET /api/auth/login - login  user with username={}", userVo.username());
        Optional<AuthResponseDto> responseDto = authService.login(userMapper.voToDto(userVo));
        if(responseDto.isPresent()){
            logger.info("User {} logged in successfully", userVo.username());
            return ResponseEntity.ok(authResponseMapper.dtoToVo(responseDto.get()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }
}
