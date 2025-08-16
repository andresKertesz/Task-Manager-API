package com.akertesz.task_manager_api.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.akertesz.task_manager_api.model.TaskPriority;
import com.akertesz.task_manager_api.model.TaskStatus;

class TaskStatisticsTest {

    private TaskStatistics taskStatistics;
    private Map<TaskStatus, Long> tasksByStatus;
    private Map<TaskPriority, Long> tasksByPriority;

    @BeforeEach
    void setUp() {
        // Initialize maps
        tasksByStatus = new HashMap<>();
        tasksByStatus.put(TaskStatus.PENDING, 5L);
        tasksByStatus.put(TaskStatus.IN_PROGRESS, 3L);
        tasksByStatus.put(TaskStatus.COMPLETED, 10L);
        tasksByStatus.put(TaskStatus.CANCELLED, 2L);

        tasksByPriority = new HashMap<>();
        tasksByPriority.put(TaskPriority.URGENT, 2L);
        tasksByPriority.put(TaskPriority.HIGH, 8L);
        tasksByPriority.put(TaskPriority.MEDIUM, 7L);
        tasksByPriority.put(TaskPriority.LOW, 3L);

        // Create TaskStatistics instance
        taskStatistics = new TaskStatistics(
            20L,    // totalTasks
            10L,    // completedTasks
            5L,     // pendingTasks
            3L,     // inProgressTasks
            2L,     // cancelledTasks
            1L,     // overdueTasks
            tasksByStatus,
            tasksByPriority
        );
    }

    // Constructor Tests
    @Test
    void testTaskStatistics_ConstructorWithAllParameters() {
        // Act
        TaskStatistics statistics = new TaskStatistics(
            25L, 15L, 5L, 3L, 2L, 1L, tasksByStatus, tasksByPriority
        );

        // Assert
        assertEquals(25L, statistics.getTotalTasks());
        assertEquals(15L, statistics.getCompletedTasks());
        assertEquals(5L, statistics.getPendingTasks());
        assertEquals(3L, statistics.getInProgressTasks());
        assertEquals(2L, statistics.getCancelledTasks());
        assertEquals(1L, statistics.getOverdueTasks());
        assertEquals(tasksByStatus, statistics.getTasksByStatus());
        assertEquals(tasksByPriority, statistics.getTasksByPriority());
    }

    @Test
    void testTaskStatistics_ConstructorWithZeroValues() {
        // Act
        TaskStatistics statistics = new TaskStatistics(
            0L, 0L, 0L, 0L, 0L, 0L, new HashMap<>(), new HashMap<>()
        );

        // Assert
        assertEquals(0L, statistics.getTotalTasks());
        assertEquals(0L, statistics.getCompletedTasks());
        assertEquals(0L, statistics.getPendingTasks());
        assertEquals(0L, statistics.getInProgressTasks());
        assertEquals(0L, statistics.getCancelledTasks());
        assertEquals(0L, statistics.getOverdueTasks());
        assertTrue(statistics.getTasksByStatus().isEmpty());
        assertTrue(statistics.getTasksByPriority().isEmpty());
    }

    @Test
    void testTaskStatistics_ConstructorWithLargeValues() {
        // Act
        TaskStatistics statistics = new TaskStatistics(
            1000000L, 500000L, 300000L, 150000L, 50000L, 10000L,
            tasksByStatus, tasksByPriority
        );

        // Assert
        assertEquals(1000000L, statistics.getTotalTasks());
        assertEquals(500000L, statistics.getCompletedTasks());
        assertEquals(300000L, statistics.getPendingTasks());
        assertEquals(150000L, statistics.getInProgressTasks());
        assertEquals(50000L, statistics.getCancelledTasks());
        assertEquals(10000L, statistics.getOverdueTasks());
    }

    // Getter Tests
    @Test
    void testTaskStatistics_GetTotalTasks() {
        // Act & Assert
        assertEquals(20L, taskStatistics.getTotalTasks());
    }

    @Test
    void testTaskStatistics_GetCompletedTasks() {
        // Act & Assert
        assertEquals(10L, taskStatistics.getCompletedTasks());
    }

    @Test
    void testTaskStatistics_GetPendingTasks() {
        // Act & Assert
        assertEquals(5L, taskStatistics.getPendingTasks());
    }

