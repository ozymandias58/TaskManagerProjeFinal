package com.example.taskmanagerlast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    private TaskRepository taskRepository;

    // YENİ METOTLAR
    public List<Task> getTasksCompletedBetween(LocalDate startDate, LocalDate endDate) {
        return taskRepository.findByCompletedAndCompletionDateBetween(true, startDate, endDate);
    }

    public Long getImportantTaskCount() {
        return taskRepository.countByImportant(1);
    }

    // GÜNCELLENMİŞ TOGGLE METODU
    public Task toggleTaskCompletion(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Görev bulunamadı: " + id));

        task.setCompleted(!task.getCompleted());
        task.setCompletionDate(task.getCompleted() ? LocalDate.now() : null); // Tamamlama tarihi güncelleme

        Task updatedTask = taskRepository.save(task);

        if (updatedTask.getImportant() == 1) {
            logger.info("ÖNEMLİ GÖREV DURUM DEĞİŞTİRDİ: {} -> {}",
                    updatedTask.getId(),
                    updatedTask.getCompleted() ? "Tamamlandı" : "Beklemede");
        }

        return updatedTask;
    }

    // DİĞER METOTLAR (Aynı kaldı)
    public List<Task> getImportantTasksDueSoon() {
        return taskRepository.findByDueDateBeforeAndImportant(LocalDate.now().plusDays(1), 1);
    }

    public List<Task> getCompletedTasks() {
        return taskRepository.findByCompleted(true);
    }

    public List<Task> getActiveTasks() {
        return taskRepository.findByCompleted(false);
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }
}