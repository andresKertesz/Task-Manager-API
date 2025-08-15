package com.akertesz.task_manager_api.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akertesz.task_manager_api.config.JwtUtil;
import com.akertesz.task_manager_api.dto.CreateTaskRequest;
import com.akertesz.task_manager_api.dto.TaskDto;
import com.akertesz.task_manager_api.dto.UpdateTaskRequest;
import com.akertesz.task_manager_api.model.TaskPriority;
import com.akertesz.task_manager_api.model.TaskStatus;
import com.akertesz.task_manager_api.service.TaskService;
import com.akertesz.task_manager_api.service.TaskStatistics;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    private final TaskService taskService;
    private final JwtUtil jwtUtil;
    @Autowired
    public TaskController(TaskService taskService, JwtUtil jwtUtil) {
        this.taskService = taskService;
        this.jwtUtil = jwtUtil;
    }
    
    // Create a new task
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody CreateTaskRequest request, @RequestHeader("Authorization") String token) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TaskDto createdTask = taskService.createTask(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
    
    // Get all tasks
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks(@RequestHeader("Authorization") String token) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TaskDto> tasks = taskService.getAllTasks(username);
        return ResponseEntity.ok(tasks);
    }
    
    // Get a task by ID
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable String id, @RequestHeader("Authorization") String token) {
        try {
            UUID uuid = UUID.fromString(id);
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            TaskDto task = taskService.getTaskById(uuid, username);
            return ResponseEntity.ok(task);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid task ID format: " + id);
        }
    }
    
    // Update a task
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable String id, 
                                            @Valid @RequestBody UpdateTaskRequest request, @RequestHeader("Authorization") String token) {
        try {
            UUID uuid = UUID.fromString(id);
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            TaskDto updatedTask = taskService.updateTask(uuid, request, username);
            return ResponseEntity.ok(updatedTask);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid task ID format: " + id);
        }
    }
    
    // Delete a task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id, @RequestHeader("Authorization") String token) {
        try {
            UUID uuid = UUID.fromString(id);
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            taskService.deleteTask(uuid, username);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid task ID format: " + id);
        }
    }
    
    // Get tasks by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDto>> getTasksByStatus(@PathVariable TaskStatus status, @RequestHeader("Authorization") String token) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TaskDto> tasks = taskService.getTasksByStatus(status, username);
        return ResponseEntity.ok(tasks);
    }
    
    // Get tasks by priority
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TaskDto>> getTasksByPriority(@PathVariable TaskPriority priority, @RequestHeader("Authorization") String token) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TaskDto> tasks = taskService.getTasksByPriority(priority, username);
        return ResponseEntity.ok(tasks);
    }
    
    // Get overdue tasks
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDto>> getOverdueTasks(@RequestHeader("Authorization") String token) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TaskDto> tasks = taskService.getOverdueTasks(username);
        return ResponseEntity.ok(tasks);
    }
    
    // Search tasks by title
    @GetMapping("/search")
    public ResponseEntity<List<TaskDto>> searchTasksByTitle(@RequestParam String title, @RequestHeader("Authorization") String token) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TaskDto> tasks = taskService.searchTasksByTitle(title, username);
        return ResponseEntity.ok(tasks);
    }
    
    // Get tasks created between dates
    @GetMapping("/created-between")
    public ResponseEntity<List<TaskDto>> getTasksCreatedBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestHeader("Authorization") String token) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TaskDto> tasks = taskService.getTasksCreatedBetween(startDate, endDate, username);
        return ResponseEntity.ok(tasks);
    }
    
    // Get tasks ordered by priority and due date
    @GetMapping("/ordered")
    public ResponseEntity<List<TaskDto>> getTasksOrderedByPriorityAndDueDate(@RequestHeader("Authorization") String token) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TaskDto> tasks = taskService.getTasksOrderedByPriorityAndDueDate(username);
        return ResponseEntity.ok(tasks);
    }
    
    // Change task status
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskDto> changeTaskStatus(@PathVariable String id, 
                                                  @RequestParam TaskStatus status, @RequestHeader("Authorization") String token) {
        try {
            UUID uuid = UUID.fromString(id);
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            TaskDto updatedTask = taskService.changeTaskStatus(uuid, status, username);
            return ResponseEntity.ok(updatedTask);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid task ID format: " + id);
        }
    }
    
    // Change task priority
    @PatchMapping("/{id}/priority")
    public ResponseEntity<TaskDto> changeTaskPriority(@PathVariable String id, 
                                                     @RequestParam TaskPriority priority, @RequestHeader("Authorization") String token ) {
        try {
            UUID uuid = UUID.fromString(id);
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            TaskDto updatedTask = taskService.changeTaskPriority(uuid, priority, username);
            return ResponseEntity.ok(updatedTask);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid task ID format: " + id);
        }
    }
    
    // Get task statistics
    @GetMapping("/statistics")
    public ResponseEntity<TaskStatistics> getTaskStatistics(@RequestHeader("Authorization") String token) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TaskStatistics statistics = taskService.getTaskStatistics(username);
        return ResponseEntity.ok(statistics);
    }
}
