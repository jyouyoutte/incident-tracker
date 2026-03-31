package com.incident.tracker.application.service.impl;

import com.incident.tracker.application.dto.auth.AuthResponseDto;
import com.incident.tracker.application.dto.auth.RoleDto;
import com.incident.tracker.application.dto.auth.UserDto;
import com.incident.tracker.application.service.AuthService;
import com.incident.tracker.domain.model.Role;
import com.incident.tracker.domain.model.User;
import com.incident.tracker.domain.port.auth.RoleRepositoryPort;
import com.incident.tracker.domain.port.auth.UserRepositoryPort;
import com.incident.tracker.infrastructure.security.exception.UserAlreadyExistsException;
import com.incident.tracker.infrastructure.security.exception.UserNotCreatedException;
import com.incident.tracker.infrastructure.security.provider.JwtTokenProvider;
import com.incident.tracker.mapper.RoleMapper;
import com.incident.tracker.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserRepositoryPort repositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final RoleRepositoryPort roleRepositoryPort;

    public AuthServiceImpl(UserRepositoryPort repositoryPort,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider,
                           AuthenticationManager authenticationManager,
                           UserMapper userMapper,
                           RoleMapper roleMapper, RoleRepositoryPort roleRepositoryPort) {
        this.repositoryPort = repositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.roleRepositoryPort = roleRepositoryPort;
    }

    @Override
    public Optional<UserDto> findByUsername(String username) {
        Optional<User> user = repositoryPort.findByUsername(username);
        if(user.isPresent()){
            logger.info("User with username={}  found", username);
            return Optional.of(userMapper.entityToDto(user.get()));
        }
        logger.info("User with username={} not found", username);
        return Optional.empty();
    }

    @Override
    public Optional<UserDto> register(UserDto userDto) {
        Optional<UserDto> userFound = findByUsername(userDto.getUsername());
        if (userFound.isPresent()) {
            logger.warn("User with username={} already exists", userDto.getUsername());
             throw new UserAlreadyExistsException(userDto.getUsername());
        }
        // Determine requested role names (support multiple roles)
        List<String> requestedRoles;
        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            requestedRoles = userDto.getRoles();
        } else {
            throw new UserNotCreatedException("No role specified for user: " + userDto.getUsername());
        }

        // Resolve role entities
        List<Role> roleEntities = requestedRoles.stream()
                .map(rn -> roleRepositoryPort.findByName(rn)
                        .orElseThrow(() -> new UserNotCreatedException("Role not found: " + rn)))
                .toList();

        // encode password
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Map to entity and attach role entities so the join table users_roles is populated
        User userEntity = userMapper.dtoToEntity(userDto);
        userEntity.setRoles(roleEntities);

        Optional<User> newUser = repositoryPort.saveUser(userEntity);
        userDto.setRoles(roleEntities.stream().map(Role::getName).toList());
        if(newUser.isEmpty()){
            logger.error("Failed to register user with username={}", userDto.getUsername());
            throw new UserNotCreatedException("User with username " + userDto.getUsername() + " not created: " + "An unexpected error occurred during user creation. Please try again later.");
        }
        logger.info("User with username={} registered successfully", userDto.getUsername());
        return Optional.of(userMapper.entityToDto(newUser.get()));
    }

    @Override
    public Optional<AuthResponseDto> login(UserDto userDto) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword())
            );
            if (authentication.isAuthenticated()) {
                repositoryPort.findByUsername(userDto.getUsername());
                logger.info("Authentication successful for username={}", userDto.getUsername());

               String token = jwtTokenProvider.generateToken((UserDetails) authentication.getPrincipal());
               logger.info("JWT token generated for username={}", userDto.getUsername());
               return Optional.of(new AuthResponseDto(token, "Bearer"));
            }
        }
        catch (Exception e){
            logger.error("Authentication failed for username={}", e.getMessage());
            return Optional.empty();
        }
        return Optional.empty();
    }
}
