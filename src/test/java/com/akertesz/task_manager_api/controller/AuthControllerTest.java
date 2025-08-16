package com.akertesz.task_manager_api.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.akertesz.task_manager_api.dto.LoginRequest;
import com.akertesz.task_manager_api.dto.LoginResponse;
import com.akertesz.task_manager_api.dto.RegisterRequest;
import com.akertesz.task_manager_api.exception.GlobalExceptionHandler;
import com.akertesz.task_manager_api.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private LoginResponse loginResponse;
    private LoginResponse registerResponse;
    private String testToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        
        objectMapper = new ObjectMapper();
        
        testToken = "jwt.token.here";
        
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
        
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("new@example.com");
        registerRequest.setPassword("password123");
        
        loginResponse = new LoginResponse(Optional.of(testToken), "Login successful");
        registerResponse = new LoginResponse(Optional.of(testToken), "Registration successful");
    }

    // Login Tests
    @Test
    void testLogin_Success() throws Exception {
        // Arrange
        when(userService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(testToken))
                .andExpect(jsonPath("$.message").value("Login successful"));

        verify(userService).login(any(LoginRequest.class));
    }

    @Test
    void testLogin_InvalidRequest() throws Exception {
        // Arrange
        LoginRequest invalidRequest = new LoginRequest();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_EmptyUsername() throws Exception {
        // Arrange
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setUsername("");
        invalidRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_EmptyPassword() throws Exception {
        // Arrange
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setUsername("testuser");
        invalidRequest.setPassword("");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_WhitespaceUsername() throws Exception {
        // Arrange
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setUsername("   ");
        invalidRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_WhitespacePassword() throws Exception {
        // Arrange
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setUsername("testuser");
        invalidRequest.setPassword("   ");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_NullRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_ServiceException() throws Exception {
        // Arrange
        when(userService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError());

        verify(userService).login(any(LoginRequest.class));
    }

    // Registration Tests
    @Test
    void testRegister_Success() throws Exception {
        // Arrange
        when(userService.register(any(RegisterRequest.class))).thenReturn(registerResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(testToken))
                .andExpect(jsonPath("$.message").value("Registration successful"));

        verify(userService).register(any(RegisterRequest.class));
    }

    @Test
    void testRegister_InvalidRequest() throws Exception {
        // Arrange
        RegisterRequest invalidRequest = new RegisterRequest();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_EmptyUsername() throws Exception {
        // Arrange
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername("");
        invalidRequest.setEmail("new@example.com");
        invalidRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_EmptyEmail() throws Exception {
        // Arrange
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername("newuser");
        invalidRequest.setEmail("");
        invalidRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_EmptyPassword() throws Exception {
        // Arrange
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername("newuser");
        invalidRequest.setEmail("new@example.com");
        invalidRequest.setPassword("");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_WhitespaceUsername() throws Exception {
        // Arrange
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername("   ");
        invalidRequest.setEmail("new@example.com");
        invalidRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_WhitespaceEmail() throws Exception {
        // Arrange
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername("newuser");
        invalidRequest.setEmail("   ");
        invalidRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_WhitespacePassword() throws Exception {
        // Arrange
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername("newuser");
        invalidRequest.setEmail("new@example.com");
        invalidRequest.setPassword("   ");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_NullRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_ServiceException() throws Exception {
        // Arrange
        when(userService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isInternalServerError());

        verify(userService).register(any(RegisterRequest.class));
    }

    // Edge Cases
    @Test
    void testLogin_UserWithSpecialCharacters() throws Exception {
        // Arrange
        LoginRequest specialRequest = new LoginRequest();
        specialRequest.setUsername("user@123");
        specialRequest.setPassword("password123");
        
        when(userService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(testToken));

        verify(userService).login(any(LoginRequest.class));
    }

    @Test
    void testRegister_UserWithSpecialCharacters() throws Exception {
        // Arrange
        RegisterRequest specialRequest = new RegisterRequest();
        specialRequest.setUsername("user@123");
        specialRequest.setEmail("user+tag@example.com");
        specialRequest.setPassword("password123");
        
        when(userService.register(any(RegisterRequest.class))).thenReturn(registerResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(testToken));

        verify(userService).register(any(RegisterRequest.class));
    }

    @Test
    void testLogin_LongUsername() throws Exception {
        // Arrange
        LoginRequest longRequest = new LoginRequest();
        longRequest.setUsername("a".repeat(100));
        longRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_LongUsername() throws Exception {
        // Arrange
        RegisterRequest longRequest = new RegisterRequest();
        longRequest.setUsername("a".repeat(100));
        longRequest.setEmail("new@example.com");
        longRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_LongPassword() throws Exception {
        // Arrange
        LoginRequest longRequest = new LoginRequest();
        longRequest.setUsername("testuser");
        longRequest.setPassword("a".repeat(1000));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_LongPassword() throws Exception {
        // Arrange
        RegisterRequest longRequest = new RegisterRequest();
        longRequest.setUsername("newuser");
        longRequest.setEmail("new@example.com");
        longRequest.setPassword("a".repeat(1000));

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longRequest)))
                .andExpect(status().isBadRequest());
    }

    // Content Type Tests
    @Test
    void testLogin_WrongContentType() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.TEXT_PLAIN)
                .content("plain text"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void testRegister_WrongContentType() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.TEXT_PLAIN)
                .content("plain text"))
                .andExpect(status().isUnsupportedMediaType());
    }

    // Method Not Allowed Tests
    @Test
    void testLogin_GetMethodNotAllowed() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/auth/login"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testRegister_GetMethodNotAllowed() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/auth/register"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testLogin_PutMethodNotAllowed() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/auth/login"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testRegister_PutMethodNotAllowed() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/auth/register"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testLogin_DeleteMethodNotAllowed() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/auth/login"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testRegister_DeleteMethodNotAllowed() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/auth/register"))
                .andExpect(status().isMethodNotAllowed());
    }
}
