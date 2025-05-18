package com.example.taskmanagerlast;
import com.example.taskmanagerlast.TaskService;
import com.example.taskmanagerlast.EmailService;
import com.example.taskmanagerlast.PdfReportService;
import com.example.taskmanagerlast.SchedulerConfig;
import com.example.taskmanagerlast.TaskRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulerConfigTest {

    @Mock
    private TaskService taskService;

    @Mock
    private EmailService emailService;

    @Mock
    private PdfReportService pdfReportService;

    @InjectMocks
    private SchedulerConfig schedulerConfig;

    @Test
    void sendImportantTaskReminders_ShouldSendEmails() {
        // Arrange
        Task task = new Task();
        when(taskService.getImportantTasksDueSoon()).thenReturn(Collections.singletonList(task));

        // Act
        schedulerConfig.sendImportantTaskReminders();

        // Assert
        verify(emailService).sendEmail(anyString(), anyString(), anyString());
    }
}