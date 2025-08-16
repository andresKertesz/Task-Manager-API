package com.akertesz.task_manager_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.akertesz.task_manager_api.config.JwtUtil;
import com.akertesz.task_manager_api.dto.LoginRequest;
import com.akertesz.task_manager_api.dto.LoginResponse;
import com.akertesz.task_manager_api.dto.RegisterRequest;
import com.akertesz.task_manager_api.exception.InvalidRequestException;
import com.akertesz.task_manager_api.model.User;
import com.akertesz.task_manager_api.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private String testToken;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setVersion(0L);

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("new@example.com");
        registerRequest.setPassword("password123");

        testToken = "jwt.token.here";
    }

    // Login Tests
    @Test
    void testLogin_Success() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtUtil.generateToken(loginRequest.getUsername())).thenReturn(testToken);

        // Act
        LoginResponse response = userService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.getToken().isPresent());
        assertEquals(testToken, response.getToken().get());
        assertEquals("Login successful", response.getMessage());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authentication).isAuthenticated();
        verify(jwtUtil).generateToken(loginRequest.getUsername());
    }

    @Test
    void testLogin_AuthenticationFailed() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            userService.login(loginRequest);
        });

        assertEquals("Authentication failed", exception.getMessage());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authentication).isAuthenticated();
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void testLogin_BadCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            userService.login(loginRequest);
        });

        assertEquals("Bad credentials", exception.getMessage());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void testLogin_GenericException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            userService.login(loginRequest);
        });

        assertEquals("Invalid username or password", exception.getMessage());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void testLogin_NullRequest() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            userService.login(null);
        });
    }

    // Registration Tests
    @Test
    void testRegister_Success() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(registerRequest.getUsername())).thenReturn(testToken);

        // Act
        LoginResponse response = userService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.getToken().isPresent());
        assertEquals(testToken, response.getToken().get());
        assertEquals("Registration successful", response.getMessage());
        
        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(registerRequest.getUsername());
    }

    @Test
    void testRegister_UsernameAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            userService.register(registerRequest);
        });

        assertEquals("Username already exists", exception.getMessage());
        
        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(userRepository, never()).existsByEmail(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            userService.register(registerRequest);
        });

        assertEquals("Email already exists", exception.getMessage());
        
        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void testRegister_NullRequest() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            userService.register(null);
        });
    }

    @Test
    void testRegister_EmptyUsername() {
        // Arrange
        registerRequest.setUsername("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.register(registerRequest);
        });
    }

    @Test
    void testRegister_EmptyEmail() {
        // Arrange
        registerRequest.setEmail("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.register(registerRequest);
        });
    }

    @Test
    void testRegister_EmptyPassword() {
        // Arrange
        registerRequest.setPassword("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.register(registerRequest);
        });
    }

    // Edge Cases
    @Test
    void testLogin_EmptyUsername() {
        // Arrange
        loginRequest.setUsername("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.login(loginRequest);
        });
    }

    @Test
    void testLogin_EmptyPassword() {
        // Arrange
        loginRequest.setPassword("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.login(loginRequest);
        });
    }

    @Test
    void testLogin_WhitespaceUsername() {
        // Arrange
        loginRequest.setUsername("   ");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.login(loginRequest);
        });
    }

    @Test
    void testLogin_WhitespacePassword() {
        // Arrange
        loginRequest.setPassword("   ");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.login(loginRequest);
        });
    }

    @Test
    void testRegister_WhitespaceUsername() {
        // Arrange
        registerRequest.setUsername("   ");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.register(registerRequest);
        });
    }

    @Test
    void testRegister_WhitespaceEmail() {
        // Arrange
        registerRequest.setEmail("   ");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.register(registerRequest);
        });
    }

    @Test
    void testRegister_WhitespacePassword() {
        // Arrange
        registerRequest.setPassword("   ");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.register(registerRequest);
        });
    }

    // Integration-like tests
    @Test
    void testRegisterAndLogin_Flow() {
        // Arrange - Register
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(registerRequest.getUsername())).thenReturn(testToken);

        // Act - Register
        LoginResponse registerResponse = userService.register(registerRequest);

        // Assert - Register
        assertNotNull(registerResponse);
        assertTrue(registerResponse.getToken().isPresent());
        assertEquals("Registration successful", registerResponse.getMessage());

        // Arrange - Login
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtUtil.generateToken(loginRequest.getUsername())).thenReturn(testToken);

        // Act - Login
        LoginResponse loginResponse = userService.login(loginRequest);

        // Assert - Login
        assertNotNull(loginResponse);
        assertTrue(loginResponse.getToken().isPresent());
        assertEquals("Login successful", loginResponse.getMessage());
    }

    @Test
    void testRegister_UserWithSpecialCharacters() {
        // Arrange
        registerRequest.setUsername("user@123");
        registerRequest.setEmail("user+tag@example.com");
        
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(registerRequest.getUsername())).thenReturn(testToken);

        // Act
        LoginResponse response = userService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.getToken().isPresent());
        assertEquals("Registration successful", response.getMessage());
    }

    @Test
    void testLogin_UserWithSpecialCharacters() {
        // Arrange
        loginRequest.setUsername("user@123");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtUtil.generateToken(loginRequest.getUsername())).thenReturn(testToken);

        // Act
        LoginResponse response = userService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.getToken().isPresent());
        assertEquals("Login successful", response.getMessage());
    }
}
