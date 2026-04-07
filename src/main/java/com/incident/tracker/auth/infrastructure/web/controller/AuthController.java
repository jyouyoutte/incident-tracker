package com.incident.tracker.auth.infrastructure.web.controller;

import com.incident.tracker.auth.application.error.ErrorResponseDto;
import com.incident.tracker.auth.infrastructure.web.vo.UserVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public interface AuthController {

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account. This operation is restricted to users with the 'ADMIN' role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserVo.class)) }),
            @ApiResponse(responseCode = "403", description = "Access Denied - ADMIN role required", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)) })
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserVo userVo);

    @Operation(
            summary = "User login",
            description = "Authenticates a user and returns an access token (JWT)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = { @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "401", description = "Invalid username or password", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserVo userVo);
}
