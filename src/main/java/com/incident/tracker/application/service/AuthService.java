package com.incident.tracker.application.service;

import com.incident.tracker.application.dto.auth.AuthResponseDto;
import com.incident.tracker.application.dto.auth.UserDto;

import java.util.Optional;
/** * AuthService is an interface that defines the contract for authentication-related operations in the application.
 * It provides methods to find a user by their username and to save a new user.
 */
public interface AuthService {
    Optional<UserDto> findByUsername (String username);
    Optional<UserDto> register(UserDto userDto);
    Optional<AuthResponseDto> login(UserDto userDto);

}
