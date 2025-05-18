package com.example.taskmanagerlast;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import com.example.taskmanagerlast.TaskService;
import com.example.taskmanagerlast.EmailService;
import com.example.taskmanagerlast.PdfReportService;
import com.example.taskmanagerlast.SchedulerConfig;
import com.example.taskmanagerlast.TaskRepository;


@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void findByCompleted_ShouldReturnCompletedTasks() {
        // Arrange
        Task task = new Task();
        task.setCompleted(true);
        taskRepository.save(task);

        // Act
        List<Task> result = taskRepository.findByCompleted(true);

        // Assert
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).getCompleted());
    }

    @Test
    void countByImportant_ShouldReturnCount() {
        // Arrange
        Task importantTask = new Task();
        importantTask.setImportant(1);
        taskRepository.save(importantTask);

        // Act
        Long count = taskRepository.countByImportant(1);

        // Assert
        assertEquals(1, count);
    }
}