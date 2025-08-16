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
import com.akertesz.task_manager_api.exception.TaskNotFoundException;
import com.akertesz.task_manager_api.exception.UserNotFoundException;
import com.akertesz.task_manager_api.exception.InvalidRequestException;
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
        if (user == null) {
            throw new UserNotFoundException("User not found: " + username);
        }
        
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
    public TaskDto getTaskById(UUID id, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + username);
        }
        
        Task task = taskRepository.findByIdAndUserAndIsDeletedFalse(id, user)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        return convertToDto(task);
    }
    
    @Override
    public List<TaskDto> getAllTasks(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + username);
        }
        
        return taskRepository.findByUserAndIsDeletedFalse(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public TaskDto updateTask(UUID id, UpdateTaskRequest request, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + username);
        }
        
        Task task = taskRepository.findByIdAndUserAndIsDeletedFalse(id, user)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        
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
    }
    
    @Override
    public boolean deleteTask(UUID id, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + username);
        }
        
        Task task = taskRepository.findByIdAndUserAndIsDeletedFalse(id, user)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        
        taskRepository.deleteTask(id, user);
        return true;
    }
    
    @Override
    public List<TaskDto> getTasksByStatus(TaskStatus status, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + username);
        }
        
        return taskRepository.findByStatusAndUserAndIsDeletedFalse(status, user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskDto> getTasksByPriority(TaskPriority priority, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + username);
        }
        
        return taskRepository.findByPriorityAndUserAndIsDeletedFalse(priority, user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskDto> getOverdueTasks(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + username);
        }
        
        return taskRepository.findByDueDateBeforeAndUserAndIsDeletedFalse(LocalDateTime.now(), user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskDto> searchTasksByTitle(String title, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + username);
        }
        
        return taskRepository.findByTitleContainingIgnoreCaseAndUserAndIsDeletedFalse(title, user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskDto> getTasksCreatedBetween(LocalDateTime startDate, LocalDateTime endDate, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + username);
        }
        
        return taskRepository.findByCreatedAtBetweenAndUserAndIsDeletedFalse(startDate, endDate, user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskDto> getTasksOrderedByPriorityAndDueDate(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + username);
        }
        
        return taskRepository.findAllOrderByPriorityAndDueDateAndUserAndIsDeletedFalse(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public TaskDto changeTaskStatus(UUID id, TaskStatus status, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + username);
        }
        
        Task task = taskRepository.findByIdAndUserAndIsDeletedFalse(id, user)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        
        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        return convertToDto(updatedTask);
    }
    
    /**
     * Changes task status with logical state transition validation.
     * 
     * This method implements a state machine that ensures tasks can only transition
     * to valid states based on their current status:
     * 
     * State Transition Rules:
     * - PENDING → IN_PROGRESS, COMPLETED, or CANCELLED
     * - IN_PROGRESS → COMPLETED, CANCELLED, or back to PENDING
     * - COMPLETED → No transitions allowed (final state)
     * - CANCELLED → Can be reactivated to PENDING or IN_PROGRESS
     * 
     * Examples:
     * - A PENDING task can be started (→ IN_PROGRESS), completed (→ COMPLETED), or cancelled (→ CANCELLED)
     * - An IN_PROGRESS task can be completed (→ COMPLETED), cancelled (→ CANCELLED), or put on hold (→ PENDING)
     * - A COMPLETED task cannot be changed (it's done!)
     * - A CANCELLED task can be reactivated (→ PENDING or IN_PROGRESS)
     * 
     * @param id Task ID
     * @param newStatus The desired new status
     * @param username Username for authentication
     * @return Updated TaskDto
     * @throws InvalidRequestException if the status transition is not allowed
     */
    @Override
    public TaskDto changeTaskStatusWithValidation(UUID id, TaskStatus newStatus, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + username);
        }
        
        Task task = taskRepository.findByIdAndUserAndIsDeletedFalse(id, user)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        
        TaskStatus currentStatus = task.getStatus();
        
        // Validate state transition
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new InvalidRequestException(
                String.format("Invalid status transition from %s to %s. Allowed transitions: %s", 
                    currentStatus, newStatus, getValidTransitions(currentStatus))
            );
        }
        
        // Apply the new status
        task.setStatus(newStatus);
        
        // Note: Task completion and cancellation dates are not currently stored in the Task model
        // The status change is sufficient to track the current state
        
        Task updatedTask = taskRepository.save(task);
        return convertToDto(updatedTask);
    }
    
    /**
     * Validates if a status transition is allowed based on the current status
     */
    private boolean isValidStatusTransition(TaskStatus currentStatus, TaskStatus newStatus) {
        if (currentStatus == newStatus) {
            return true; // No change needed
        }
        
        switch (currentStatus) {
            case PENDING:
                // PENDING tasks can move to IN_PROGRESS, COMPLETED, or CANCELLED
                return newStatus == TaskStatus.IN_PROGRESS || 
                       newStatus == TaskStatus.COMPLETED || 
                       newStatus == TaskStatus.CANCELLED;
                       
            case IN_PROGRESS:
                // IN_PROGRESS tasks can move to COMPLETED, CANCELLED, or back to PENDING
                return newStatus == TaskStatus.COMPLETED || 
                       newStatus == TaskStatus.CANCELLED || 
                       newStatus == TaskStatus.PENDING;
                       
            case COMPLETED:
                // COMPLETED tasks are final - cannot change to other statuses
                return false;
                
            case CANCELLED:
                // CANCELLED tasks can be reactivated to PENDING or IN_PROGRESS
                return newStatus == TaskStatus.PENDING || 
                       newStatus == TaskStatus.IN_PROGRESS;
                       
            default:
                return false;
        }
    }
    
    /**
     * Returns a list of valid status transitions for the current status
     */
    private String getValidTransitions(TaskStatus currentStatus) {
        switch (currentStatus) {
            case PENDING:
                return "IN_PROGRESS, COMPLETED, CANCELLED";
            case IN_PROGRESS:
                return "COMPLETED, CANCELLED, PENDING";
            case COMPLETED:
                return "No transitions allowed (final state)";
            case CANCELLED:
                return "PENDING, IN_PROGRESS";
            default:
                return "Unknown status";
        }
    }
    
    @Override
    public TaskDto changeTaskPriority(UUID id, TaskPriority priority, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + username);
        }
        
        Task task = taskRepository.findByIdAndUserAndIsDeletedFalse(id, user)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        
        task.setPriority(priority);
        task.setUser(user);
        Task updatedTask = taskRepository.save(task);
        return convertToDto(updatedTask);
    }
    
    @Override
    public TaskStatistics getTaskStatistics(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + username);
        }
        
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
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        
        TaskDto dto = new TaskDto();
        dto.setId(task.getId() != null ? task.getId().toString() : null);
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
