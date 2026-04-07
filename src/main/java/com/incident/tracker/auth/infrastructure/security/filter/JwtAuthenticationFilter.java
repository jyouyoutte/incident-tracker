package com.incident.tracker.auth.infrastructure.security.filter;

import com.incident.tracker.auth.infrastructure.security.service.CustomUserDetailsService;
import com.incident.tracker.auth.infrastructure.security.provider.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Validates JWT on each request
 * Filter that intercepts incoming HTTP requests to validate JWT tokens.
 * It extracts the token from the Authorization header, validates it, and sets the authentication context if valid.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }


    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        logger.info("Extract the token from the Authorization header");
        String token = extractTokenFromHeader(request);

        // Validate and set the authentication context
        if (token != null) {
            String username = tokenProvider.getUsernameFromToken(token);
            logger.info("Token found for user: {}", username);
            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                logger.info("Load user details from the database");
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                if(tokenProvider.validateToken(token, userDetails)){
                    logger.info("Token is valid, setting authentication context");
                    // Create an authentication token with the user's authorities
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication in the security context
                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                } else {
                    logger.warn("Invalid token for user: {}", username);
                }
            }
        }else {
            logger.info("No JWT token found in the request");
        }
        // IMPORTANT : always Continue the filter chain
        filterChain.doFilter(request, response);
    }

    /** Extract Bearer token from the Authorization header */
    private String extractTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
