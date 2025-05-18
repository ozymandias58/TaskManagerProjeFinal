package com.example.taskmanagerlast;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByDueDateBetween(LocalDate start, LocalDate end);
    List<Task> findByCompletedAndCompletionDateBetween(Boolean completed, LocalDate start, LocalDate end);
    long countByImportant(Integer important);
    List<Task> findByDueDateBeforeAndImportant(LocalDate date, int important);
    List<Task> findByCompleted(Boolean completed);
}