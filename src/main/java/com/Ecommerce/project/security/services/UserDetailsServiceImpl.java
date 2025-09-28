package com.Ecommerce.project.security.services;

import com.Ecommerce.project.repositories.UserRepository;
import com.Ecommerce.project.model.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

//    @Override
//    @Transactional //Transaction ka matlab hota hai series of operations jo ek unit ke roop me execute hote hain.|| Either sab successful hote hain (commit) || Ya kuch galat hua to sab rollback ho jata hai.
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
////        fetching user as model from database but spring security required it as userDetails
//        User user = userRepository.findByUserName(username)
//                .orElseThrow(() -> new UsernameNotFoundException("user not found with username:  " +username));
////here we are storing  them from user to userDetails object
//        return UserDetailsImpl.build(user);
//    }


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("🔥 Trying to load user: " + username);

        // DB se user fetch karo
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        System.out.println("🔥 Found user in DB: " + user.getUserName() + " | Password: " + user.getPassword());

        // Ensure userDetails object returns encoded password from DB
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        System.out.println("🔥 UserDetails password: " + userDetails.getPassword());

        return userDetails;
    }




}
