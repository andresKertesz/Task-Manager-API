package com.akertesz.task_manager_api.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.akertesz.task_manager_api.config.JwtUtil;
import com.akertesz.task_manager_api.dto.CreateTaskRequest;
import com.akertesz.task_manager_api.dto.TaskDto;
import com.akertesz.task_manager_api.dto.UpdateTaskRequest;
import com.akertesz.task_manager_api.exception.GlobalExceptionHandler;
import com.akertesz.task_manager_api.model.TaskPriority;
import com.akertesz.task_manager_api.model.TaskStatus;
import com.akertesz.task_manager_api.service.TaskService;
import com.akertesz.task_manager_api.service.TaskStatistics;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private TaskController taskController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private TaskDto testTaskDto;
    private CreateTaskRequest createTaskRequest;
    private UpdateTaskRequest updateTaskRequest;
    private UUID taskId;
    private String username;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        
        objectMapper = new ObjectMapper();
        // Configure ObjectMapper to handle LocalDateTime
        objectMapper.findAndRegisterModules();
        
        username = "testuser";
        taskId = UUID.randomUUID();
        now = LocalDateTime.now();
        
        testTaskDto = new TaskDto();
        testTaskDto.setId(taskId.toString());
        testTaskDto.setTitle("Test Task");
        testTaskDto.setDescription("Test Description");
        testTaskDto.setStatus(TaskStatus.PENDING);
        testTaskDto.setPriority(TaskPriority.MEDIUM);
        testTaskDto.setCreatedAt(now);
        testTaskDto.setUpdatedAt(now);
        testTaskDto.setDueDate(now.plusDays(7));
        
        createTaskRequest = new CreateTaskRequest();
        createTaskRequest.setTitle("New Task");
        createTaskRequest.setDescription("New Description");
        createTaskRequest.setPriority(TaskPriority.HIGH);
        createTaskRequest.setDueDate(now.plusDays(5));
        
        updateTaskRequest = new UpdateTaskRequest();
        updateTaskRequest.setTitle("Updated Task");
        updateTaskRequest.setDescription("Updated Description");
        updateTaskRequest.setPriority(TaskPriority.LOW);
        
        // Setup security context - only when needed
        // when(securityContext.getAuthentication()).thenReturn(authentication);
        // when(authentication.getName()).thenReturn(username);
        // SecurityContextHolder.setContext(securityContext);
    }

    // Create Task Tests
    @Test
    void testCreateTask_Success() throws Exception {
        // Arrange
        when(taskService.createTask(any(CreateTaskRequest.class), eq(username)))
                .thenReturn(testTaskDto);

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTaskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"));

        verify(taskService).createTask(any(CreateTaskRequest.class), eq(username));
    }

    @Test
    void testCreateTask_InvalidRequest() throws Exception {
        // Arrange
        CreateTaskRequest invalidRequest = new CreateTaskRequest();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // Get All Tasks Tests
    @Test
    void testGetAllTasks_Success() throws Exception {
        // Arrange
        List<TaskDto> tasks = Arrays.asList(testTaskDto);
        when(taskService.getAllTasks(username)).thenReturn(tasks);

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(taskId.toString()))
                .andExpect(jsonPath("$[0].title").value("Test Task"));

        verify(taskService).getAllTasks(username);
    }

    // Get Task By ID Tests
    @Test
    void testGetTaskById_Success() throws Exception {
        // Arrange
        when(taskService.getTaskById(eq(taskId), eq(username)))
                .thenReturn(testTaskDto);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/{id}", taskId.toString())
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.title").value("Test Task"));

        verify(taskService).getTaskById(taskId, username);
    }

    @Test
    void testGetTaskById_InvalidIdFormat() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/tasks/{id}", "invalid-uuid")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest());
    }

    // Update Task Tests
    @Test
    void testUpdateTask_Success() throws Exception {
        // Arrange
        when(taskService.updateTask(eq(taskId), any(UpdateTaskRequest.class), eq(username)))
                .thenReturn(testTaskDto);

        // Act & Assert
        mockMvc.perform(put("/api/tasks/{id}", taskId.toString())
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTaskRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()));

        verify(taskService).updateTask(eq(taskId), any(UpdateTaskRequest.class), eq(username));
    }

    @Test
    void testUpdateTask_InvalidIdFormat() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/tasks/{id}", "invalid-uuid")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTaskRequest)))
                .andExpect(status().isBadRequest());
    }

    // Delete Task Tests
    @Test
    void testDeleteTask_Success() throws Exception {
        // Arrange
        when(taskService.deleteTask(eq(taskId), eq(username))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/tasks/{id}", taskId.toString())
                .header("Authorization", "Bearer token"))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(taskId, username);
    }

    @Test
    void testDeleteTask_InvalidIdFormat() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/tasks/{id}", "invalid-uuid")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest());
    }

    // Get Tasks By Status Tests
    @Test
    void testGetTasksByStatus_Success() throws Exception {
        // Arrange
        List<TaskDto> tasks = Arrays.asList(testTaskDto);
        when(taskService.getTasksByStatus(eq(TaskStatus.PENDING), eq(username)))
                .thenReturn(tasks);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/status/{status}", "PENDING")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(taskId.toString()));

        verify(taskService).getTasksByStatus(TaskStatus.PENDING, username);
    }

    // Get Tasks By Priority Tests
    @Test
    void testGetTasksByPriority_Success() throws Exception {
        // Arrange
        List<TaskDto> tasks = Arrays.asList(testTaskDto);
        when(taskService.getTasksByPriority(eq(TaskPriority.MEDIUM), eq(username)))
                .thenReturn(tasks);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/priority/{priority}", "MEDIUM")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(taskId.toString()));

        verify(taskService).getTasksByPriority(TaskPriority.MEDIUM, username);
    }

    // Get Overdue Tasks Tests
    @Test
    void testGetOverdueTasks_Success() throws Exception {
        // Arrange
        List<TaskDto> tasks = Arrays.asList(testTaskDto);
        when(taskService.getOverdueTasks(username)).thenReturn(tasks);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/overdue")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(taskId.toString()));

        verify(taskService).getOverdueTasks(username);
    }

    // Search Tasks By Title Tests
    @Test
    void testSearchTasksByTitle_Success() throws Exception {
        // Arrange
        List<TaskDto> tasks = Arrays.asList(testTaskDto);
        when(taskService.searchTasksByTitle(eq("test"), eq(username)))
                .thenReturn(tasks);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/search")
                .param("title", "test")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(taskId.toString()));

        verify(taskService).searchTasksByTitle("test", username);
    }

    // Get Tasks Created Between Tests
    @Test
    void testGetTasksCreatedBetween_Success() throws Exception {
        // Arrange
        LocalDateTime startDate = now.minusDays(7);
        LocalDateTime endDate = now.plusDays(7);
        List<TaskDto> tasks = Arrays.asList(testTaskDto);
        
        when(taskService.getTasksCreatedBetween(eq(startDate), eq(endDate), eq(username)))
                .thenReturn(tasks);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String startDateStr = startDate.format(formatter);
        String endDateStr = endDate.format(formatter);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/created-between")
                .param("startDate", startDateStr)
                .param("endDate", endDateStr)
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(taskId.toString()));

        verify(taskService).getTasksCreatedBetween(startDate, endDate, username);
    }

    // Get Tasks Ordered By Priority And Due Date Tests
    @Test
    void testGetTasksOrderedByPriorityAndDueDate_Success() throws Exception {
        // Arrange
        List<TaskDto> tasks = Arrays.asList(testTaskDto);
        when(taskService.getTasksOrderedByPriorityAndDueDate(username))
                .thenReturn(tasks);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/ordered")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(taskId.toString()));

        verify(taskService).getTasksOrderedByPriorityAndDueDate(username);
    }

    // Change Task Status Tests
    @Test
    void testChangeTaskStatus_Success() throws Exception {
        // Arrange
        when(taskService.changeTaskStatus(eq(taskId), eq(TaskStatus.IN_PROGRESS), eq(username)))
                .thenReturn(testTaskDto);

        // Act & Assert
        mockMvc.perform(patch("/api/tasks/{id}/status", taskId.toString())
                .param("status", "IN_PROGRESS")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()));

        verify(taskService).changeTaskStatus(taskId, TaskStatus.IN_PROGRESS, username);
    }

    @Test
    void testChangeTaskStatus_InvalidIdFormat() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/api/tasks/{id}/status", "invalid-uuid")
                .param("status", "IN_PROGRESS")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest());
    }

    // Change Task Status With Validation Tests
    @Test
    void testChangeTaskStatusWithValidation_Success() throws Exception {
        // Arrange
        when(taskService.changeTaskStatusWithValidation(eq(taskId), eq(TaskStatus.IN_PROGRESS), eq(username)))
                .thenReturn(testTaskDto);

        // Act & Assert
        mockMvc.perform(patch("/api/tasks/{id}/status-with-validation", taskId.toString())
                .param("status", "IN_PROGRESS")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()));

        verify(taskService).changeTaskStatusWithValidation(taskId, TaskStatus.IN_PROGRESS, username);
    }

    @Test
    void testChangeTaskStatusWithValidation_InvalidIdFormat() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/api/tasks/{id}/status-with-validation", "invalid-uuid")
                .param("status", "IN_PROGRESS")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest());
    }

    // Change Task Priority Tests
    @Test
    void testChangeTaskPriority_Success() throws Exception {
        // Arrange
        when(taskService.changeTaskPriority(eq(taskId), eq(TaskPriority.HIGH), eq(username)))
                .thenReturn(testTaskDto);

        // Act & Assert
        mockMvc.perform(patch("/api/tasks/{id}/priority", taskId.toString())
                .param("priority", "HIGH")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()));

        verify(taskService).changeTaskPriority(taskId, TaskPriority.HIGH, username);
    }

    @Test
    void testChangeTaskPriority_InvalidIdFormat() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/api/tasks/{id}/priority", "invalid-uuid")
                .param("priority", "HIGH")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest());
    }

    // Get Task Statistics Tests
    @Test
    void testGetTaskStatistics_Success() throws Exception {
        // Arrange
        TaskStatistics statistics = new TaskStatistics(
            10L, 4L, 3L, 2L, 1L, 1L, 
            java.util.Map.of(), java.util.Map.of()
        );
        when(taskService.getTaskStatistics(username)).thenReturn(statistics);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/statistics")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTasks").value(10))
                .andExpect(jsonPath("$.completedTasks").value(4))
                .andExpect(jsonPath("$.pendingTasks").value(3));

        verify(taskService).getTaskStatistics(username);
    }

    // Error Handling Tests
    @Test
    void testCreateTask_ServiceException() throws Exception {
        // Arrange
        when(taskService.createTask(any(CreateTaskRequest.class), eq(username)))
                .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTaskRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetTaskById_ServiceException() throws Exception {
        // Arrange
        when(taskService.getTaskById(eq(taskId), eq(username)))
                .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        mockMvc.perform(get("/api/tasks/{id}", taskId.toString())
                .header("Authorization", "Bearer token"))
                .andExpect(status().isInternalServerError());
    }

    // Validation Tests
    @Test
    void testCreateTask_MissingTitle() throws Exception {
        // Arrange
        CreateTaskRequest missingTitleRequest = new CreateTaskRequest();
        missingTitleRequest.setDescription("Test Description");
        missingTitleRequest.setPriority(TaskPriority.HIGH);
        // Missing title - should fail validation

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(missingTitleRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateTask_MissingPriority() throws Exception {
        // Arrange
        CreateTaskRequest missingPriorityRequest = new CreateTaskRequest();
        missingPriorityRequest.setTitle("Test Task");
        missingPriorityRequest.setDescription("Test Description");
        // Missing priority - should fail validation

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(missingPriorityRequest)))
                .andExpect(status().isBadRequest());
    }

    // Security Tests
    @Test
    void testEndpoints_RequireAuthorization() throws Exception {
        // Setup security context for this test
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);
        
        // Test without Authorization header
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }

    // Edge Cases
    @Test
    void testGetTasksByStatus_InvalidStatus() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/tasks/status/{status}", "INVALID_STATUS")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetTasksByPriority_InvalidPriority() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/tasks/priority/{priority}", "INVALID_PRIORITY")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSearchTasksByTitle_EmptyTitle() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/tasks/search")
                .param("title", "")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchTasksByTitle_MissingTitle() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/tasks/search")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest());
    }
}
