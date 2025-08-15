package com.akertesz.task_manager_api.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    
    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    
    // Create a new task
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody CreateTaskRequest request) {
        TaskDto createdTask = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
    
    // Get all tasks
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<TaskDto> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }
    
    // Get a task by ID
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable String id) {
        UUID uuid = UUID.fromString(id);
        return taskService.getTaskById(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Update a task
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable String id, 
                                            @Valid @RequestBody UpdateTaskRequest request) {
        UUID uuid = UUID.fromString(id);
        return taskService.updateTask(uuid, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Delete a task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        UUID uuid = UUID.fromString(id);
        if (taskService.deleteTask(uuid)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    // Get tasks by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDto>> getTasksByStatus(@PathVariable TaskStatus status) {
        List<TaskDto> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }
    
    // Get tasks by priority
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TaskDto>> getTasksByPriority(@PathVariable TaskPriority priority) {
        List<TaskDto> tasks = taskService.getTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }
    
    // Get overdue tasks
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDto>> getOverdueTasks() {
        List<TaskDto> tasks = taskService.getOverdueTasks();
        return ResponseEntity.ok(tasks);
    }
    
    // Search tasks by title
    @GetMapping("/search")
    public ResponseEntity<List<TaskDto>> searchTasksByTitle(@RequestParam String title) {
        List<TaskDto> tasks = taskService.searchTasksByTitle(title);
        return ResponseEntity.ok(tasks);
    }
    
    // Get tasks created between dates
    @GetMapping("/created-between")
    public ResponseEntity<List<TaskDto>> getTasksCreatedBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<TaskDto> tasks = taskService.getTasksCreatedBetween(startDate, endDate);
        return ResponseEntity.ok(tasks);
    }
    
    // Get tasks ordered by priority and due date
    @GetMapping("/ordered")
    public ResponseEntity<List<TaskDto>> getTasksOrderedByPriorityAndDueDate() {
        List<TaskDto> tasks = taskService.getTasksOrderedByPriorityAndDueDate();
        return ResponseEntity.ok(tasks);
    }
    
    // Change task status
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskDto> changeTaskStatus(@PathVariable String id, 
                                                  @RequestParam TaskStatus status) {
        UUID uuid = UUID.fromString(id);
        return taskService.changeTaskStatus(uuid, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Change task priority
    @PatchMapping("/{id}/priority")
    public ResponseEntity<TaskDto> changeTaskPriority(@PathVariable String id, 
                                                     @RequestParam TaskPriority priority) {
        UUID uuid = UUID.fromString(id);
        return taskService.changeTaskPriority(uuid, priority)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Get task statistics
    @GetMapping("/statistics")
    public ResponseEntity<TaskStatistics> getTaskStatistics() {
        TaskStatistics statistics = taskService.getTaskStatistics();
        return ResponseEntity.ok(statistics);
    }
}
