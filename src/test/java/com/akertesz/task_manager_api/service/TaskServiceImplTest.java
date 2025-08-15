package com.akertesz.task_manager_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.akertesz.task_manager_api.dto.TaskDto;
import com.akertesz.task_manager_api.exception.InvalidRequestException;
import com.akertesz.task_manager_api.exception.TaskNotFoundException;
import com.akertesz.task_manager_api.exception.UserNotFoundException;
import com.akertesz.task_manager_api.model.Task;
import com.akertesz.task_manager_api.model.TaskPriority;
import com.akertesz.task_manager_api.model.TaskStatus;
import com.akertesz.task_manager_api.model.User;
import com.akertesz.task_manager_api.repository.TaskRepository;
import com.akertesz.task_manager_api.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User testUser;
    private Task testTask;
    private UUID taskId;
    private String username;

    @BeforeEach
    void setUp() {
        username = "testuser";
        taskId = UUID.randomUUID();
        
        testUser = new User();
        testUser.setUsername(username);
        
        testTask = new Task();
        testTask.setId(taskId);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(TaskStatus.PENDING);
        testTask.setPriority(TaskPriority.MEDIUM);
        testTask.setUser(testUser);
        testTask.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testValidStatusTransition_PendingToInProgress() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        TaskDto result = taskService.changeTaskStatusWithValidation(taskId, TaskStatus.IN_PROGRESS, username);

        // Assert
        assertNotNull(result);
        verify(taskRepository).save(testTask);
        assertEquals(TaskStatus.IN_PROGRESS, testTask.getStatus());
    }

    @Test
    void testValidStatusTransition_PendingToCompleted() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        TaskDto result = taskService.changeTaskStatusWithValidation(taskId, TaskStatus.COMPLETED, username);

        // Assert
        assertNotNull(result);
        verify(taskRepository).save(testTask);
        assertEquals(TaskStatus.COMPLETED, testTask.getStatus());
    }

    @Test
    void testInvalidStatusTransition_CompletedToInProgress() {
        // Arrange
        testTask.setStatus(TaskStatus.COMPLETED);
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.of(testTask));

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            taskService.changeTaskStatusWithValidation(taskId, TaskStatus.IN_PROGRESS, username);
        });

        assertTrue(exception.getMessage().contains("Invalid status transition from COMPLETED to IN_PROGRESS"));
        assertTrue(exception.getMessage().contains("No transitions allowed (final state)"));
    }

    @Test
    void testInvalidStatusTransition_PendingToPending() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.of(testTask));

        // Act
        TaskDto result = taskService.changeTaskStatusWithValidation(taskId, TaskStatus.PENDING, username);

        // Assert
        assertNotNull(result);
        // Should allow same status (no change)
        assertEquals(TaskStatus.PENDING, testTask.getStatus());
    }

    @Test
    void testValidStatusTransition_CancelledToPending() {
        // Arrange
        testTask.setStatus(TaskStatus.CANCELLED);
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        TaskDto result = taskService.changeTaskStatusWithValidation(taskId, TaskStatus.PENDING, username);

        // Assert
        assertNotNull(result);
        verify(taskRepository).save(testTask);
        assertEquals(TaskStatus.PENDING, testTask.getStatus());
    }

    @Test
    void testUserNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            taskService.changeTaskStatusWithValidation(taskId, TaskStatus.IN_PROGRESS, username);
        });
    }

    @Test
    void testTaskNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.changeTaskStatusWithValidation(taskId, TaskStatus.IN_PROGRESS, username);
        });
    }
}
