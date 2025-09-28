package com.Ecommerce.project.security.services;

import com.Ecommerce.project.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
public class UserDetailsImpl implements UserDetails {
//    private static final long serialVersionUID = 1L; ka matlab hai class ka ek stable version ID define karna, jo serialization/deserialization ke time use hoti hai taaki compatibility issue na ho.
    private static  final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private  String email;
    @JsonIgnore //🔹 Use Cases:Sensitive data hide karna (like password, token).||Unwanted data ignore karna jo API consumer ke liye zaruri nahi.||Circular references avoid karne ke liye (entity relations me).
    private String password;
    private Collection<? extends GrantedAuthority>authorities;

    public UserDetailsImpl(Long id,String username,String email,String password,
            Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
        this.id=id;
        this.email=email;
        this.password=password;
        this.username=username;
    }

    public static UserDetailsImpl build(User user) {

        // 1️⃣ User ke roles uthaye ja rahe hain aur unhe GrantedAuthority me convert kiya ja raha hai
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toList());

        // 2️⃣ Naya UserDetailsImpl object return ho raha hai jisme user ke details + authorities hain
        return new UserDetailsImpl(
                user.getUserId(),
                user.getUserName(),   // ✅ username sahi place par
                user.getEmail(),      // ✅ email sahi place par
                user.getPassword(),   // ✅ password sahi place par (encoded)
                authorities
        );

    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

//    manually written
    @Override
    public boolean equals(Object o){
        if (this==o){
            return true;
        }
        if (o==null || getClass() != o.getClass()){
            return false;
        }
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id,user.id);
    }
}
