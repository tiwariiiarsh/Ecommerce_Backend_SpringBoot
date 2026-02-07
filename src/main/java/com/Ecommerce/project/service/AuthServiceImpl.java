package com.Ecommerce.project.service;

import com.Ecommerce.project.model.AppRole;
import com.Ecommerce.project.model.Role;
import com.Ecommerce.project.model.User;
import com.Ecommerce.project.payload.AuthenticationResult;
import com.Ecommerce.project.payload.UserDTO;
import com.Ecommerce.project.payload.UserResponse;
import com.Ecommerce.project.repositories.RoleRepository;
import com.Ecommerce.project.repositories.UserRepository;
import com.Ecommerce.project.security.jwt.JwtUtils;
import com.Ecommerce.project.security.request.LoginRequest;
import com.Ecommerce.project.security.request.SignupRequest;
import com.Ecommerce.project.security.response.MessageResponse;
import com.Ecommerce.project.security.response.UserInfofResponse;
import com.Ecommerce.project.security.services.UserDetailsImpl;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthServiceImpl implements AuthService{

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public AuthenticationResult login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        used for token formate
//        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

//        use in cookie formate
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());


        UserInfofResponse response =  new UserInfofResponse(userDetails.getId(),
                userDetails.getUsername(), roles,userDetails.getEmail(), jwtCookie.toString());

        return new AuthenticationResult(response,jwtCookie);
    }

    @Override
    public ResponseEntity<MessageResponse> register(SignupRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "seller":
                        Role modRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @Override
    public Object getCurrentUserDetails(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfofResponse response = new UserInfofResponse(userDetails.getId(),
                userDetails.getUsername(), roles);
        return null;
    }

    @Override
    public ResponseCookie getUserLogOut() {
        return jwtUtils.getCleanCookie();
    }

    @Override
    public UserResponse getAllSellers(Pageable pageDetails) {
        Page<User> allUser = userRepository.findByRoleName(AppRole.ROLE_SELLER,pageDetails);

        List<UserDTO> userDTOS = allUser.getContent()
                .stream()
                .map(p -> modelMapper.map(p,UserDTO.class))
                .toList();

        UserResponse response = new UserResponse();
        response.setContent(userDTOS);
        response.setPageNumber(allUser.getNumber());
        response.setPageSize(allUser.getSize());
        response.setTotalPages(allUser.getTotalPages());
        response.setTotalElements(allUser.getTotalElements());
        response.setLastPage(allUser.isLast());
        return response;
    }
}
