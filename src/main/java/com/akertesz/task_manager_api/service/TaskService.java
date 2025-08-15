package com.akertesz.task_manager_api.service;

import com.akertesz.task_manager_api.dto.CreateTaskRequest;
import com.akertesz.task_manager_api.dto.TaskDto;
import com.akertesz.task_manager_api.dto.UpdateTaskRequest;
import com.akertesz.task_manager_api.model.TaskPriority;
import com.akertesz.task_manager_api.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskService {
    
    // Create a new task
    TaskDto createTask(CreateTaskRequest request);
    
    // Get a task by ID
    Optional<TaskDto> getTaskById(Long id);
    
    // Get all tasks
    List<TaskDto> getAllTasks();
    
    // Update an existing task
    Optional<TaskDto> updateTask(Long id, UpdateTaskRequest request);
    
    // Delete a task
    boolean deleteTask(Long id);
    
    // Get tasks by status
    List<TaskDto> getTasksByStatus(TaskStatus status);
    
    // Get tasks by priority
    List<TaskDto> getTasksByPriority(TaskPriority priority);
    
    // Get overdue tasks
    List<TaskDto> getOverdueTasks();
    
    // Search tasks by title
    List<TaskDto> searchTasksByTitle(String title);
    
    // Get tasks created between dates
    List<TaskDto> getTasksCreatedBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Get tasks ordered by priority and due date
    List<TaskDto> getTasksOrderedByPriorityAndDueDate();
    
    // Change task status
    Optional<TaskDto> changeTaskStatus(Long id, TaskStatus status);
    
    // Change task priority
    Optional<TaskDto> changeTaskPriority(Long id, TaskPriority priority);
    
    // Get task statistics
    TaskStatistics getTaskStatistics();
}
