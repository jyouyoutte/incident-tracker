package com.incident.tracker.auth.infrastructure.security.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Handles JWT creation and validation
 * Utility class for generating and validating JWT tokens.
 * It uses a secret key to sign the tokens and includes user information and roles in the token claims.
 */
@Component
public class JwtTokenProvider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Secret key loaded from application.yaml
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    // Token expiration time in milliseconds
    @Value("${app.jwt.expiration-time-ms}")
    private long jwtExpirationTimeMs;

    /** Generate a JWT token for an authenticated user */
    public String generateToken(UserDetails userDetails) {
        logger.info("Generating JWT token for user: {}", userDetails.getUsername());
        logger.info("Build the signing key from the secret");
        SecretKey key = getSigningKey();
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtExpirationTimeMs);
        return Jwts.builder()
                // Subject is the username
                .subject(userDetails.getUsername())
                // Include roles as a custom claim
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .issuedAt(now)
                .expiration(expirationDate)
                // Sign with HMAC-SHA256
                .signWith(key) // signature
                .compact();// transforms JWT to a String
    }

    /** Extract the username from a valid token */
    public String getUsernameFromToken(String token) {
        return extractClaimFromToken(token, Claims::getSubject);
    }

    /** Extract the roles from a valid token */
    public <T> T extractClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /** Validate the token signature and expiration*/
    public boolean validateToken( String token, UserDetails userDetails) {
        try {
            return userDetails!= null
                    && userDetails.getUsername().equals(getUsernameFromToken(token))
                    && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }


/** Parse and verify the token and extract all claims*/
    private Claims extractAllClaims(String token) {
        SecretKey key = getSigningKey();
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private @NonNull SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaimFromToken(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}
