package com.akertesz.task_manager_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.akertesz.task_manager_api.dto.CreateTaskRequest;
import com.akertesz.task_manager_api.dto.TaskDto;
import com.akertesz.task_manager_api.dto.UpdateTaskRequest;
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
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        username = "testuser";
        taskId = UUID.randomUUID();
        now = LocalDateTime.now();
        
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername(username);
        testUser.setEmail("test@example.com");
        
        testTask = new Task();
        testTask.setId(taskId);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(TaskStatus.PENDING);
        testTask.setPriority(TaskPriority.MEDIUM);
        testTask.setUser(testUser);
        testTask.setCreatedAt(now);
        testTask.setUpdatedAt(now);
        testTask.setDueDate(now.plusDays(7));
    }

    // Create Task Tests
    @Test
    void testCreateTask_Success() {
        // Arrange
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("New Task");
        request.setDescription("New Description");
        request.setPriority(TaskPriority.HIGH);
        request.setDueDate(now.plusDays(5));
        
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        TaskDto result = taskService.createTask(request, username);

        // Assert
        assertNotNull(result);
        verify(taskRepository).save(any(Task.class));
        verify(userRepository).findByUsername(username);
    }

    @Test
    void testCreateTask_UserNotFound() {
        // Arrange
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("New Task");
        
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            taskService.createTask(request, username);
        });
        
        verify(userRepository).findByUsername(username);
        verify(taskRepository, never()).save(any(Task.class));
    }

    // Get Task By ID Tests
    @Test
    void testGetTaskById_Success() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.of(testTask));

        // Act
        TaskDto result = taskService.getTaskById(taskId, username);

        // Assert
        assertNotNull(result);
        assertEquals(testTask.getId().toString(), result.getId());
        assertEquals(testTask.getTitle(), result.getTitle());
        verify(userRepository).findByUsername(username);
        verify(taskRepository).findByIdAndUserAndIsDeletedFalse(taskId, testUser);
    }

    @Test
    void testGetTaskById_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            taskService.getTaskById(taskId, username);
        });
        
        verify(userRepository).findByUsername(username);
        verify(taskRepository, never()).findByIdAndUserAndIsDeletedFalse(any(), any());
    }

    @Test
    void testGetTaskById_TaskNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.getTaskById(taskId, username);
        });
        
        verify(userRepository).findByUsername(username);
        verify(taskRepository).findByIdAndUserAndIsDeletedFalse(taskId, testUser);
    }

    // Get All Tasks Tests
    @Test
    void testGetAllTasks_Success() {
        // Arrange
        List<Task> tasks = Arrays.asList(testTask);
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByUserAndIsDeletedFalse(testUser)).thenReturn(tasks);

        // Act
        List<TaskDto> result = taskService.getAllTasks(username);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask.getId().toString(), result.get(0).getId());
        verify(userRepository).findByUsername(username);
        verify(taskRepository).findByUserAndIsDeletedFalse(testUser);
    }

    @Test
    void testGetAllTasks_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            taskService.getAllTasks(username);
        });
        
        verify(userRepository).findByUsername(username);
        verify(taskRepository, never()).findByUserAndIsDeletedFalse(any());
    }

    // Update Task Tests
    @Test
    void testUpdateTask_Success() {
        // Arrange
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");
        request.setPriority(TaskPriority.HIGH);
        
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        TaskDto result = taskService.updateTask(taskId, request, username);

        // Assert
        assertNotNull(result);
        verify(taskRepository).save(testTask);
        verify(userRepository).findByUsername(username);
        verify(taskRepository).findByIdAndUserAndIsDeletedFalse(taskId, testUser);
    }

    @Test
    void testUpdateTask_UserNotFound() {
        // Arrange
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle("Updated Title");
        
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            taskService.updateTask(taskId, request, username);
        });
        
        verify(userRepository).findByUsername(username);
        verify(taskRepository, never()).findByIdAndUserAndIsDeletedFalse(any(), any());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testUpdateTask_TaskNotFound() {
        // Arrange
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle("Updated Title");
        
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.updateTask(taskId, request, username);
        });
        
        verify(userRepository).findByUsername(username);
        verify(taskRepository).findByIdAndUserAndIsDeletedFalse(taskId, testUser);
        verify(taskRepository, never()).save(any(Task.class));
    }

    // Delete Task Tests
    @Test
    void testDeleteTask_Success() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.of(testTask));
        doNothing().when(taskRepository).deleteTask(taskId, testUser);

        // Act
        boolean result = taskService.deleteTask(taskId, username);

        // Assert
        assertTrue(result);
        verify(taskRepository).deleteTask(taskId, testUser);
        verify(userRepository).findByUsername(username);
        verify(taskRepository).findByIdAndUserAndIsDeletedFalse(taskId, testUser);
    }

    @Test
    void testDeleteTask_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            taskService.deleteTask(taskId, username);
        });
        
        verify(userRepository).findByUsername(username);
        verify(taskRepository, never()).findByIdAndUserAndIsDeletedFalse(any(), any());
        verify(taskRepository, never()).deleteTask(any(), any());
    }

    @Test
    void testDeleteTask_TaskNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.deleteTask(taskId, username);
        });
        
        verify(userRepository).findByUsername(username);
        verify(taskRepository).findByIdAndUserAndIsDeletedFalse(taskId, testUser);
        verify(taskRepository, never()).deleteTask(any(), any());
    }

    // Get Tasks By Status Tests
    @Test
    void testGetTasksByStatus_Success() {
        // Arrange
        List<Task> tasks = Arrays.asList(testTask);
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByStatusAndUserAndIsDeletedFalse(TaskStatus.PENDING, testUser))
                .thenReturn(tasks);

        // Act
        List<TaskDto> result = taskService.getTasksByStatus(TaskStatus.PENDING, username);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findByUsername(username);
        verify(taskRepository).findByStatusAndUserAndIsDeletedFalse(TaskStatus.PENDING, testUser);
    }

    @Test
    void testGetTasksByStatus_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            taskService.getTasksByStatus(TaskStatus.PENDING, username);
        });
        
        verify(userRepository).findByUsername(username);
        verify(taskRepository, never()).findByStatusAndUserAndIsDeletedFalse(any(), any());
    }

    // Get Tasks By Priority Tests
    @Test
    void testGetTasksByPriority_Success() {
        // Arrange
        List<Task> tasks = Arrays.asList(testTask);
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByPriorityAndUserAndIsDeletedFalse(TaskPriority.MEDIUM, testUser))
                .thenReturn(tasks);

        // Act
        List<TaskDto> result = taskService.getTasksByPriority(TaskPriority.MEDIUM, username);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findByUsername(username);
        verify(taskRepository).findByPriorityAndUserAndIsDeletedFalse(TaskPriority.MEDIUM, testUser);
    }

    @Test
    void testGetTasksByPriority_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            taskService.getTasksByPriority(TaskPriority.MEDIUM, username);
        });
        
        verify(userRepository).findByUsername(username);
        verify(taskRepository, never()).findByPriorityAndUserAndIsDeletedFalse(any(), any());
    }

    // Get Overdue Tasks Tests
    @Test
    void testGetOverdueTasks_Success() {
        // Arrange
        List<Task> tasks = Arrays.asList(testTask);
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByDueDateBeforeAndUserAndIsDeletedFalse(any(LocalDateTime.class), eq(testUser)))
                .thenReturn(tasks);

        // Act
        List<TaskDto> result = taskService.getOverdueTasks(username);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findByUsername(username);
        verify(taskRepository).findByDueDateBeforeAndUserAndIsDeletedFalse(any(LocalDateTime.class), eq(testUser));
    }

    @Test
    void testGetOverdueTasks_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            taskService.getOverdueTasks(username);
        });
        
        verify(userRepository).findByUsername(username);
        verify(taskRepository, never()).findByDueDateBeforeAndUserAndIsDeletedFalse(any(), any());
    }

    // Search Tasks By Title Tests
    @Test
    void testSearchTasksByTitle_Success() {
        // Arrange
        List<Task> tasks = Arrays.asList(testTask);
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByTitleContainingIgnoreCaseAndUserAndIsDeletedFalse("test", testUser))
                .thenReturn(tasks);

        // Act
        List<TaskDto> result = taskService.searchTasksByTitle("test", username);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findByUsername(username);
        verify(taskRepository).findByTitleContainingIgnoreCaseAndUserAndIsDeletedFalse("test", testUser);
    }

    @Test
    void testSearchTasksByTitle_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            taskService.searchTasksByTitle("test", username);
        });
        
        verify(userRepository).findByUsername(username);
        verify(taskRepository, never()).findByTitleContainingIgnoreCaseAndUserAndIsDeletedFalse(any(), any());
    }

    // Get Tasks Created Between Tests
    @Test
    void testGetTasksCreatedBetween_Success() {
        // Arrange
        LocalDateTime startDate = now.minusDays(7);
        LocalDateTime endDate = now.plusDays(7);
        List<Task> tasks = Arrays.asList(testTask);
        
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByCreatedAtBetweenAndUserAndIsDeletedFalse(startDate, endDate, testUser))
                .thenReturn(tasks);

        // Act
        List<TaskDto> result = taskService.getTasksCreatedBetween(startDate, endDate, username);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findByUsername(username);
        verify(taskRepository).findByCreatedAtBetweenAndUserAndIsDeletedFalse(startDate, endDate, testUser);
    }

    @Test
    void testGetTasksCreatedBetween_UserNotFound() {
        // Arrange
        LocalDateTime startDate = now.minusDays(7);
        LocalDateTime endDate = now.plusDays(7);
        
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            taskService.getTasksCreatedBetween(startDate, endDate, username);
        });
        
        verify(userRepository).findByUsername(username);
        verify(taskRepository, never()).findByCreatedAtBetweenAndUserAndIsDeletedFalse(any(), any(), any());
    }

    // Get Tasks Ordered By Priority And Due Date Tests
    @Test
    void testGetTasksOrderedByPriorityAndDueDate_Success() {
        // Arrange
        List<Task> tasks = Arrays.asList(testTask);
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findAllOrderByPriorityAndDueDateAndUserAndIsDeletedFalse(testUser))
                .thenReturn(tasks);

        // Act
        List<TaskDto> result = taskService.getTasksOrderedByPriorityAndDueDate(username);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findByUsername(username);
        verify(taskRepository).findAllOrderByPriorityAndDueDateAndUserAndIsDeletedFalse(testUser);
    }

    @Test
    void testGetTasksOrderedByPriorityAndDueDate_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            taskService.getTasksOrderedByPriorityAndDueDate(username);
        });
        
        verify(userRepository).findByUsername(username);
        verify(taskRepository, never()).findAllOrderByPriorityAndDueDateAndUserAndIsDeletedFalse(any());
    }

    // Change Task Status Tests
    @Test
    void testChangeTaskStatus_Success() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        TaskDto result = taskService.changeTaskStatus(taskId, TaskStatus.IN_PROGRESS, username);

        // Assert
        assertNotNull(result);
        assertEquals(TaskStatus.IN_PROGRESS, testTask.getStatus());
        verify(taskRepository).save(testTask);
        verify(userRepository).findByUsername(username);
        verify(taskRepository).findByIdAndUserAndIsDeletedFalse(taskId, testUser);
    }

    @Test
    void testChangeTaskStatus_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            taskService.changeTaskStatus(taskId, TaskStatus.IN_PROGRESS, username);
        });
        
        verify(userRepository).findByUsername(username);
        verify(taskRepository, never()).findByIdAndUserAndIsDeletedFalse(any(), any());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testChangeTaskStatus_TaskNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.changeTaskStatus(taskId, TaskStatus.IN_PROGRESS, username);
        });
        
        verify(userRepository).findByUsername(username);
        verify(taskRepository).findByIdAndUserAndIsDeletedFalse(taskId, testUser);
        verify(taskRepository, never()).save(any(Task.class));
    }

    // Change Task Status With Validation Tests
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
    void testValidStatusTransition_PendingToCancelled() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        TaskDto result = taskService.changeTaskStatusWithValidation(taskId, TaskStatus.CANCELLED, username);

        // Assert
        assertNotNull(result);
        verify(taskRepository).save(testTask);
        assertEquals(TaskStatus.CANCELLED, testTask.getStatus());
    }

    @Test
    void testValidStatusTransition_InProgressToCompleted() {
        // Arrange
        testTask.setStatus(TaskStatus.IN_PROGRESS);
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
    void testValidStatusTransition_InProgressToCancelled() {
        // Arrange
        testTask.setStatus(TaskStatus.IN_PROGRESS);
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        TaskDto result = taskService.changeTaskStatusWithValidation(taskId, TaskStatus.CANCELLED, username);

        // Assert
        assertNotNull(result);
        verify(taskRepository).save(testTask);
        assertEquals(TaskStatus.CANCELLED, testTask.getStatus());
    }

    @Test
    void testValidStatusTransition_InProgressToPending() {
        // Arrange
        testTask.setStatus(TaskStatus.IN_PROGRESS);
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
    void testValidStatusTransition_CancelledToInProgress() {
        // Arrange
        testTask.setStatus(TaskStatus.CANCELLED);
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
    void testInvalidStatusTransition_CompletedToPending() {
        // Arrange
        testTask.setStatus(TaskStatus.COMPLETED);
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.of(testTask));

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            taskService.changeTaskStatusWithValidation(taskId, TaskStatus.PENDING, username);
        });

        assertTrue(exception.getMessage().contains("Invalid status transition from COMPLETED to PENDING"));
        assertTrue(exception.getMessage().contains("No transitions allowed (final state)"));
    }

    @Test
    void testInvalidStatusTransition_CompletedToCancelled() {
        // Arrange
        testTask.setStatus(TaskStatus.COMPLETED);
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.of(testTask));

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            taskService.changeTaskStatusWithValidation(taskId, TaskStatus.CANCELLED, username);
        });

        assertTrue(exception.getMessage().contains("Invalid status transition from COMPLETED to CANCELLED"));
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
    void testInvalidStatusTransition_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            taskService.changeTaskStatusWithValidation(taskId, TaskStatus.IN_PROGRESS, username);
        });
    }

    @Test
    void testInvalidStatusTransition_TaskNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.changeTaskStatusWithValidation(taskId, TaskStatus.IN_PROGRESS, username);
        });
    }

    // Change Task Priority Tests
    @Test
    void testChangeTaskPriority_Success() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        TaskDto result = taskService.changeTaskPriority(taskId, TaskPriority.HIGH, username);

        // Assert
        assertNotNull(result);
        assertEquals(TaskPriority.HIGH, testTask.getPriority());
        verify(taskRepository).save(testTask);
        verify(userRepository).findByUsername(username);
        verify(taskRepository).findByIdAndUserAndIsDeletedFalse(taskId, testUser);
    }

    @Test
    void testChangeTaskPriority_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            taskService.changeTaskPriority(taskId, TaskPriority.HIGH, username);
        });
        
        verify(userRepository).findByUsername(username);
        verify(taskRepository, never()).findByIdAndUserAndIsDeletedFalse(any(), any());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testChangeTaskPriority_TaskNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.findByIdAndUserAndIsDeletedFalse(taskId, testUser))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.changeTaskPriority(taskId, TaskPriority.HIGH, username);
        });
        
        verify(userRepository).findByUsername(username);
        verify(taskRepository).findByIdAndUserAndIsDeletedFalse(taskId, testUser);
        verify(taskRepository, never()).save(any(Task.class));
    }

    // Get Task Statistics Tests
    @Test
    void testGetTaskStatistics_Success() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(testUser);
        when(taskRepository.countByUserAndIsDeletedFalse(testUser)).thenReturn(10L);
        when(taskRepository.countByStatusAndUserAndIsDeletedFalse(TaskStatus.PENDING, testUser)).thenReturn(3L);
        when(taskRepository.countByStatusAndUserAndIsDeletedFalse(TaskStatus.IN_PROGRESS, testUser)).thenReturn(2L);
        when(taskRepository.countByStatusAndUserAndIsDeletedFalse(TaskStatus.COMPLETED, testUser)).thenReturn(4L);
        when(taskRepository.countByStatusAndUserAndIsDeletedFalse(TaskStatus.CANCELLED, testUser)).thenReturn(1L);
        when(taskRepository.findByDueDateBeforeAndUserAndIsDeletedFalse(any(LocalDateTime.class), eq(testUser)))
                .thenReturn(Arrays.asList(testTask));
        when(taskRepository.findByPriorityAndUserAndIsDeletedFalse(TaskPriority.URGENT, testUser))
                .thenReturn(Arrays.asList());
        when(taskRepository.findByPriorityAndUserAndIsDeletedFalse(TaskPriority.HIGH, testUser))
                .thenReturn(Arrays.asList(testTask));
        when(taskRepository.findByPriorityAndUserAndIsDeletedFalse(TaskPriority.MEDIUM, testUser))
                .thenReturn(Arrays.asList(testTask));
        when(taskRepository.findByPriorityAndUserAndIsDeletedFalse(TaskPriority.LOW, testUser))
                .thenReturn(Arrays.asList());

        // Act
        var result = taskService.getTaskStatistics(username);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getTotalTasks());
        assertEquals(4L, result.getCompletedTasks());
        assertEquals(3L, result.getPendingTasks());
        assertEquals(2L, result.getInProgressTasks());
        assertEquals(1L, result.getCancelledTasks());
        assertEquals(1L, result.getOverdueTasks());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void testGetTaskStatistics_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            taskService.getTaskStatistics(username);
        });
        
        verify(userRepository).findByUsername(username);
        verify(taskRepository, never()).countByUserAndIsDeletedFalse(any());
    }
}
