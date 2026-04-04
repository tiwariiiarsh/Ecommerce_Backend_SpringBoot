package com.Ecommerce.project.security;


import com.Ecommerce.project.model.AppRole;
import com.Ecommerce.project.model.Role;
import com.Ecommerce.project.model.User;
import com.Ecommerce.project.repositories.RoleRepository;
import com.Ecommerce.project.repositories.UserRepository;
import com.Ecommerce.project.security.jwt.AuthEntryPointJwt;
import com.Ecommerce.project.security.jwt.AuthTokenFilter;
import com.Ecommerce.project.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizeHandler;


    @Value("${frontend.url}")
    private String frontEndUrl;

    // CORS Fix v2




    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000",
                frontEndUrl  // pulls from env var on Render
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public  AuthTokenFilter authenticationJwtTokenFilter(){
        return new AuthTokenFilter();
    }



    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        // UserDetailsService: user fetch karega DB se
        authenticationProvider.setUserDetailsService(userDetailsService);


//        spring security internally checks passwordEncoder.matches(rawPasswordFromUser,encodedPasswordFromDB);
        // PasswordEncoder: password match karega (BCrypt, NoOp, etc.)
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    //provide us our own SecurityFilterChain and uses own security method ,don't use default security method
//    authenticationJwtTokenFilter()--it will run during validation of jwt
//    UsernamePasswordAuthenticationFilter.class---- this iwll run after validation if valid token found cant runn this class otherwise it will check for login the user and then give jwt
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))// CORS ko enable kar diya
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizeHandler))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/order/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/seller/**").hasAnyRole("ADMIN","SELLER")
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .anyRequest().authenticated()
                );


//        it will check during login wuth username and password with the help of DaoAuthenticationProvider
        http.authenticationProvider(authenticationProvider());
        return http.build();
    }


    //http.httpBasic(withDefaults());

//        spring security by default header bhejta h,jisme tumhari website kisi bhi iframe me open nhihogi
//        but H2 console by default iframe me hi open hota h,so we need to assign the sameorigin fameoption given below
//        so that H2 console will not give us an error


//        Ye line Spring Security me custom JWT filter ko add karti hai.
//        addFilterBefore ka matlab---->Mera custom filter (authenticationJwtTokenFilter()) ko insert karo UsernamePasswordAuthenticationFilter se pehle."
//        Agar JWT valid hai toh user already authenticated hoga.,,,,Nahi toh baaki filters apna kaam karenge.


//        http.build() ka kaam hai:
//👉 In saare configurations ko final form me convert karke ek SecurityFilterChain object banana.
//        Aur ye SecurityFilterChain hi woh cheez hai jisko Spring Security use karta hai har request ko check karne ke liye.

//Ye kuch endpoints ko completely ignore karta hai (Swagger docs etc.). from authenti...
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web -> web.ignoring().requestMatchers(
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**")
        );
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authconfig) throws Exception {
        return authconfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


//    this is in doubt full why and how it is used check during revision
//    🔹 Why CommandLineRunner is used?
//    CommandLineRunner ek Spring Boot interface hai jo app start hote hi ek baar run hota hai.
//    Iska use initial setup ya dummy/test data insert karne ke liye hota hai.
//    Jaise: roles (ROLE_USER, ROLE_ADMIN, ROLE_SELLER) ko DB me ensure karna, default users banana.
//    Matlab: Application start hote hi system ke liye zaroori data hamesha ready ho.
    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("🔥🔥🔥 NEW SECURITY CONFIG LOADED in init block 🔥🔥🔥");
            // Retrieve or create roles
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> {
                        Role newUserRole = new Role(AppRole.ROLE_USER);
                        return roleRepository.save(newUserRole);
                    });

            Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                    .orElseGet(() -> {
                        Role newSellerRole = new Role(AppRole.ROLE_SELLER);
                        return roleRepository.save(newSellerRole);
                    });

            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> {
                        Role newAdminRole = new Role(AppRole.ROLE_ADMIN);
                        return roleRepository.save(newAdminRole);
                    });

            Set<Role> userRoles = Set.of(userRole);
            Set<Role> sellerRoles = Set.of(sellerRole);
            Set<Role> adminRoles = Set.of(userRole, sellerRole, adminRole);


            // Create users if not already present
            if (!userRepository.existsByUserName("user1")) {
                User user1 = new User("user1", "user1@example.com", passwordEncoder.encode("password1"));
                userRepository.save(user1);
            }

            if (!userRepository.existsByUserName("seller1")) {
                User seller1 = new User("seller1", "seller1@example.com", passwordEncoder.encode("password2"));
                userRepository.save(seller1);
            }

            if (!userRepository.existsByUserName("admin")) {
                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminPass"));
                userRepository.save(admin);
            }

            // Update roles for existing users
            userRepository.findByUserName("user1").ifPresent(user -> {
                user.setRoles(userRoles);
                userRepository.save(user);
            });

            userRepository.findByUserName("seller1").ifPresent(seller -> {
                seller.setRoles(sellerRoles);
                userRepository.save(seller);
            });

            userRepository.findByUserName("admin").ifPresent(admin -> {
                admin.setRoles(adminRoles);
                userRepository.save(admin);
            });
        };

    }
}