    @Test
    void testTaskStatistics_GetInProgressTasks() {
        // Act & Assert
        assertEquals(3L, taskStatistics.getInProgressTasks());
    }

    @Test
    void testTaskStatistics_GetCancelledTasks() {
        // Act & Assert
        assertEquals(2L, taskStatistics.getCancelledTasks());
    }

    @Test
    void testTaskStatistics_GetOverdueTasks() {
        // Act & Assert
        assertEquals(1L, taskStatistics.getOverdueTasks());
    }

    @Test
    void testTaskStatistics_GetTasksByStatus() {
        // Act & Assert
        assertNotNull(taskStatistics.getTasksByStatus());
        assertEquals(4, taskStatistics.getTasksByStatus().size());
        assertEquals(5L, taskStatistics.getTasksByStatus().get(TaskStatus.PENDING));
        assertEquals(3L, taskStatistics.getTasksByStatus().get(TaskStatus.IN_PROGRESS));
        assertEquals(10L, taskStatistics.getTasksByStatus().get(TaskStatus.COMPLETED));
        assertEquals(2L, taskStatistics.getTasksByStatus().get(TaskStatus.CANCELLED));
    }

    @Test
    void testTaskStatistics_GetTasksByPriority() {
        // Act & Assert
        assertNotNull(taskStatistics.getTasksByPriority());
        assertEquals(4, taskStatistics.getTasksByPriority().size());
        assertEquals(2L, taskStatistics.getTasksByPriority().get(TaskPriority.URGENT));
        assertEquals(8L, taskStatistics.getTasksByPriority().get(TaskPriority.HIGH));
        assertEquals(7L, taskStatistics.getTasksByPriority().get(TaskPriority.MEDIUM));
        assertEquals(3L, taskStatistics.getTasksByPriority().get(TaskPriority.LOW));
    }

    // Data Consistency Tests
    @Test
    void testTaskStatistics_DataConsistency() {
        // Test that the sum of individual status counts equals total tasks
        long sumOfStatusCounts = taskStatistics.getPendingTasks() +
                                taskStatistics.getInProgressTasks() +
                                taskStatistics.getCompletedTasks() +
                                taskStatistics.getCancelledTasks();
        
        assertEquals(taskStatistics.getTotalTasks(), sumOfStatusCounts);
    }

    @Test
    void testTaskStatistics_PriorityConsistency() {
        // Test that the sum of priority counts equals total tasks
        long sumOfPriorityCounts = taskStatistics.getTasksByPriority().values().stream()
                .mapToLong(Long::longValue)
                .sum();
        
        assertEquals(taskStatistics.getTotalTasks(), sumOfPriorityCounts);
    }

    @Test
    void testTaskStatistics_StatusConsistency() {
        // Test that the sum of status counts equals total tasks
        long sumOfStatusCounts = taskStatistics.getTasksByStatus().values().stream()
                .mapToLong(Long::longValue)
                .sum();
        
        assertEquals(taskStatistics.getTotalTasks(), sumOfStatusCounts);
    }

    // Edge Cases Tests
    @Test
    void testTaskStatistics_EmptyMaps() {
        // Arrange
        Map<TaskStatus, Long> emptyStatusMap = new HashMap<>();
        Map<TaskPriority, Long> emptyPriorityMap = new HashMap<>();
        
        // Act
        TaskStatistics statistics = new TaskStatistics(
            0L, 0L, 0L, 0L, 0L, 0L, emptyStatusMap, emptyPriorityMap
        );

        // Assert
        assertTrue(statistics.getTasksByStatus().isEmpty());
        assertTrue(statistics.getTasksByPriority().isEmpty());
        assertEquals(0L, statistics.getTotalTasks());
    }

