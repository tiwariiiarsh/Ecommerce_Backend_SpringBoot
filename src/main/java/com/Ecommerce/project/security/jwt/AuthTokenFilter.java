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
//this is our custom authentication filter which will run and check during checking of all filters
@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

//    HTTP Request (concept)
//        ↓
//HttpServletRequest (Java implementation)

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // 🔥 JWT filter ko login/signup pe mat chalao
        if (path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        logger.debug("AuthTokenFilter called for URI: {}", request.getRequestURI());
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
//                User ki detail (roles, permissions, etc.) fetch karta hai UserDetailsService ke through.
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//                Ek authentication object banata hai jo Spring Security context me store hoga.
//                Parameters:
//                userDetails → Current user ka object
//                null → Password not required kyunki JWT se verify kar liya
//                userDetails.getAuthorities() → Roles/permissions
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null,
                                userDetails.getAuthorities());
                logger.debug("Roles from JWT: {}", userDetails.getAuthorities());
//                Request ke extra details (IP, session info, etc.) attach karta hai authentication object ke saath.
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                Security context me authentication set karta hai → iska matlab ab user authenticated maana jaayega.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

//  Request ko aage agle filter/endpoint tak forward karta hai.
        filterChain.doFilter(request, response);
    }


//  this will run after login when cookie or header have jwt then it will parse jwt from cookie and used for validation
    private String parseJwt(HttpServletRequest request) {
        String jwt = jwtUtils.getJwtFromCookies(request);
        logger.debug("AuthTokenFilter.java: {}", jwt);
        return jwt;

    }

}

