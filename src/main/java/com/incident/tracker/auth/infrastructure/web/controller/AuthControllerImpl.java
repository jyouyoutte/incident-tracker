package com.incident.tracker.auth.infrastructure.web.controller;

import com.incident.tracker.auth.application.dto.AuthResponseDto;
import com.incident.tracker.auth.application.dto.UserDto;
import com.incident.tracker.auth.application.error.ErrorResponseDto;
import com.incident.tracker.auth.application.service.AuthService;
import com.incident.tracker.auth.infrastructure.web.vo.UserVo;
import com.incident.tracker.mapper.AuthResponseMapper;
import com.incident.tracker.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class AuthControllerImpl implements AuthController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AuthService authService;
    private final UserMapper userMapper;
    private final AuthResponseMapper authResponseMapper;

    public AuthControllerImpl(AuthService authService, UserMapper userMapper, AuthResponseMapper authResponseMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
        this.authResponseMapper = authResponseMapper;
    }

    @Override
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
                    .body(new ErrorResponseDto("SERVER_ERROR", "Failed to register user", LocalDateTime.now()));
        }
    }

    @Override
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

