package com.incident.tracker.infrastructure.security.service;

import com.incident.tracker.domain.model.User;
import com.incident.tracker.domain.port.auth.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Custom implementation of UserDetailsService to load user details from the database.
 * This service is used by Spring Security to authenticate users based on their username.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserRepositoryPort userRepositoryPort;

    public CustomUserDetailsService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Loading user by username: {}", username);

        Optional<User> user = userRepositoryPort.findByUsername(username);
        if (user.isEmpty()) {
            logger.warn("User not found: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        } else {
            logger.info("User found: {}", user.get().getUsername());
            return new org.springframework.security.core.userdetails.User(
                    user.get().getUsername(),
                    user.get().getPassword(),
                    user.get().getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority(role.getName()))
                            .toList());
        }
    }
}
