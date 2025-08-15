package com.akertesz.task_manager_api.repository;

import com.akertesz.task_manager_api.model.Task;
import com.akertesz.task_manager_api.model.TaskPriority;
import com.akertesz.task_manager_api.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Find tasks by status
    List<Task> findByStatus(TaskStatus status);
    
    // Find tasks by priority
    List<Task> findByPriority(TaskPriority priority);
    
    // Find tasks by status and priority
    List<Task> findByStatusAndPriority(TaskStatus status, TaskPriority priority);
    
    // Find tasks due before a specific date
    List<Task> findByDueDateBefore(LocalDateTime date);
    
    // Find overdue tasks (due date is in the past and status is not completed)
    @Query("SELECT t FROM Task t WHERE t.dueDate < :now AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now);
    
    // Find tasks by title containing a keyword (case-insensitive)
    List<Task> findByTitleContainingIgnoreCase(String title);
    
    // Find tasks created between two dates
    List<Task> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Count tasks by status
    long countByStatus(TaskStatus status);
    
    // Find tasks ordered by priority (high to low) and due date
    @Query("SELECT t FROM Task t ORDER BY " +
           "CASE t.priority " +
           "  WHEN 'URGENT' THEN 1 " +
           "  WHEN 'HIGH' THEN 2 " +
           "  WHEN 'MEDIUM' THEN 3 " +
           "  WHEN 'LOW' THEN 4 " +
           "END, t.dueDate ASC")
    List<Task> findAllOrderByPriorityAndDueDate();
}
