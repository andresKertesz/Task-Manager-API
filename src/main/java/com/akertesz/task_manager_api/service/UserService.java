package com.akertesz.task_manager_api.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.akertesz.task_manager_api.config.JwtUtil;
import com.akertesz.task_manager_api.dto.LoginRequest;
import com.akertesz.task_manager_api.dto.LoginResponse;
import com.akertesz.task_manager_api.dto.RegisterRequest;
import com.akertesz.task_manager_api.model.User;
import com.akertesz.task_manager_api.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            
            if (authentication.isAuthenticated()) {
                String token = jwtUtil.generateToken(loginRequest.getUsername());
                return new LoginResponse(Optional.of(token), "Login successful");
            } else {
                return new LoginResponse(Optional.empty(), "Authentication failed");
            }
        } catch (Exception e) {
            return new LoginResponse(Optional.empty(), "Invalid username or password");
        }
    }

    public LoginResponse register(RegisterRequest registerRequest) {
        // Check if user already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return new LoginResponse(Optional.empty(), "Username already exists");
        }
        
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return new LoginResponse(Optional.empty(), "Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        
        userRepository.save(user);
        
        // Generate token for auto-login after registration
        String token = jwtUtil.generateToken(user.getUsername());
        return new LoginResponse(Optional.of(token), "Registration successful");
    }
}
