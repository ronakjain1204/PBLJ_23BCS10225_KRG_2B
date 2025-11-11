package com.feedbacksystem.feedback_backend.service;

import com.feedbacksystem.feedback_backend.model.User;
import com.feedbacksystem.feedback_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Service to load user details for Spring Security.
 */
@Service // Marks this as a Spring service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired // Asks Spring to give us the UserRepository
    private UserRepository userRepository;

    /**
     * This method is called by Spring Security when a user tries to authenticate.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Find the user by email using our repository
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // 2. Get the user's role (e.g., ROLE_STUDENT)
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().name())
        );

        // 3. Return a Spring Security User object
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}