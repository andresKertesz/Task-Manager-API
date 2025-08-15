package com.akertesz.task_manager_api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akertesz.task_manager_api.dto.CreateTaskRequest;
import com.akertesz.task_manager_api.dto.TaskDto;
import com.akertesz.task_manager_api.dto.UpdateTaskRequest;
import com.akertesz.task_manager_api.model.Task;
import com.akertesz.task_manager_api.model.TaskPriority;
import com.akertesz.task_manager_api.model.TaskStatus;
import com.akertesz.task_manager_api.model.User;
import com.akertesz.task_manager_api.repository.TaskRepository;
import com.akertesz.task_manager_api.repository.UserRepository;

@Service
public class TaskServiceImpl implements TaskService {
    
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    public TaskDto createTask(CreateTaskRequest request, String username) {
        User user = userRepository.findByUsername(username);
        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setStatus(TaskStatus.PENDING);
        task.setUser(user);
        Task savedTask = taskRepository.save(task);
        return convertToDto(savedTask);
    }
    
    @Override
    public Optional<TaskDto> getTaskById(UUID id, String username) {
        User user = userRepository.findByUsername(username);
        return taskRepository.findByIdAndUserAndIsDeletedFalse(id, user)
                .map(this::convertToDto);
    }
    
    @Override
    public List<TaskDto> getAllTasks(String username) {
        User user = userRepository.findByUsername(username);
        return taskRepository.findByUserAndIsDeletedFalse(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<TaskDto> updateTask(UUID id, UpdateTaskRequest request, String username) {
        User user = userRepository.findByUsername(username);
        return taskRepository.findByIdAndUserAndIsDeletedFalse(id, user)
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
                    task.setUser(user);
                    Task updatedTask = taskRepository.save(task);
                    return convertToDto(updatedTask);
                });
    }
    
    @Override
    public boolean deleteTask(UUID id, String username) {
        User user = userRepository.findByUsername(username);
        if (taskRepository.findByIdAndUserAndIsDeletedFalse(id, user).isPresent()) {
            taskRepository.deleteTask(id, user);
            return true;
        }
        return false;
    }
    
    @Override
    public List<TaskDto> getTasksByStatus(TaskStatus status, String username) {
        User user = userRepository.findByUsername(username);
        return taskRepository.findByStatusAndUserAndIsDeletedFalse(status, user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskDto> getTasksByPriority(TaskPriority priority, String username) {
        User user = userRepository.findByUsername(username);
        return taskRepository.findByPriorityAndUserAndIsDeletedFalse(priority, user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskDto> getOverdueTasks(String username) {
        User user = userRepository.findByUsername(username);
        return taskRepository.findByDueDateBeforeAndUserAndIsDeletedFalse(LocalDateTime.now(), user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskDto> searchTasksByTitle(String title, String username) {
        User user = userRepository.findByUsername(username);
        return taskRepository.findByTitleContainingIgnoreCaseAndUserAndIsDeletedFalse(title, user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskDto> getTasksCreatedBetween(LocalDateTime startDate, LocalDateTime endDate, String username) {
        User user = userRepository.findByUsername(username);
        return taskRepository.findByCreatedAtBetweenAndUserAndIsDeletedFalse(startDate, endDate, user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskDto> getTasksOrderedByPriorityAndDueDate(String username) {
        User user = userRepository.findByUsername(username);
        return taskRepository.findAllOrderByPriorityAndDueDateAndUserAndIsDeletedFalse(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<TaskDto> changeTaskStatus(UUID id, TaskStatus status, String username) {
        User user = userRepository.findByUsername(username);
        return taskRepository.findByIdAndUserAndIsDeletedFalse(id, user)
                .map(task -> {
                    task.setStatus(status);
                    Task updatedTask = taskRepository.save(task);
                    return convertToDto(updatedTask);
                });
    }
    
    @Override
    public Optional<TaskDto> changeTaskPriority(UUID id, TaskPriority priority, String username) {
        User user = userRepository.findByUsername(username);
        return taskRepository.findByIdAndUserAndIsDeletedFalse(id, user)
                .map(task -> {  
                    task.setPriority(priority);
                    task.setUser(user);
                    Task updatedTask = taskRepository.save(task);
                    return convertToDto(updatedTask);
                });
    }
    
    @Override
    public TaskStatistics getTaskStatistics(String username) {
        User user = userRepository.findByUsername(username);
        long totalTasks = taskRepository.countByUserAndIsDeletedFalse(user);
        long pendingTasks = taskRepository.countByStatusAndUserAndIsDeletedFalse(TaskStatus.PENDING, user);
        long inProgressTasks = taskRepository.countByStatusAndUserAndIsDeletedFalse(TaskStatus.IN_PROGRESS, user);
        long completedTasks = taskRepository.countByStatusAndUserAndIsDeletedFalse(TaskStatus.COMPLETED, user);
        long cancelledTasks = taskRepository.countByStatusAndUserAndIsDeletedFalse(TaskStatus.CANCELLED, user);
        long overdueTasks = taskRepository.findByDueDateBeforeAndUserAndIsDeletedFalse(LocalDateTime.now(), user).size();
        
        // Create maps for tasks by status and priority
        Map<TaskStatus, Long> tasksByStatus = Map.of(
            TaskStatus.PENDING, pendingTasks,
            TaskStatus.IN_PROGRESS, inProgressTasks,
            TaskStatus.COMPLETED, completedTasks,
            TaskStatus.CANCELLED, cancelledTasks
        );
        
        Map<TaskPriority, Long> tasksByPriority = Map.of(
            TaskPriority.URGENT, (long) taskRepository.findByPriorityAndUserAndIsDeletedFalse(TaskPriority.URGENT, user).size(),
            TaskPriority.HIGH, (long) taskRepository.findByPriorityAndUserAndIsDeletedFalse(TaskPriority.HIGH, user).size(),
            TaskPriority.MEDIUM, (long) taskRepository.findByPriorityAndUserAndIsDeletedFalse(TaskPriority.MEDIUM, user).size(),
            TaskPriority.LOW, (long) taskRepository.findByPriorityAndUserAndIsDeletedFalse(TaskPriority.LOW, user).size()
        );
        
        return new TaskStatistics(totalTasks, completedTasks, pendingTasks, inProgressTasks, cancelledTasks, overdueTasks, tasksByStatus, tasksByPriority);
    }
    
    private TaskDto convertToDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId().toString());
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
