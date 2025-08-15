package com.akertesz.task_manager_api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akertesz.task_manager_api.dto.CreateTaskRequest;
import com.akertesz.task_manager_api.dto.TaskDto;
import com.akertesz.task_manager_api.dto.UpdateTaskRequest;
import com.akertesz.task_manager_api.model.Task;
import com.akertesz.task_manager_api.model.TaskPriority;
import com.akertesz.task_manager_api.model.TaskStatus;
import com.akertesz.task_manager_api.repository.TaskRepository;

@Service
public class TaskServiceImpl implements TaskService {
    
    private final TaskRepository taskRepository;
    
    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    @Override
    public TaskDto createTask(CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setStatus(TaskStatus.PENDING);
        
        Task savedTask = taskRepository.save(task);
        return convertToDto(savedTask);
    }
    
    @Override
    public Optional<TaskDto> getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(this::convertToDto);
    }
    
    @Override
    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<TaskDto> updateTask(Long id, UpdateTaskRequest request) {
        return taskRepository.findById(id)
                .map(task -> {
                    if (request.getTitle() != null) {
                        task.setTitle(request.getTitle());
                    }
                    if (request.getDescription() != null) {
                        task.setDescription(request.getDescription());
                    }
                    if (request.getPriority() != null) {
                        task.setPriority(request.getPriority());
                    }
                    if (request.getDueDate() != null) {
                        task.setDueDate(request.getDueDate());
                    }
                    if (request.getStatus() != null) {
                        task.setStatus(request.getStatus());
                    }
                    
                    Task updatedTask = taskRepository.save(task);
                    return convertToDto(updatedTask);
                });
    }
    
    @Override
    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    @Override
    public List<TaskDto> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskDto> getTasksByPriority(TaskPriority priority) {
        return taskRepository.findByPriority(priority).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskDto> getOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDateTime.now()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskDto> searchTasksByTitle(String title) {
        return taskRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskDto> getTasksCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return taskRepository.findByCreatedAtBetween(startDate, endDate).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskDto> getTasksOrderedByPriorityAndDueDate() {
        return taskRepository.findAllOrderByPriorityAndDueDate().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<TaskDto> changeTaskStatus(Long id, TaskStatus status) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setStatus(status);
                    Task updatedTask = taskRepository.save(task);
                    return convertToDto(updatedTask);
                });
    }
    
    @Override
    public Optional<TaskDto> changeTaskPriority(Long id, TaskPriority priority) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setPriority(priority);
                    Task updatedTask = taskRepository.save(task);
                    return convertToDto(updatedTask);
                });
    }
    
    @Override
    public TaskStatistics getTaskStatistics() {
        long totalTasks = taskRepository.count();
        long pendingTasks = taskRepository.countByStatus(TaskStatus.PENDING);
        long inProgressTasks = taskRepository.countByStatus(TaskStatus.IN_PROGRESS);
        long completedTasks = taskRepository.countByStatus(TaskStatus.COMPLETED);
        long cancelledTasks = taskRepository.countByStatus(TaskStatus.CANCELLED);
        long overdueTasks = taskRepository.findOverdueTasks(LocalDateTime.now()).size();
        
        // Create maps for tasks by status and priority
        Map<TaskStatus, Long> tasksByStatus = Map.of(
            TaskStatus.PENDING, pendingTasks,
            TaskStatus.IN_PROGRESS, inProgressTasks,
            TaskStatus.COMPLETED, completedTasks,
            TaskStatus.CANCELLED, cancelledTasks
        );
        
        Map<TaskPriority, Long> tasksByPriority = Map.of(
            TaskPriority.URGENT, (long) taskRepository.findByPriority(TaskPriority.URGENT).size(),
            TaskPriority.HIGH, (long) taskRepository.findByPriority(TaskPriority.HIGH).size(),
            TaskPriority.MEDIUM, (long) taskRepository.findByPriority(TaskPriority.MEDIUM).size(),
            TaskPriority.LOW, (long) taskRepository.findByPriority(TaskPriority.LOW).size()
        );
        
        return new TaskStatistics(totalTasks, completedTasks, pendingTasks, inProgressTasks, cancelledTasks, overdueTasks, tasksByStatus, tasksByPriority);
    }
    
    private TaskDto convertToDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setDueDate(task.getDueDate());
        return dto;
    }
}
