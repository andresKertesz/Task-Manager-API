package com.akertesz.task_manager_api.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ModelTest {

    private LocalDateTime now;
    private User testUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setVersion(0L);
        
        testTask = new Task();
        testTask.setId(UUID.randomUUID());
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(TaskStatus.PENDING);
        testTask.setPriority(TaskPriority.MEDIUM);
        testTask.setUser(testUser);
        testTask.setCreatedAt(now);
        testTask.setUpdatedAt(now);
        testTask.setDueDate(now.plusDays(7));
        testTask.setDeleted(false);
    }

    // User Model Tests
    @Test
    void testUser_DefaultConstructor() {
        // Act
        User user = new User();
        
        // Assert
        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getVersion());
    }

    @Test
    void testUser_SettersAndGetters() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String username = "newuser";
        String email = "new@example.com";
        String password = "newPassword";
        Long version = 1L;
        
        // Act
        testUser.setId(userId);
        testUser.setUsername(username);
        testUser.setEmail(email);
        testUser.setPassword(password);
        testUser.setVersion(version);
        
        // Assert
        assertEquals(userId, testUser.getId());
        assertEquals(username, testUser.getUsername());
        assertEquals(email, testUser.getEmail());
        assertEquals(password, testUser.getPassword());
        assertEquals(version, testUser.getVersion());
    }

    @Test
    void testUser_EdgeCases() {
        // Test with empty strings
        testUser.setUsername("");
        testUser.setEmail("");
        assertEquals("", testUser.getUsername());
        assertEquals("", testUser.getEmail());
        
        // Test with null values
        testUser.setUsername(null);
        testUser.setEmail(null);
        assertNull(testUser.getUsername());
        assertNull(testUser.getEmail());
        
        // Test with very long strings
        String longString = "a".repeat(1000);
        testUser.setUsername(longString);
        testUser.setEmail(longString);
        assertEquals(longString, testUser.getUsername());
        assertEquals(longString, testUser.getEmail());
    }

    // Task Model Tests
    @Test
    void testTask_DefaultConstructor() {
        // Act
        Task task = new Task();
        
        // Assert
        assertNotNull(task);
        assertNull(task.getId());
        assertNull(task.getTitle());
        assertNull(task.getDescription());
        assertNull(task.getStatus());
        assertNull(task.getPriority());
        assertNull(task.getUser());
        assertNull(task.getCreatedAt());
        assertNull(task.getUpdatedAt());
        assertNull(task.getDueDate());
        assertFalse(task.isDeleted());
    }

    @Test
    void testTask_SettersAndGetters() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        String title = "Updated Task";
        String description = "Updated Description";
        TaskStatus status = TaskStatus.IN_PROGRESS;
        TaskPriority priority = TaskPriority.HIGH;
        LocalDateTime createdAt = now.minusDays(1);
        LocalDateTime updatedAt = now.plusHours(1);
        LocalDateTime dueDate = now.plusDays(14);
        boolean isDeleted = true;
        
        // Act
        testTask.setId(taskId);
        testTask.setTitle(title);
        testTask.setDescription(description);
        testTask.setStatus(status);
        testTask.setPriority(priority);
        testTask.setCreatedAt(createdAt);
        testTask.setUpdatedAt(updatedAt);
        testTask.setDueDate(dueDate);
        testTask.setDeleted(isDeleted);
        
        // Assert
        assertEquals(taskId, testTask.getId());
        assertEquals(title, testTask.getTitle());
        assertEquals(description, testTask.getDescription());
        assertEquals(status, testTask.getStatus());
        assertEquals(priority, testTask.getPriority());
        assertEquals(createdAt, testTask.getCreatedAt());
        assertEquals(updatedAt, testTask.getUpdatedAt());
        assertEquals(dueDate, testTask.getDueDate());
        assertEquals(isDeleted, testTask.isDeleted());
    }

    @Test
    void testTask_EdgeCases() {
        // Test with empty strings
        testTask.setTitle("");
        testTask.setDescription("");
        assertEquals("", testTask.getTitle());
        assertEquals("", testTask.getDescription());
        
        // Test with null values
        testTask.setTitle(null);
        testTask.setDescription(null);
        assertNull(testTask.getTitle());
        assertNull(testTask.getDescription());
        
        // Test with very long strings
        String longString = "a".repeat(1000);
        testTask.setTitle(longString);
        testTask.setDescription(longString);
        assertEquals(longString, testTask.getTitle());
        assertEquals(longString, testTask.getDescription());
    }

    // TaskStatus Enum Tests
    @Test
    void testTaskStatus_Values() {
        // Act & Assert
        assertEquals(4, TaskStatus.values().length);
        assertTrue(contains(TaskStatus.values(), TaskStatus.PENDING));
        assertTrue(contains(TaskStatus.values(), TaskStatus.IN_PROGRESS));
        assertTrue(contains(TaskStatus.values(), TaskStatus.COMPLETED));
        assertTrue(contains(TaskStatus.values(), TaskStatus.CANCELLED));
    }

    @Test
    void testTaskStatus_ValueOf() {
        // Act & Assert
        assertEquals(TaskStatus.PENDING, TaskStatus.valueOf("PENDING"));
        assertEquals(TaskStatus.IN_PROGRESS, TaskStatus.valueOf("IN_PROGRESS"));
        assertEquals(TaskStatus.COMPLETED, TaskStatus.valueOf("COMPLETED"));
        assertEquals(TaskStatus.CANCELLED, TaskStatus.valueOf("CANCELLED"));
    }

    @Test
    void testTaskStatus_InvalidValueOf() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            TaskStatus.valueOf("INVALID_STATUS");
        });
    }

    // TaskPriority Enum Tests
    @Test
    void testTaskPriority_Values() {
        // Act & Assert
        assertEquals(4, TaskPriority.values().length);
        assertTrue(contains(TaskPriority.values(), TaskPriority.URGENT));
        assertTrue(contains(TaskPriority.values(), TaskPriority.HIGH));
        assertTrue(contains(TaskPriority.values(), TaskPriority.MEDIUM));
        assertTrue(contains(TaskPriority.values(), TaskPriority.LOW));
    }

    @Test
    void testTaskPriority_ValueOf() {
        // Act & Assert
        assertEquals(TaskPriority.URGENT, TaskPriority.valueOf("URGENT"));
        assertEquals(TaskPriority.HIGH, TaskPriority.valueOf("HIGH"));
        assertEquals(TaskPriority.MEDIUM, TaskPriority.valueOf("MEDIUM"));
        assertEquals(TaskPriority.LOW, TaskPriority.valueOf("LOW"));
    }

    @Test
    void testTaskPriority_InvalidValueOf() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            TaskPriority.valueOf("INVALID_PRIORITY");
        });
    }

    // Model Relationships Tests
    @Test
    void testTask_UserRelationship() {
        // Arrange
        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setUsername("anotheruser");
        
        // Act
        testTask.setUser(newUser);
        
        // Assert
        assertEquals(newUser, testTask.getUser());
        assertEquals(newUser.getId(), testTask.getUser().getId());
        assertEquals(newUser.getUsername(), testTask.getUser().getUsername());
    }

    @Test
    void testTask_UserRelationshipNull() {
        // Act
        testTask.setUser(null);
        
        // Assert
        assertNull(testTask.getUser());
    }

    // Model Validation Tests
    @Test
    void testUser_UsernameValidation() {
        // Test with various username formats
        String[] validUsernames = {
            "user123",
            "user_name",
            "user-name",
            "user.name",
            "user@domain",
            "user+tag",
            "123user",
            "user",
            "a"
        };
        
        for (String username : validUsernames) {
            testUser.setUsername(username);
            assertEquals(username, testUser.getUsername());
        }
    }

    @Test
    void testUser_EmailValidation() {
        // Test with various email formats
        String[] validEmails = {
            "user@example.com",
            "user+tag@example.com",
            "user.name@example.com",
            "user-name@example.com",
            "user123@example.com",
            "user@subdomain.example.com",
            "user@example.co.uk"
        };
        
        for (String email : validEmails) {
            testUser.setEmail(email);
            assertEquals(email, testUser.getEmail());
        }
    }

    @Test
    void testTask_TitleValidation() {
        // Test with various title formats
        String[] validTitles = {
            "Simple Task",
            "Task with 123 numbers",
            "Task with @#$%^&*() symbols",
            "Task with ñáccénts",
            "Task with 中文 characters",
            "Task with 🚀 emojis",
            "A", // Single character
            "a".repeat(1000) // Very long title
        };
        
        for (String title : validTitles) {
            testTask.setTitle(title);
            assertEquals(title, testTask.getTitle());
        }
    }

    // Model State Tests
    @Test
    void testTask_StatusTransitions() {
        // Test that status can be changed
        testTask.setStatus(TaskStatus.PENDING);
        assertEquals(TaskStatus.PENDING, testTask.getStatus());
        
        testTask.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, testTask.getStatus());
        
        testTask.setStatus(TaskStatus.COMPLETED);
        assertEquals(TaskStatus.COMPLETED, testTask.getStatus());
        
        testTask.setStatus(TaskStatus.CANCELLED);
        assertEquals(TaskStatus.CANCELLED, testTask.getStatus());
    }

    @Test
    void testTask_PriorityChanges() {
        // Test that priority can be changed
        testTask.setPriority(TaskPriority.LOW);
        assertEquals(TaskPriority.LOW, testTask.getPriority());
        
        testTask.setPriority(TaskPriority.MEDIUM);
        assertEquals(TaskPriority.MEDIUM, testTask.getPriority());
        
        testTask.setPriority(TaskPriority.HIGH);
        assertEquals(TaskPriority.HIGH, testTask.getPriority());
        
        testTask.setPriority(TaskPriority.URGENT);
        assertEquals(TaskPriority.URGENT, testTask.getPriority());
    }

    @Test
    void testTask_SoftDelete() {
        // Test soft delete functionality
        assertFalse(testTask.isDeleted());
        
        testTask.setDeleted(true);
        assertTrue(testTask.isDeleted());
        
        testTask.setDeleted(false);
        assertFalse(testTask.isDeleted());
    }

    // Model Timestamp Tests
    @Test
    void testTask_Timestamps() {
        // Test timestamp functionality
        LocalDateTime past = now.minusDays(1);
        LocalDateTime future = now.plusDays(1);
        
        testTask.setCreatedAt(past);
        testTask.setUpdatedAt(future);
        testTask.setDueDate(future);
        
        assertEquals(past, testTask.getCreatedAt());
        assertEquals(future, testTask.getUpdatedAt());
        assertEquals(future, testTask.getDueDate());
    }

    @Test
    void testTask_TimestampOrdering() {
        // Test that timestamps can be set in logical order
        LocalDateTime created = now.minusDays(7);
        LocalDateTime updated = now.minusDays(3);
        LocalDateTime due = now.plusDays(7);
        
        testTask.setCreatedAt(created);
        testTask.setUpdatedAt(updated);
        testTask.setDueDate(due);
        
        assertTrue(testTask.getCreatedAt().isBefore(testTask.getUpdatedAt()));
        assertTrue(testTask.getUpdatedAt().isBefore(testTask.getDueDate()));
    }

    // Model Immutability Tests
    @Test
    void testModels_Immutability() {
        // Test that setting values doesn't affect other instances
        User user1 = new User();
        User user2 = new User();
        
        user1.setUsername("user1");
        user2.setUsername("user2");
        
        assertEquals("user1", user1.getUsername());
        assertEquals("user2", user2.getUsername());
        assertNotEquals(user1.getUsername(), user2.getUsername());
        
        Task task1 = new Task();
        Task task2 = new Task();
        
        task1.setTitle("Task 1");
        task2.setTitle("Task 2");
        
        assertEquals("Task 1", task1.getTitle());
        assertEquals("Task 2", task2.getTitle());
        assertNotEquals(task1.getTitle(), task2.getTitle());
    }

    // Model Null Safety Tests
    @Test
    void testModels_NullSafety() {
        // Test that setting null doesn't cause exceptions
        assertDoesNotThrow(() -> {
            testUser.setId(null);
            testUser.setUsername(null);
            testUser.setEmail(null);
            testUser.setPassword(null);
            testUser.setVersion(null);
        });
        
        assertDoesNotThrow(() -> {
            testTask.setId(null);
            testTask.setTitle(null);
            testTask.setDescription(null);
            testTask.setStatus(null);
            testTask.setPriority(null);
            testTask.setUser(null);
            testTask.setCreatedAt(null);
            testTask.setUpdatedAt(null);
            testTask.setDueDate(null);
        });
    }

    // Model Performance Tests
    @Test
    void testModels_Performance() {
        // Test that setting/getting values is fast
        long startTime = System.nanoTime();
        
        for (int i = 0; i < 1000; i++) {
            testTask.setTitle("Title " + i);
            String title = testTask.getTitle();
            assertNotNull(title);
        }
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        
        // Should complete in reasonable time (less than 1 second)
        assertTrue(duration < 1_000_000_000L, "Model operations should be fast");
    }

    // Model UUID Tests
    @Test
    void testModels_UUIDHandling() {
        // Test UUID generation and handling
        UUID newUserId = UUID.randomUUID();
        UUID newTaskId = UUID.randomUUID();
        
        testUser.setId(newUserId);
        testTask.setId(newTaskId);
        
        assertEquals(newUserId, testUser.getId());
        assertEquals(newTaskId, testTask.getId());
        
        // Test that UUIDs are unique
        assertNotEquals(testUser.getId(), testTask.getId());
    }

    // Model Version Tests
    @Test
    void testUser_VersionHandling() {
        // Test version increment
        Long initialVersion = testUser.getVersion();
        testUser.setVersion(initialVersion + 1);
        
        assertEquals(initialVersion + 1, testUser.getVersion());
        
        // Test with null version
        testUser.setVersion(null);
        assertNull(testUser.getVersion());
    }

    // Helper method for array contains check
    private <T> boolean contains(T[] array, T value) {
        for (T item : array) {
            if (item.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
