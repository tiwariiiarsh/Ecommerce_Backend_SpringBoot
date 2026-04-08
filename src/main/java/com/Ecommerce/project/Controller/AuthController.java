package com.Ecommerce.project.Controller;

import com.Ecommerce.project.config.AppConstants;
import com.Ecommerce.project.payload.AuthenticationResult;
import com.Ecommerce.project.repositories.RoleRepository;
import com.Ecommerce.project.repositories.UserRepository;
import com.Ecommerce.project.security.jwt.JwtUtils;
import com.Ecommerce.project.security.request.LoginRequest;
import com.Ecommerce.project.security.request.SignupRequest;
import com.Ecommerce.project.security.response.MessageResponse;
import com.Ecommerce.project.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
       AuthenticationResult result = authService.login(loginRequest);


//        incase of token formate
//        return ResponseEntity.ok(response);

//        incase of cookie formate
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                result.getJwtCookie().toString())
                .body(result.getResponse());
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
       return  authService.register(signUpRequest);
    }


    @GetMapping("/username")
    public String currentUserName(Authentication authentication){
        if (authentication!=null){
            return authentication.getName();
        }else {
            return "";
        }
    }


    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){
        return ResponseEntity.ok().body(authService.getCurrentUserDetails(authentication));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser(){
        ResponseCookie cookie = authService.getUserLogOut();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                        cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }

    @GetMapping("/sellers")
    public ResponseEntity<?> getAllSellers(
            @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber
    ){

        Sort sortByAndOrder = Sort.by(AppConstants.SORT_USER_BY).descending();
        Pageable pageDetails = PageRequest.of(pageNumber,Integer.parseInt(AppConstants.PAGE_SIZE),sortByAndOrder);
        return ResponseEntity.ok(authService.getAllSellers(pageDetails));
    }


}




//User → /signin with credentials.
//Spring Security → username/password validate karega.
//Agar correct hai → JWT token generate karega.
//Response me JWT token + user info + roles jayega.
//Next requests me client Authorization: Bearer <token> bhejega.

