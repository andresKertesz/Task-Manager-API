package com.akertesz.task_manager_api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.akertesz.task_manager_api.dto.CreateTaskRequest;
import com.akertesz.task_manager_api.dto.TaskDto;
import com.akertesz.task_manager_api.dto.UpdateTaskRequest;
import com.akertesz.task_manager_api.model.TaskPriority;
import com.akertesz.task_manager_api.model.TaskStatus;

public interface TaskService {
    
    // Create a new task
    TaskDto createTask(CreateTaskRequest request, String username);
    
    // Get a task by ID
    TaskDto getTaskById(UUID id, String username);
    
    // Get all tasks
    List<TaskDto> getAllTasks(String username);
    
    // Update an existing task
    TaskDto updateTask(UUID id, UpdateTaskRequest request, String username);
    
    // Delete a task
    boolean deleteTask(UUID id, String username);
    
    // Get tasks by status
    List<TaskDto> getTasksByStatus(TaskStatus status, String username);
    
    // Get tasks by priority
    List<TaskDto> getTasksByPriority(TaskPriority priority, String username);
    
    // Get overdue tasks
    List<TaskDto> getOverdueTasks(String username);
    
    // Search tasks by title
    List<TaskDto> searchTasksByTitle(String title, String username);
    
    // Get tasks created between dates
    List<TaskDto> getTasksCreatedBetween(LocalDateTime startDate, LocalDateTime endDate, String username);
    
    // Get tasks ordered by priority and due date
    List<TaskDto> getTasksOrderedByPriorityAndDueDate(String username);
    
    // Change task status
    TaskDto changeTaskStatus(UUID id, TaskStatus status, String username);
    
    // Change task status with validation (logical state transitions)
    TaskDto changeTaskStatusWithValidation(UUID id, TaskStatus status, String username);
    
    // Change task priority
    TaskDto changeTaskPriority(UUID id, TaskPriority priority, String username);
    
    // Get task statistics
    TaskStatistics getTaskStatistics(String username);
}
