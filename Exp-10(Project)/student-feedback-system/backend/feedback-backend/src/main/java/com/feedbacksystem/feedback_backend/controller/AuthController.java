package com.feedbacksystem.feedback_backend.controller;

import com.feedbacksystem.feedback_backend.dto.AuthResponse;
import com.feedbacksystem.feedback_backend.dto.LoginRequest;
import com.feedbacksystem.feedback_backend.dto.RegisterRequest;
import com.feedbacksystem.feedback_backend.model.Role;
import com.feedbacksystem.feedback_backend.model.User;
import com.feedbacksystem.feedback_backend.repository.UserRepository;
import com.feedbacksystem.feedback_backend.security.JwtTokenProvider;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager; // Hum 'Manager' ko use kar rahe hain
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController // Yeh batata hai ki yeh file ek API controller hai (jo JSON bhejega).
@RequestMapping("/api/auth") // Iska matlab hai ki is file ke saare API URLs "/api/auth" se shuru honge.
public class AuthController {

    // @Autowired Spring se bolta hai ki "Mujhe AuthenticationManager ka object do."
    // Yeh object login ko check karega (SecurityConfig mein define kiya hai).
    @Autowired
    private AuthenticationManager authenticationManager;

    // @Autowired Spring se bolta hai ki "Mujhe UserRepository ka object do."
    // Isse hum users ko database mein find/save karenge.
    @Autowired
    private UserRepository userRepository;

    // @Autowired Spring se bolta hai ki "Mujhe PasswordEncoder ka object do."
    // Isse hum passwords ko encrypt (hash) karenge.
    @Autowired
    private PasswordEncoder passwordEncoder;

    // @Autowired Spring se bolta hai ki "Mujhe JwtTokenProvider ka object do."
    // Isse hum login ke baad JWT token banayenge.
    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * Yeh login endpoint hai.
     * URL: POST /api/auth/login
     */
    @PostMapping("/login")
    // Yeh function login request ko handle karta hai.
    // @Valid -- validation rules check karta hai (DTO file se)
    // @RequestBody -- frontend se aaye JSON ko LoginRequest object mein badal deta hai.
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        // **YEH SABSE IMPORTANT LINE HAI (LOGIN LOGIC)**
        // Yeh 'AuthenticationManager' ko bolta hai ki "is email aur password ko check karo."
        // Manager parde ke piche hamare 'SecurityConfig' wale setup (BCrypt) ko use karta hai.
        // Agar email/password galat hai, toh yeh line ek error throw karegi (aur code ruk jayega).
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // Agar upar wali line successful hai (matlab login sahi hai), toh:
        // Yeh line Spring Security ko batati hai ki yeh user ab "logged in" hai.
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Yeh line 'JwtTokenProvider' ka use karke naya JWT token banati hai.
        String jwt = tokenProvider.generateToken(authentication);

        // Hum user ko database se firse find kar rahe hain...
        User user = userRepository.findByEmail(loginRequest.getEmail())
                // ...taaki hum response mein uska email aur naam bhej sakein.
                .orElseThrow(() -> new RuntimeException("Error: User not found after authentication."));
    
        // Yeh line frontend ko token, role, email aur naam (JSON mein) '200 OK' response ke sath bhejti hai.
        return ResponseEntity.ok(new AuthResponse(
                jwt,
                user.getRole().name(),
                user.getEmail(),
                user.getName()
        ));
    }

    /**
     * Yeh register endpoint hai.
     * URL: POST /api/auth/register
     */
    @PostMapping("/register")
    // Yeh function naye user ko register karta hai.
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        
        // Yeh check kar raha hai ki email pehle se use toh nahi hua hai.
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            // Agar email pehle se use hua hai, toh '400 Bad Request' error bhej do.
            return ResponseEntity.badRequest().body("Error: Email is already taken!");
        }

        // Yahan hum 'User.builder()' ka use karke ek naya User object bana rahe hain.
        User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                // **YEH BHI IMPORTANT HAI!** Hum user ka password encrypt karke save kar rahe hain.
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                // Naye user ko default 'ROLE_STUDENT' de rahe hain.
                .role(Role.ROLE_STUDENT)
                .createdAt(LocalDateTime.now())
                .build();

        // Naye user ko MongoDB database mein save kar rahe hain.
        userRepository.save(user);

        // Frontend ko success message bhej rahe hain.
        return ResponseEntity.ok("User registered successfully!");
    }
}