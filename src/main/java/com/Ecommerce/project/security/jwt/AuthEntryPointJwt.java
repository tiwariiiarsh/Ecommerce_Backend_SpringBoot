package com.Ecommerce.project.security.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
//Ye AuthEntryPointJwt class banti hai unauthorized access (401 error) ko handle karne ke liye.
// Normally, agar tum Spring Security use karte ho aur koi protected API without login / invalid JWT token access karega, toh Spring ek default HTML error page return karega (jo REST API ke liye useful nahi hota).
// ⚡ Is problem ko solve karne ke liye hum ye class banate hain:
//Ye AuthenticationEntryPoint implement karti hai.
//Jab bhi koi unauthenticated request aayegi, toh ye class trigger hogi.
//Ye request ko handle karke ek custom JSON response bhejegi jisme status, error, message aur path hoga.
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint { //AuthenticationEntryPoint -->> unauthorized access handle karta hai.
    //for  error handling and debugging message in console,, taaki error track kiya ja sake.
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    //    commence() method tab call hota hai jab user unauthenticated hota hai aur protected resource access karna chahta hai.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        logger.error("Unauthorized error: {}", authException.getMessage());
//        Response ka type application/json set hota hai.
//        Status code 401 (Unauthorized) set hota hai.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//  Ek JSON response body banayi gayi hai jo client ko return hogi.
//  Isme status code, error type, error message, aur request ka path included hai.
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());
//        ObjectMapper Jackson library ka class hai.
//        Serialization:Java object → JSON / byte stream / string me convert karna.
//        Deserialization: JSON / byte stream / string → Java object me convert karna.

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }

}

