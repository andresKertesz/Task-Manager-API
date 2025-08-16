package com.akertesz.task_manager_api.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.akertesz.task_manager_api.config.JwtUtil;
import com.akertesz.task_manager_api.dto.LoginRequest;
import com.akertesz.task_manager_api.dto.LoginResponse;
import com.akertesz.task_manager_api.dto.RegisterRequest;
import com.akertesz.task_manager_api.exception.InvalidRequestException;
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
        // Validate input parameters
        if (loginRequest == null) {
            throw new IllegalArgumentException("LoginRequest cannot be null");
        }
        
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            
            if (authentication.isAuthenticated()) {
                String token = jwtUtil.generateToken(username);
                return new LoginResponse(Optional.of(token), "Login successful");
            } else {
                throw new BadCredentialsException("Authentication failed");
            }
        } catch (BadCredentialsException e) {
            throw e; // Re-throw BadCredentialsException for proper handling
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    public LoginResponse register(RegisterRequest registerRequest) {
        // Validate input parameters
        if (registerRequest == null) {
            throw new IllegalArgumentException("RegisterRequest cannot be null");
        }
        
        String username = registerRequest.getUsername();
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();
        
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        // Check if user already exists
        if (userRepository.existsByUsername(username)) {
            throw new InvalidRequestException("Username already exists");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new InvalidRequestException("Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setVersion(0L);
        userRepository.save(user);
        
        // Generate token for auto-login after registration
        String token = jwtUtil.generateToken(user.getUsername());
        return new LoginResponse(Optional.of(token), "Registration successful");
    }
}
