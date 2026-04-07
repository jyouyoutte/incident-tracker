package com.incident.tracker.infrastructure.security.provider;

import com.incident.tracker.TestUtils;
import com.incident.tracker.auth.infrastructure.security.provider.JwtTokenProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.concurrent.TimeUnit;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        // secret must be base64 encoded; use a 32-byte key (256 bits) encoded in base64
        // "01234567890123456789012345678901" -> base64
        String secretBase64 = "MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=";
        TestUtils.setField(jwtTokenProvider, "jwtSecret", secretBase64);
        // set expiration to 10 seconds for stable non-expired tests
        TestUtils.setField(jwtTokenProvider, "jwtExpirationTimeMs", 10_000L);
    }

    @Test
    @DisplayName("Should generate token and extract username and roles correctly")
    void shouldGenerateTokenAndExtractClaims() {
        UserDetails user = new User("alice", "pwd", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        String token = jwtTokenProvider.generateToken(user);

        String username = jwtTokenProvider.getUsernameFromToken(token);
        @SuppressWarnings("unchecked")
        List<String> roles = jwtTokenProvider.extractClaimFromToken(token, claims -> claims.get("roles", List.class));

        Assertions.assertThat(token).isNotBlank();
        Assertions.assertThat(username).isEqualTo("alice");
        Assertions.assertThat(roles).containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("Should validate a valid token")
    void shouldValidateValidToken() {
        UserDetails user = new User("bob", "pwd", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        String token = jwtTokenProvider.generateToken(user);

        boolean valid = jwtTokenProvider.validateToken(token, user);

        Assertions.assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("Should reject an expired token")
    void shouldRejectExpiredToken() throws InterruptedException {
        // set expiration to 1ms to force immediate expiry
        TestUtils.setField(jwtTokenProvider, "jwtExpirationTimeMs", 1L);

        UserDetails user = new User("carol", "pwd", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        String token = jwtTokenProvider.generateToken(user);

        // slight sleep to allow token to be considered expired
        TimeUnit.MILLISECONDS.sleep(10);

        boolean valid = jwtTokenProvider.validateToken(token, user);

        Assertions.assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("Should reject token when username mismatch")
    void shouldRejectWhenUsernameMismatch() {
        UserDetails user = new User("dave", "pwd", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        String token = jwtTokenProvider.generateToken(user);

        UserDetails other = new User("eve", "pwd", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        boolean valid = jwtTokenProvider.validateToken(token, other);

        Assertions.assertThat(valid).isFalse();
    }
}
