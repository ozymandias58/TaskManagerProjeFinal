package com.example.taskmanagerlast;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.taskmanagerlast.TaskService;
import com.example.taskmanagerlast.EmailService;
import com.example.taskmanagerlast.PdfReportService;
import com.example.taskmanagerlast.SchedulerConfig;
import com.example.taskmanagerlast.TaskRepository;


@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void saveTask_ShouldReturnSavedTask() {
        // Arrange
        Task task = new Task();
        task.setDescription("Test Task");
        when(taskRepository.save(task)).thenReturn(task);

        // Act
        Task savedTask = taskService.saveTask(task);

        // Assert
        assertNotNull(savedTask);
        assertEquals("Test Task", savedTask.getDescription());
        verify(taskRepository).save(task);
    }

    @Test
    void toggleTaskCompletion_ShouldToggleStatus() {
        // Arrange
        Task task = new Task();
        task.setId(1L);
        task.setCompleted(false);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        // Act
        Task updatedTask = taskService.toggleTaskCompletion(1L);

        // Assert
        assertTrue(updatedTask.getCompleted());
        verify(taskRepository).save(task);
    }

    @Test
    void getImportantTasksDueSoon_ShouldReturnTasks() {
        // Arrange
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Task task1 = new Task();
        Task task2 = new Task();
        when(taskRepository.findByDueDateBeforeAndImportant(tomorrow, 1))
                .thenReturn(Arrays.asList(task1, task2));

        // Act
        List<Task> tasks = taskService.getImportantTasksDueSoon();

        // Assert
        assertEquals(2, tasks.size());
        verify(taskRepository).findByDueDateBeforeAndImportant(tomorrow, 1);
    }
}