package com.Ecommerce.project.service;

import com.Ecommerce.project.payload.AuthenticationResult;
import com.Ecommerce.project.payload.UserResponse;
import com.Ecommerce.project.security.request.LoginRequest;
import com.Ecommerce.project.security.request.SignupRequest;
import com.Ecommerce.project.security.response.MessageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface AuthService {
    AuthenticationResult login(LoginRequest loginRequest);

    ResponseEntity<MessageResponse> register(SignupRequest signUpRequest);

    Object getCurrentUserDetails(Authentication authentication);

    ResponseCookie getUserLogOut();

    UserResponse getAllSellers(Pageable pageDetails);
}
