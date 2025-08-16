package com.akertesz.task_manager_api.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.akertesz.task_manager_api.model.TaskPriority;
import com.akertesz.task_manager_api.model.TaskStatus;

class DtoTest {

    private LocalDateTime now;
    private TaskDto taskDto;
    private CreateTaskRequest createTaskRequest;
    private UpdateTaskRequest updateTaskRequest;
    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        
        taskDto = new TaskDto();
        createTaskRequest = new CreateTaskRequest();
        updateTaskRequest = new UpdateTaskRequest();
        loginRequest = new LoginRequest();
        loginResponse = new LoginResponse(Optional.of("token"), "message");
        registerRequest = new RegisterRequest();
    }

    // TaskDto Tests
    @Test
    void testTaskDto_DefaultConstructor() {
        // Act
        TaskDto dto = new TaskDto();
        
        // Assert
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getTitle());
        assertNull(dto.getDescription());
        assertNull(dto.getStatus());
        assertNull(dto.getPriority());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
        assertNull(dto.getDueDate());
    }

    @Test
    void testTaskDto_ConstructorWithAllFields() {
        // Arrange
        String id = "123";
        String title = "Test Task";
        String description = "Test Description";
        TaskStatus status = TaskStatus.PENDING;
        TaskPriority priority = TaskPriority.MEDIUM;
        LocalDateTime createdAt = now;
        LocalDateTime updatedAt = now.plusHours(1);
        LocalDateTime dueDate = now.plusDays(7);
        
        // Act
        TaskDto dto = new TaskDto(id, title, description, status, priority, createdAt, updatedAt, dueDate);
        
        // Assert
        assertEquals(id, dto.getId());
        assertEquals(title, dto.getTitle());
        assertEquals(description, dto.getDescription());
        assertEquals(status, dto.getStatus());
        assertEquals(priority, dto.getPriority());
        assertEquals(createdAt, dto.getCreatedAt());
        assertEquals(updatedAt, dto.getUpdatedAt());
        assertEquals(dueDate, dto.getDueDate());
    }

    @Test
    void testTaskDto_SettersAndGetters() {
        // Arrange
        String id = "123";
        String title = "Test Task";
        String description = "Test Description";
        TaskStatus status = TaskStatus.IN_PROGRESS;
        TaskPriority priority = TaskPriority.HIGH;
        LocalDateTime createdAt = now;
        LocalDateTime updatedAt = now.plusHours(1);
        LocalDateTime dueDate = now.plusDays(7);
        
        // Act
        taskDto.setId(id);
        taskDto.setTitle(title);
        taskDto.setDescription(description);
        taskDto.setStatus(status);
        taskDto.setPriority(priority);
        taskDto.setCreatedAt(createdAt);
        taskDto.setUpdatedAt(updatedAt);
        taskDto.setDueDate(dueDate);
        
        // Assert
        assertEquals(id, taskDto.getId());
        assertEquals(title, taskDto.getTitle());
        assertEquals(description, taskDto.getDescription());
        assertEquals(status, taskDto.getStatus());
        assertEquals(priority, taskDto.getPriority());
        assertEquals(createdAt, taskDto.getCreatedAt());
        assertEquals(updatedAt, taskDto.getUpdatedAt());
        assertEquals(dueDate, taskDto.getDueDate());
    }

    @Test
    void testTaskDto_EdgeCases() {
        // Test with empty strings
        taskDto.setTitle("");
        taskDto.setDescription("");
        assertEquals("", taskDto.getTitle());
        assertEquals("", taskDto.getDescription());
        
        // Test with null values
        taskDto.setTitle(null);
        taskDto.setDescription(null);
        assertNull(taskDto.getTitle());
        assertNull(taskDto.getDescription());
        
        // Test with very long strings
        String longString = "a".repeat(1000);
        taskDto.setTitle(longString);
        assertEquals(longString, taskDto.getTitle());
    }

    // CreateTaskRequest Tests
    @Test
    void testCreateTaskRequest_DefaultConstructor() {
        // Act
        CreateTaskRequest request = new CreateTaskRequest();
        
        // Assert
        assertNotNull(request);
        assertNull(request.getTitle());
        assertNull(request.getDescription());
        assertNull(request.getPriority());
        assertNull(request.getDueDate());
    }

    @Test
    void testCreateTaskRequest_SettersAndGetters() {
        // Arrange
        String title = "New Task";
        String description = "New Description";
        TaskPriority priority = TaskPriority.URGENT;
        LocalDateTime dueDate = now.plusDays(5);
        
        // Act
        createTaskRequest.setTitle(title);
        createTaskRequest.setDescription(description);
        createTaskRequest.setPriority(priority);
        createTaskRequest.setDueDate(dueDate);
        
        // Assert
        assertEquals(title, createTaskRequest.getTitle());
        assertEquals(description, createTaskRequest.getDescription());
        assertEquals(priority, createTaskRequest.getPriority());
        assertEquals(dueDate, createTaskRequest.getDueDate());
    }

    @Test
    void testCreateTaskRequest_EdgeCases() {
        // Test with empty strings
        createTaskRequest.setTitle("");
        createTaskRequest.setDescription("");
        assertEquals("", createTaskRequest.getTitle());
        assertEquals("", createTaskRequest.getDescription());
        
        // Test with null values
        createTaskRequest.setTitle(null);
        createTaskRequest.setDescription(null);
        assertNull(createTaskRequest.getTitle());
        assertNull(createTaskRequest.getDescription());
        
        // Test with very long strings
        String longString = "a".repeat(1000);
        createTaskRequest.setTitle(longString);
        assertEquals(longString, createTaskRequest.getTitle());
    }

    // UpdateTaskRequest Tests
    @Test
    void testUpdateTaskRequest_DefaultConstructor() {
        // Act
        UpdateTaskRequest request = new UpdateTaskRequest();
        
        // Assert
        assertNotNull(request);
        assertNull(request.getTitle());
        assertNull(request.getDescription());
        assertNull(request.getPriority());
        assertNull(request.getDueDate());
        assertNull(request.getStatus());
    }

    @Test
    void testUpdateTaskRequest_SettersAndGetters() {
        // Arrange
        String title = "Updated Task";
        String description = "Updated Description";
        TaskPriority priority = TaskPriority.LOW;
        LocalDateTime dueDate = now.plusDays(10);
        TaskStatus status = TaskStatus.COMPLETED;
        
        // Act
        updateTaskRequest.setTitle(title);
        updateTaskRequest.setDescription(description);
        updateTaskRequest.setPriority(priority);
        updateTaskRequest.setDueDate(dueDate);
        updateTaskRequest.setStatus(status);
        
        // Assert
        assertEquals(title, updateTaskRequest.getTitle());
        assertEquals(description, updateTaskRequest.getDescription());
        assertEquals(priority, updateTaskRequest.getPriority());
        assertEquals(dueDate, updateTaskRequest.getDueDate());
        assertEquals(status, updateTaskRequest.getStatus());
    }

    @Test
    void testUpdateTaskRequest_EdgeCases() {
        // Test with empty strings
        updateTaskRequest.setTitle("");
        updateTaskRequest.setDescription("");
        assertEquals("", updateTaskRequest.getTitle());
        assertEquals("", updateTaskRequest.getDescription());
        
        // Test with null values
        updateTaskRequest.setTitle(null);
        updateTaskRequest.setDescription(null);
        assertNull(updateTaskRequest.getTitle());
        assertNull(updateTaskRequest.getDescription());
        
        // Test with very long strings
        String longString = "a".repeat(1000);
        updateTaskRequest.setTitle(longString);
        assertEquals(longString, updateTaskRequest.getTitle());
    }

    // LoginRequest Tests
    @Test
    void testLoginRequest_DefaultConstructor() {
        // Act
        LoginRequest request = new LoginRequest();
        
        // Assert
        assertNotNull(request);
        assertNull(request.getUsername());
        assertNull(request.getPassword());
    }

    @Test
    void testLoginRequest_SettersAndGetters() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        
        // Act
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        
        // Assert
        assertEquals(username, loginRequest.getUsername());
        assertEquals(password, loginRequest.getPassword());
    }

    @Test
    void testLoginRequest_EdgeCases() {
        // Test with empty strings
        loginRequest.setUsername("");
        loginRequest.setPassword("");
        assertEquals("", loginRequest.getUsername());
        assertEquals("", loginRequest.getPassword());
        
        // Test with null values
        loginRequest.setUsername(null);
        loginRequest.setPassword(null);
        assertNull(loginRequest.getUsername());
        assertNull(loginRequest.getPassword());
        
        // Test with very long strings
        String longString = "a".repeat(1000);
        loginRequest.setUsername(longString);
        loginRequest.setPassword(longString);
        assertEquals(longString, loginRequest.getUsername());
        assertEquals(longString, loginRequest.getPassword());
    }

    // LoginResponse Tests
    @Test
    void testLoginResponse_ConstructorWithToken() {
        // Arrange
        String token = "jwt.token.here";
        String message = "Login successful";
        
        // Act
        LoginResponse response = new LoginResponse(Optional.of(token), message);
        
        // Assert
        assertTrue(response.getToken().isPresent());
        assertEquals(token, response.getToken().get());
        assertEquals(message, response.getMessage());
    }

    @Test
    void testLoginResponse_ConstructorWithoutToken() {
        // Arrange
        String message = "Login failed";
        
        // Act
        LoginResponse response = new LoginResponse(Optional.empty(), message);
        
        // Assert
        assertFalse(response.getToken().isPresent());
        assertEquals(message, response.getMessage());
    }

    @Test
    void testLoginResponse_SettersAndGetters() {
        // Arrange
        String token = "new.token.here";
        String message = "Updated message";
        
        // Act
        loginResponse.setToken(Optional.of(token));
        loginResponse.setMessage(message);
        
        // Assert
        assertTrue(loginResponse.getToken().isPresent());
        assertEquals(token, loginResponse.getToken().get());
        assertEquals(message, loginResponse.getMessage());
    }

    @Test
    void testLoginResponse_EdgeCases() {
        // Test with null token
        loginResponse.setToken(null);
        assertNull(loginResponse.getToken());
        
        // Test with empty message
        loginResponse.setMessage("");
        assertEquals("", loginResponse.getMessage());
        
        // Test with null message
        loginResponse.setMessage(null);
        assertNull(loginResponse.getMessage());
        
        // Test with very long message
        String longMessage = "a".repeat(1000);
        loginResponse.setMessage(longMessage);
        assertEquals(longMessage, loginResponse.getMessage());
    }

    // RegisterRequest Tests
    @Test
    void testRegisterRequest_DefaultConstructor() {
        // Act
        RegisterRequest request = new RegisterRequest();
        
        // Assert
        assertNotNull(request);
        assertNull(request.getUsername());
        assertNull(request.getEmail());
        assertNull(request.getPassword());
    }

    @Test
    void testRegisterRequest_SettersAndGetters() {
        // Arrange
        String username = "newuser";
        String email = "new@example.com";
        String password = "password123";
        
        // Act
        registerRequest.setUsername(username);
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        
        // Assert
        assertEquals(username, registerRequest.getUsername());
        assertEquals(email, registerRequest.getEmail());
        assertEquals(password, registerRequest.getPassword());
    }

    @Test
    void testRegisterRequest_EdgeCases() {
        // Test with empty strings
        registerRequest.setUsername("");
        registerRequest.setEmail("");
        registerRequest.setPassword("");
        assertEquals("", registerRequest.getUsername());
        assertEquals("", registerRequest.getEmail());
        assertEquals("", registerRequest.getPassword());
        
        // Test with null values
        registerRequest.setUsername(null);
        registerRequest.setEmail(null);
        registerRequest.setPassword(null);
        assertNull(registerRequest.getUsername());
        assertNull(registerRequest.getEmail());
        assertNull(registerRequest.getPassword());
        
        // Test with very long strings
        String longString = "a".repeat(1000);
        registerRequest.setUsername(longString);
        registerRequest.setEmail(longString);
        registerRequest.setPassword(longString);
        assertEquals(longString, registerRequest.getUsername());
        assertEquals(longString, registerRequest.getEmail());
        assertEquals(longString, registerRequest.getPassword());
    }

    // Special Character Tests
    @Test
    void testDtos_WithSpecialCharacters() {
        // Test usernames with special characters
        String specialUsername = "user@123+test";
        loginRequest.setUsername(specialUsername);
        registerRequest.setUsername(specialUsername);
        assertEquals(specialUsername, loginRequest.getUsername());
        assertEquals(specialUsername, registerRequest.getUsername());
        
        // Test emails with special characters
        String specialEmail = "user+tag@example.com";
        registerRequest.setEmail(specialEmail);
        assertEquals(specialEmail, registerRequest.getEmail());
        
        // Test titles with special characters
        String specialTitle = "Task with @#$%^&*() characters";
        taskDto.setTitle(specialTitle);
        createTaskRequest.setTitle(specialTitle);
        updateTaskRequest.setTitle(specialTitle);
        assertEquals(specialTitle, taskDto.getTitle());
        assertEquals(specialTitle, createTaskRequest.getTitle());
        assertEquals(specialTitle, updateTaskRequest.getTitle());
    }

    // Unicode Tests
    @Test
    void testDtos_WithUnicodeCharacters() {
        // Test with unicode characters
        String unicodeString = "Tâsk with ñáccénts and 中文 and 🚀 emojis";
        taskDto.setTitle(unicodeString);
        createTaskRequest.setTitle(unicodeString);
        updateTaskRequest.setTitle(unicodeString);
        assertEquals(unicodeString, taskDto.getTitle());
        assertEquals(unicodeString, createTaskRequest.getTitle());
        assertEquals(unicodeString, updateTaskRequest.getTitle());
        
        // Test usernames with unicode
        String unicodeUsername = "usérñame";
        loginRequest.setUsername(unicodeUsername);
        registerRequest.setUsername(unicodeUsername);
        assertEquals(unicodeUsername, loginRequest.getUsername());
        assertEquals(unicodeUsername, registerRequest.getUsername());
    }

    // Boundary Tests
    @Test
    void testDtos_BoundaryValues() {
        // Test with minimum length strings
        String minString = "a";
        taskDto.setTitle(minString);
        assertEquals(minString, taskDto.getTitle());
        
        // Test with single character
        loginRequest.setUsername("a");
        assertEquals("a", loginRequest.getUsername());
        
        // Test with maximum reasonable length
        String maxString = "a".repeat(10000);
        taskDto.setDescription(maxString);
        assertEquals(maxString, taskDto.getDescription());
    }

    // Null Safety Tests
    @Test
    void testDtos_NullSafety() {
        // Test that setting null doesn't cause exceptions
        assertDoesNotThrow(() -> {
            taskDto.setId(null);
            taskDto.setTitle(null);
            taskDto.setDescription(null);
            taskDto.setStatus(null);
            taskDto.setPriority(null);
            taskDto.setCreatedAt(null);
            taskDto.setUpdatedAt(null);
            taskDto.setDueDate(null);
        });
        
        assertDoesNotThrow(() -> {
            createTaskRequest.setTitle(null);
            createTaskRequest.setDescription(null);
            createTaskRequest.setPriority(null);
            createTaskRequest.setDueDate(null);
        });
        
        assertDoesNotThrow(() -> {
            updateTaskRequest.setTitle(null);
            updateTaskRequest.setDescription(null);
            updateTaskRequest.setPriority(null);
            updateTaskRequest.setDueDate(null);
            updateTaskRequest.setStatus(null);
        });
        
        assertDoesNotThrow(() -> {
            loginRequest.setUsername(null);
            loginRequest.setPassword(null);
        });
        
        assertDoesNotThrow(() -> {
            registerRequest.setUsername(null);
            registerRequest.setEmail(null);
            registerRequest.setPassword(null);
        });
    }

    // Immutability Tests
    @Test
    void testDtos_Immutability() {
        // Test that setting values doesn't affect other instances
        TaskDto dto1 = new TaskDto();
        TaskDto dto2 = new TaskDto();
        
        dto1.setTitle("Title 1");
        dto2.setTitle("Title 2");
        
        assertEquals("Title 1", dto1.getTitle());
        assertEquals("Title 2", dto2.getTitle());
        assertNotEquals(dto1.getTitle(), dto2.getTitle());
    }

    // Performance Tests
    @Test
    void testDtos_Performance() {
        // Test that setting/getting values is fast
        long startTime = System.nanoTime();
        
        for (int i = 0; i < 1000; i++) {
            taskDto.setTitle("Title " + i);
            String title = taskDto.getTitle();
            assertNotNull(title);
        }
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        
        // Should complete in reasonable time (less than 1 second)
        assertTrue(duration < 1_000_000_000L, "DTO operations should be fast");
    }
}
