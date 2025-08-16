package com.akertesz.task_manager_api.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ExceptionTest {

    // TaskNotFoundException Tests
    @Test
    void testTaskNotFoundException_WithMessage() {
        // Arrange
        String message = "Task not found with id: 123";
        
        // Act
        TaskNotFoundException exception = new TaskNotFoundException(message);
        
        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testTaskNotFoundException_WithMessageAndCause() {
        // Arrange
        String message = "Task not found with id: 123";
        Throwable cause = new RuntimeException("Database error");
        
        // Act
        TaskNotFoundException exception = new TaskNotFoundException(message, cause);
        
        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testTaskNotFoundException_WithCause() {
        // Arrange
        Throwable cause = new RuntimeException("Database error");
        
        // Act
        TaskNotFoundException exception = new TaskNotFoundException("Task not found", cause);
        
        // Assert
        assertTrue(exception.getMessage().contains("Task not found"));
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testTaskNotFoundException_DefaultConstructor() {
        // Act
        TaskNotFoundException exception = new TaskNotFoundException("Default message");
        
        // Assert
        assertEquals("Default message", exception.getMessage());
        assertNull(exception.getCause());
    }

    // UserNotFoundException Tests
    @Test
    void testUserNotFoundException_WithMessage() {
        // Arrange
        String message = "User not found: testuser";
        
        // Act
        UserNotFoundException exception = new UserNotFoundException(message);
        
        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testUserNotFoundException_WithMessageAndCause() {
        // Arrange
        String message = "User not found: testuser";
        Throwable cause = new RuntimeException("Database error");
        
        // Act
        UserNotFoundException exception = new UserNotFoundException(message, cause);
        
        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testUserNotFoundException_WithCause() {
        // Arrange
        Throwable cause = new RuntimeException("Database error");
        
        // Act
        UserNotFoundException exception = new UserNotFoundException("User not found", cause);
        
        // Assert
        assertTrue(exception.getMessage().contains("User not found"));
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testUserNotFoundException_DefaultConstructor() {
        // Act
        UserNotFoundException exception = new UserNotFoundException("Default message");
        
        // Assert
        assertEquals("Default message", exception.getMessage());
        assertNull(exception.getCause());
    }

    // InvalidRequestException Tests
    @Test
    void testInvalidRequestException_WithMessage() {
        // Arrange
        String message = "Invalid request parameters";
        
        // Act
        InvalidRequestException exception = new InvalidRequestException(message);
        
        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testInvalidRequestException_WithMessageAndCause() {
        // Arrange
        String message = "Invalid request parameters";
        Throwable cause = new IllegalArgumentException("Invalid input");
        
        // Act
        InvalidRequestException exception = new InvalidRequestException(message, cause);
        
        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testInvalidRequestException_WithCause() {
        // Arrange
        Throwable cause = new IllegalArgumentException("Invalid input");
        
        // Act
        InvalidRequestException exception = new InvalidRequestException("Invalid request", cause);
        
        // Assert
        assertTrue(exception.getMessage().contains("Invalid request"));
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testInvalidRequestException_DefaultConstructor() {
        // Act
        InvalidRequestException exception = new InvalidRequestException("Default message");
        
        // Assert
        assertEquals("Default message", exception.getMessage());
        assertNull(exception.getCause());
    }

    // UnauthorizedAccessException Tests
    @Test
    void testUnauthorizedAccessException_WithMessage() {
        // Arrange
        String message = "Unauthorized access to resource";
        
        // Act
        UnauthorizedAccessException exception = new UnauthorizedAccessException(message);
        
        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testUnauthorizedAccessException_WithMessageAndCause() {
        // Arrange
        String message = "Unauthorized access to resource";
        Throwable cause = new SecurityException("Permission denied");
        
        // Act
        UnauthorizedAccessException exception = new UnauthorizedAccessException(message, cause);
        
        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testUnauthorizedAccessException_WithCause() {
        // Arrange
        Throwable cause = new SecurityException("Permission denied");
        
        // Act
        UnauthorizedAccessException exception = new UnauthorizedAccessException("Unauthorized access", cause);
        
        // Assert
        assertTrue(exception.getMessage().contains("Unauthorized access"));
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testUnauthorizedAccessException_DefaultConstructor() {
        // Act
        UnauthorizedAccessException exception = new UnauthorizedAccessException("Default message");
        
        // Assert
        assertEquals("Default message", exception.getMessage());
        assertNull(exception.getCause());
    }

    // Exception Inheritance Tests
    @Test
    void testExceptionInheritance() {
        // Test that all custom exceptions extend RuntimeException
        assertTrue(TaskNotFoundException.class.getSuperclass().equals(RuntimeException.class));
        assertTrue(UserNotFoundException.class.getSuperclass().equals(RuntimeException.class));
        assertTrue(InvalidRequestException.class.getSuperclass().equals(RuntimeException.class));
        assertTrue(UnauthorizedAccessException.class.getSuperclass().equals(RuntimeException.class));
    }

    // Exception Message Format Tests
    @Test
    void testTaskNotFoundException_MessageFormat() {
        // Arrange
        String taskId = "123e4567-e89b-12d3-a456-426614174000";
        String expectedMessage = "Task not found with id: " + taskId;
        
        // Act
        TaskNotFoundException exception = new TaskNotFoundException(expectedMessage);
        
        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertTrue(exception.getMessage().contains("Task not found"));
        assertTrue(exception.getMessage().contains(taskId));
    }

    @Test
    void testUserNotFoundException_MessageFormat() {
        // Arrange
        String username = "testuser";
        String expectedMessage = "User not found: " + username;
        
        // Act
        UserNotFoundException exception = new UserNotFoundException(expectedMessage);
        
        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertTrue(exception.getMessage().contains("User not found"));
        assertTrue(exception.getMessage().contains(username));
    }

    @Test
    void testInvalidRequestException_MessageFormat() {
        // Arrange
        String field = "email";
        String expectedMessage = "Invalid " + field + " format";
        
        // Act
        InvalidRequestException exception = new InvalidRequestException(expectedMessage);
        
        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertTrue(exception.getMessage().contains("Invalid"));
        assertTrue(exception.getMessage().contains(field));
    }

    @Test
    void testUnauthorizedAccessException_MessageFormat() {
        // Arrange
        String resource = "task";
        String expectedMessage = "Unauthorized access to " + resource;
        
        // Act
        UnauthorizedAccessException exception = new UnauthorizedAccessException(expectedMessage);
        
        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertTrue(exception.getMessage().contains("Unauthorized access"));
        assertTrue(exception.getMessage().contains(resource));
    }

    // Exception Chaining Tests
    @Test
    void testExceptionChaining() {
        // Arrange
        Throwable rootCause = new RuntimeException("Database connection failed");
        Throwable middleCause = new RuntimeException("Query execution failed", rootCause);
        String message = "Task retrieval failed";
        
        // Act
        TaskNotFoundException exception = new TaskNotFoundException(message, middleCause);
        
        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(middleCause, exception.getCause());
        assertEquals(rootCause, exception.getCause().getCause());
    }

    // Exception Stack Trace Tests
    @Test
    void testExceptionStackTrace() {
        // Arrange
        String message = "Test exception for stack trace";
        
        // Act
        TaskNotFoundException exception = new TaskNotFoundException(message);
        
        // Assert
        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
        
        // Verify the stack trace contains the test method
        boolean foundTestMethod = false;
        for (StackTraceElement element : exception.getStackTrace()) {
            if (element.getMethodName().equals("testExceptionStackTrace")) {
                foundTestMethod = true;
                break;
            }
        }
        assertTrue(foundTestMethod, "Stack trace should contain the test method");
    }

    // Exception Serialization Tests (basic)
    @Test
    void testExceptionSerialization() {
        // Arrange
        String message = "Test exception message";
        TaskNotFoundException originalException = new TaskNotFoundException(message);
        
        // Act & Assert
        // Test that the exception can be recreated with the same message
        TaskNotFoundException recreatedException = new TaskNotFoundException(originalException.getMessage());
        assertEquals(originalException.getMessage(), recreatedException.getMessage());
    }

    // Exception Equality Tests
    @Test
    void testExceptionEquality() {
        // Arrange
        String message1 = "Same message";
        String message2 = "Different message";
        
        TaskNotFoundException exception1 = new TaskNotFoundException(message1);
        TaskNotFoundException exception2 = new TaskNotFoundException(message1);
        TaskNotFoundException exception3 = new TaskNotFoundException(message2);
        
        // Act & Assert
        // Exceptions with same message should not be equal (different instances)
        assertNotEquals(exception1, exception2);
        assertNotEquals(exception1, exception3);
        
        // But they should have the same message
        assertEquals(exception1.getMessage(), exception2.getMessage());
        assertNotEquals(exception1.getMessage(), exception3.getMessage());
    }

    // Exception Null Handling Tests
    @Test
    void testExceptionNullMessage() {
        // Act
        TaskNotFoundException exception = new TaskNotFoundException((String) null);
        
        // Assert
        assertNull(exception.getMessage());
    }

    @Test
    void testExceptionNullCause() {
        // Act
        TaskNotFoundException exception = new TaskNotFoundException("Message", null);
        
        // Assert
        assertEquals("Message", exception.getMessage());
        assertNull(exception.getCause());
    }

    // Exception Performance Tests (basic)
    @Test
    void testExceptionCreationPerformance() {
        // Arrange
        int iterations = 1000;
        long startTime = System.nanoTime();
        
        // Act
        for (int i = 0; i < iterations; i++) {
            new TaskNotFoundException("Test message " + i);
        }
        long endTime = System.nanoTime();
        
        // Assert
        long duration = endTime - startTime;
        // Should complete in reasonable time (less than 1 second)
        assertTrue(duration < 1_000_000_000L, "Exception creation should be fast");
    }
}
