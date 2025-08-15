package com.akertesz.task_manager_api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.akertesz.task_manager_api.model.Task;
import com.akertesz.task_manager_api.model.TaskPriority;
import com.akertesz.task_manager_api.model.TaskStatus;
import com.akertesz.task_manager_api.model.User;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    
    // Soft delete methods
    long countByUserAndIsDeletedFalse(User user);
    List<Task> findByUserAndIsDeletedFalse(User user);
    
    // Find task by ID and user (not deleted)
    Optional<Task> findByIdAndUserAndIsDeletedFalse(UUID id, User user);
    
    // Find tasks by status and user (not deleted)
    List<Task> findByStatusAndUserAndIsDeletedFalse(TaskStatus status, User user);
    
    // Find tasks by priority and user (not deleted)
    List<Task> findByPriorityAndUserAndIsDeletedFalse(TaskPriority priority, User user);
    
    // Find tasks by status and priority and user (not deleted)
    List<Task> findByStatusAndPriorityAndUserAndIsDeletedFalse(TaskStatus status, TaskPriority priority, User user);
    
    // Find tasks due before a specific date and user (not deleted)
    List<Task> findByDueDateBeforeAndUserAndIsDeletedFalse(LocalDateTime date, User user);
    
    // Find overdue tasks for a user (not deleted)
    @Query("SELECT t FROM Task t WHERE t.dueDate < :now AND t.status != 'COMPLETED' AND t.user = :user AND t.isDeleted = false")
    List<Task> findOverdueTasksByUser(@Param("now") LocalDateTime now, @Param("user") User user);
    
    // Find tasks by title containing a keyword for a user (not deleted)
    List<Task> findByTitleContainingIgnoreCaseAndUserAndIsDeletedFalse(String title, User user);
    
    // Find tasks created between two dates for a user (not deleted)
    List<Task> findByCreatedAtBetweenAndUserAndIsDeletedFalse(LocalDateTime startDate, LocalDateTime endDate, User user);
    
    // Count tasks by status for a user (not deleted)
    long countByStatusAndUserAndIsDeletedFalse(TaskStatus status, User user);
    
    // Find tasks ordered by priority and due date for a user (not deleted)
    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.isDeleted = false ORDER BY " +
           "CASE t.priority " +
           "  WHEN 'URGENT' THEN 1 " +
           "  WHEN 'HIGH' THEN 2 " +
           "  WHEN 'MEDIUM' THEN 3 " +
           "  WHEN 'LOW' THEN 4 " +
           "END, t.dueDate ASC")
    List<Task> findAllOrderByPriorityAndDueDateAndUserAndIsDeletedFalse(@Param("user") User user);
    
    // Soft delete method
    @Modifying
    @Query("UPDATE Task t SET t.isDeleted = true WHERE t.id = :id AND t.user = :user")
    void deleteTask(@Param("id") UUID id, @Param("user") User user);
    
    // Update task status
    @Modifying
    @Query("UPDATE Task t SET t.status = :status WHERE t.id = :id AND t.user = :user AND t.isDeleted = false")
    void updateTaskStatus(@Param("id") UUID id, @Param("status") TaskStatus status, @Param("user") User user);
    
    // Update task priority
    @Modifying
    @Query("UPDATE Task t SET t.priority = :priority WHERE t.id = :id AND t.user = :user AND t.isDeleted = false")
    void updateTaskPriority(@Param("id") UUID id, @Param("priority") TaskPriority priority, @Param("user") User user);
    
    // Find tasks by username (for service layer convenience)
    @Query("SELECT t FROM Task t WHERE t.user.username = :username AND t.isDeleted = false")
    List<Task> findByUsername(@Param("username") String username);
    
    // Find task by ID and username (not deleted)
    @Query("SELECT t FROM Task t WHERE t.id = :id AND t.user.username = :username AND t.isDeleted = false")
    Optional<Task> findByIdAndUsername(@Param("id") UUID id, @Param("username") String username);
}
