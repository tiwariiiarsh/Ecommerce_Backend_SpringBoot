
package com.Ecommerce.project.security.jwt;

import com.Ecommerce.project.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// This is our custom authentication filter.
// It runs once per request and checks whether a valid JWT cookie exists.
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger =
            LoggerFactory.getLogger(AuthTokenFilter.class);

    /*
     * HTTP request flow:
     * Browser -> HttpServletRequest -> Security filters -> Controller
     *
     * This filter reads the JWT from the configured cookie, validates it,
     * loads the user and sets the authenticated user in Spring Security.
     */

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();

        // Skip JWT authentication processing for login/signup endpoints.
        // These endpoints must be accessible before the user logs in.
        if (path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        logger.debug("AuthTokenFilter called for URI: {}",
                request.getRequestURI());

        try {
            // Read the JWT from the configured HTTP cookie.
            String jwt = parseJwt(request);

            // Never log the actual JWT because it is a sensitive credential.
            logger.debug("JWT cookie present: {}", jwt != null);

            if (jwt != null) {

                // Validate the token signature and expiration.
                boolean valid = jwtUtils.validateJwtToken(jwt);
                logger.debug("JWT valid: {}", valid);

                if (valid) {

                    // Extract the username from the validated JWT.
                    String username =
                            jwtUtils.getUserNameFromJwtToken(jwt);

                    // Load the user's details, including roles/authorities,
                    // from the database using UserDetailsService.
                    UserDetails userDetails =
                            userDetailsService.loadUserByUsername(username);

                    // Create an authenticated Spring Security object.
                    // The authorities determine which protected endpoints
                    // the user is allowed to access.
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // Attach request details such as the remote address.
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    // Store the authenticated user in the SecurityContext.
                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);

                    // Log the username and roles, but never the JWT itself.
                    logger.info("Authenticated user: {}", username);
                    logger.info("User authorities: {}",
                            userDetails.getAuthorities());
                }
            }

        } catch (Exception e) {
            // Log authentication failures for debugging.
            // The request continues through the filter chain; Spring Security
            // decides whether the endpoint requires authentication.
            logger.error("Cannot set user authentication: {}", e.getMessage(), e);
        }

        // Continue to the next security filter or the requested controller.
        filterChain.doFilter(request, response);
    }

    /*
     * Reads the JWT from the cookie whose name is configured in
     * spring.ecom.app.jwtCookieName.
     *
     * This project currently uses cookie-based JWT authentication,
     * not Authorization: Bearer authentication.
     */
    private String parseJwt(HttpServletRequest request) {
        String jwt = jwtUtils.getJwtFromCookies(request);

        // Log only whether the cookie exists; do not expose its value.
        logger.debug("JWT cookie received: {}", jwt != null);

        return jwt;
    }
}
