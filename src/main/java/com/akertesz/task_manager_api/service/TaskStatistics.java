package com.akertesz.task_manager_api.service;

import com.akertesz.task_manager_api.model.TaskPriority;
import com.akertesz.task_manager_api.model.TaskStatus;

import java.util.Map;

public class TaskStatistics {
    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private long inProgressTasks;
    private long cancelledTasks;
    private long overdueTasks;
    private Map<TaskStatus, Long> tasksByStatus;
    private Map<TaskPriority, Long> tasksByPriority;
    
    // Default constructor
    public TaskStatistics() {}
    
    // Constructor with all fields
    public TaskStatistics(long totalTasks, long completedTasks, long pendingTasks, 
                         long inProgressTasks, long cancelledTasks, long overdueTasks,
                         Map<TaskStatus, Long> tasksByStatus, Map<TaskPriority, Long> tasksByPriority) {
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.pendingTasks = pendingTasks;
        this.inProgressTasks = inProgressTasks;
        this.cancelledTasks = cancelledTasks;
        this.overdueTasks = overdueTasks;
        this.tasksByStatus = tasksByStatus;
        this.tasksByPriority = tasksByPriority;
    }
    
    // Getters and Setters
    public long getTotalTasks() {
        return totalTasks;
    }
    
    public void setTotalTasks(long totalTasks) {
        this.totalTasks = totalTasks;
    }
    
    public long getCompletedTasks() {
        return completedTasks;
    }
    
    public void setCompletedTasks(long completedTasks) {
        this.completedTasks = completedTasks;
    }
    
    public long getPendingTasks() {
        return pendingTasks;
    }
    
    public void setPendingTasks(long pendingTasks) {
        this.pendingTasks = pendingTasks;
    }
    
    public long getInProgressTasks() {
        return inProgressTasks;
    }
    
    public void setInProgressTasks(long inProgressTasks) {
        this.inProgressTasks = inProgressTasks;
    }
    
    public long getCancelledTasks() {
        return cancelledTasks;
    }
    
    public void setCancelledTasks(long cancelledTasks) {
        this.cancelledTasks = cancelledTasks;
    }
    
    public long getOverdueTasks() {
        return overdueTasks;
    }
    
    public void setOverdueTasks(long overdueTasks) {
        this.overdueTasks = overdueTasks;
    }
    
    public Map<TaskStatus, Long> getTasksByStatus() {
        return tasksByStatus;
    }
    
    public void setTasksByStatus(Map<TaskStatus, Long> tasksByStatus) {
        this.tasksByStatus = tasksByStatus;
    }
    
    public Map<TaskPriority, Long> getTasksByPriority() {
        return tasksByPriority;
    }
    
    public void setTasksByPriority(Map<TaskPriority, Long> tasksByPriority) {
        this.tasksByPriority = tasksByPriority;
    }
    
    // Calculate completion percentage
    public double getCompletionPercentage() {
        if (totalTasks == 0) {
            return 0.0;
        }
        return (double) completedTasks / totalTasks * 100;
    }
}
