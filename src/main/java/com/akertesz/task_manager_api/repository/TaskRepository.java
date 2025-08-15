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
    long countByUserAndDeletedFalse(User user);
    List<Task> findByUserAndDeletedFalse(User user);

    @Modifying
    @Query("UPDATE Task t SET t.status = :status WHERE t.id = :id AND t.user = :user")
    void updateTaskStatus(@Param("id") UUID id, @Param("status") TaskStatus status, @Param("user") User user);

    @Modifying
    @Query("UPDATE Task t SET t.priority = :priority WHERE t.id = :id AND t.user = :user")
    void updateTaskPriority(@Param("id") UUID id, @Param("priority") TaskPriority priority, @Param("user") User user);

    @Modifying
    @Query("UPDATE Task t SET t.deleted = true WHERE t.id = :id AND t.user = :user")
    void deleteTask(@Param("id") UUID id, @Param("user") User user);

    Optional<Task> findByIdAndUserAndDeletedFalse(UUID id, User user);
    // Find tasks by status
    List<Task> findByStatusAndUserAndDeletedFalse(TaskStatus status, User user);
    
    // Find tasks by priority
    List<Task> findByPriorityAndUserAndDeletedFalse(TaskPriority priority, User user);
    
    // Find tasks by status and priority
    List<Task> findByStatusAndPriorityAndUserAndDeletedFalse(TaskStatus status, TaskPriority priority, User user);
    
    // Find tasks due before a specific date
    List<Task> findByDueDateBeforeAndUserAndDeletedFalse(LocalDateTime date, User user);
    
    // Find overdue tasks (due date is in the past and status is not completed)
    @Query("SELECT t FROM Task t WHERE t.dueDate < :now AND t.status != 'COMPLETED' AND t.deleted = false")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now);
    
    // Find tasks by title containing a keyword (case-insensitive)
    List<Task> findByTitleContainingIgnoreCaseAndUserAndDeletedFalse(String title, User user);
    
    // Find tasks created between two dates
    List<Task> findByCreatedAtBetweenAndUserAndDeletedFalse(LocalDateTime startDate, LocalDateTime endDate, User user);
    
    // Count tasks by status
    long countByStatusAndUserAndDeletedFalse(TaskStatus status, User user);
    
    // Find tasks ordered by priority (high to low) and due date
    @Query("SELECT t FROM Task t ORDER BY " +
           "CASE t.priority " +
           "  WHEN 'URGENT' THEN 1 " +
           "  WHEN 'HIGH' THEN 2 " +
           "  WHEN 'MEDIUM' THEN 3 " +
           "  WHEN 'LOW' THEN 4 " +
           "END, t.dueDate ASC")
    List<Task> findAllOrderByPriorityAndDueDateAndUserAndDeletedFalse(User user);
}