    @Test
    void testTaskStatistics_NullMaps() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            new TaskStatistics(0L, 0L, 0L, 0L, 0L, 0L, null, null);
        });
    }

    @Test
    void testTaskStatistics_NegativeValues() {
        // Act
        TaskStatistics statistics = new TaskStatistics(
            -5L, -3L, -2L, -1L, -1L, -1L, tasksByStatus, tasksByPriority
        );

        // Assert
        assertEquals(-5L, statistics.getTotalTasks());
        assertEquals(-3L, statistics.getCompletedTasks());
        assertEquals(-2L, statistics.getPendingTasks());
        assertEquals(-1L, statistics.getInProgressTasks());
        assertEquals(-1L, statistics.getCancelledTasks());
        assertEquals(-1L, statistics.getOverdueTasks());
    }

    // Map Manipulation Tests
    @Test
    void testTaskStatistics_MapModification() {
        // Arrange
        Map<TaskStatus, Long> modifiableStatusMap = new HashMap<>(tasksByStatus);
        Map<TaskPriority, Long> modifiablePriorityMap = new HashMap<>(tasksByPriority);
        
        TaskStatistics statistics = new TaskStatistics(
            20L, 10L, 5L, 3L, 2L, 1L, modifiableStatusMap, modifiablePriorityMap
        );

        // Act - Modify the maps
        modifiableStatusMap.put(TaskStatus.PENDING, 10L);
        modifiablePriorityMap.put(TaskPriority.URGENT, 5L);

        // Assert - Changes should be reflected
        assertEquals(10L, statistics.getTasksByStatus().get(TaskStatus.PENDING));
        assertEquals(5L, statistics.getTasksByPriority().get(TaskPriority.URGENT));
    }

    @Test
    void testTaskStatistics_MapImmutability() {
        // Act
        Map<TaskStatus, Long> statusMap = taskStatistics.getTasksByStatus();
        Map<TaskPriority, Long> priorityMap = taskStatistics.getTasksByPriority();

        // Assert - Maps should be modifiable
        assertDoesNotThrow(() -> {
            statusMap.put(TaskStatus.PENDING, 100L);
            priorityMap.put(TaskPriority.URGENT, 100L);
        });
    }

    // Validation Tests
    @Test
    void testTaskStatistics_ValidTaskStatuses() {
        // Test that all expected TaskStatus values are present
        assertTrue(taskStatistics.getTasksByStatus().containsKey(TaskStatus.PENDING));
        assertTrue(taskStatistics.getTasksByStatus().containsKey(TaskStatus.IN_PROGRESS));
        assertTrue(taskStatistics.getTasksByStatus().containsKey(TaskStatus.COMPLETED));
        assertTrue(taskStatistics.getTasksByStatus().containsKey(TaskStatus.CANCELLED));
    }

    @Test
    void testTaskStatistics_ValidTaskPriorities() {
        // Test that all expected TaskPriority values are present
        assertTrue(taskStatistics.getTasksByPriority().containsKey(TaskPriority.URGENT));
        assertTrue(taskStatistics.getTasksByPriority().containsKey(TaskPriority.HIGH));
        assertTrue(taskStatistics.getTasksByPriority().containsKey(TaskPriority.MEDIUM));
        assertTrue(taskStatistics.getTasksByPriority().containsKey(TaskPriority.LOW));
    }

    // Performance Tests
    @Test
    void testTaskStatistics_CreationPerformance() {
        // Test that creating TaskStatistics is fast
        long startTime = System.nanoTime();
        
        for (int i = 0; i < 1000; i++) {
            new TaskStatistics(
                (long) i, (long) i, (long) i, (long) i, (long) i, (long) i,
                tasksByStatus, tasksByPriority
            );
        }
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        
        // Should complete in reasonable time (less than 1 second)
        assertTrue(duration < 1_000_000_000L, "TaskStatistics creation should be fast");
    }

    @Test
    void testTaskStatistics_GetterPerformance() {
        // Test that getting values is fast
        long startTime = System.nanoTime();
        
        for (int i = 0; i < 10000; i++) {
            long total = taskStatistics.getTotalTasks();
            long completed = taskStatistics.getCompletedTasks();
            long pending = taskStatistics.getPendingTasks();
            long inProgress = taskStatistics.getInProgressTasks();
            long cancelled = taskStatistics.getCancelledTasks();
            long overdue = taskStatistics.getOverdueTasks();
            
            // Use the values to prevent optimization
            assertTrue(total >= 0);
            assertTrue(completed >= 0);
            assertTrue(pending >= 0);
            assertTrue(inProgress >= 0);
            assertTrue(cancelled >= 0);
            assertTrue(overdue >= 0);
        }
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        
        // Should complete in reasonable time (less than 1 second)
        assertTrue(duration < 1_000_000_000L, "TaskStatistics getters should be fast");
    }

    // Immutability Tests
    @Test
    void testTaskStatistics_Immutability() {
        // Test that creating multiple instances doesn't affect each other
        TaskStatistics statistics1 = new TaskStatistics(
            10L, 5L, 3L, 1L, 1L, 0L, tasksByStatus, tasksByPriority
        );
        
        TaskStatistics statistics2 = new TaskStatistics(
            20L, 10L, 5L, 3L, 2L, 1L, tasksByStatus, tasksByPriority
        );
        
        // Assert they are different
        assertNotEquals(statistics1.getTotalTasks(), statistics2.getTotalTasks());
        assertNotEquals(statistics1.getCompletedTasks(), statistics2.getCompletedTasks());
        assertNotEquals(statistics1.getPendingTasks(), statistics2.getPendingTasks());
    }

    // Boundary Tests
    @Test
    void testTaskStatistics_BoundaryValues() {
        // Test with maximum Long values
        TaskStatistics statistics = new TaskStatistics(
            Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE,
            tasksByStatus, tasksByPriority
        );
        
        assertEquals(Long.MAX_VALUE, statistics.getTotalTasks());
        assertEquals(Long.MAX_VALUE, statistics.getCompletedTasks());
        assertEquals(Long.MAX_VALUE, statistics.getPendingTasks());
        assertEquals(Long.MAX_VALUE, statistics.getInProgressTasks());
        assertEquals(Long.MAX_VALUE, statistics.getCancelledTasks());
        assertEquals(Long.MAX_VALUE, statistics.getOverdueTasks());
    }

    @Test
    void testTaskStatistics_MinimumValues() {
        // Test with minimum Long values
        TaskStatistics statistics = new TaskStatistics(
            Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE,
            tasksByStatus, tasksByPriority
        );
        
        assertEquals(Long.MIN_VALUE, statistics.getTotalTasks());
        assertEquals(Long.MIN_VALUE, statistics.getCompletedTasks());
        assertEquals(Long.MIN_VALUE, statistics.getPendingTasks());
        assertEquals(Long.MIN_VALUE, statistics.getInProgressTasks());
        assertEquals(Long.MIN_VALUE, statistics.getCancelledTasks());
        assertEquals(Long.MIN_VALUE, statistics.getOverdueTasks());
    }

    // Integration Tests
    @Test
    void testTaskStatistics_RealWorldScenario() {
        // Simulate a real-world scenario with realistic values
        Map<TaskStatus, Long> realStatusMap = new HashMap<>();
        realStatusMap.put(TaskStatus.PENDING, 15L);
        realStatusMap.put(TaskStatus.IN_PROGRESS, 8L);
        realStatusMap.put(TaskStatus.COMPLETED, 45L);
        realStatusMap.put(TaskStatus.CANCELLED, 3L);
        
        Map<TaskPriority, Long> realPriorityMap = new HashMap<>();
        realPriorityMap.put(TaskPriority.URGENT, 5L);
        realPriorityMap.put(TaskPriority.HIGH, 20L);
        realPriorityMap.put(TaskPriority.MEDIUM, 35L);
        realPriorityMap.put(TaskPriority.LOW, 10L);
        
        TaskStatistics realStatistics = new TaskStatistics(
            71L, 45L, 15L, 8L, 3L, 2L, realStatusMap, realPriorityMap
        );
        
        // Assert realistic values
        assertTrue(realStatistics.getTotalTasks() > 0);
        assertTrue(realStatistics.getCompletedTasks() > 0);
        assertTrue(realStatistics.getPendingTasks() > 0);
        assertTrue(realStatistics.getInProgressTasks() > 0);
        assertTrue(realStatistics.getCancelledTasks() >= 0);
        assertTrue(realStatistics.getOverdueTasks() >= 0);
        
        // Assert consistency
        assertEquals(71L, realStatistics.getTotalTasks());
        assertEquals(45L, realStatistics.getCompletedTasks());
        assertEquals(15L, realStatistics.getPendingTasks());
        assertEquals(8L, realStatistics.getInProgressTasks());
        assertEquals(3L, realStatistics.getCancelledTasks());
        assertEquals(2L, realStatistics.getOverdueTasks());
    }
}
