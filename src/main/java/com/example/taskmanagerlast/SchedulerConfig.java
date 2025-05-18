package com.example.taskmanagerlast;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDate;
import java.util.List;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PdfReportService pdfReportService;

    // ÖNEMLİ GÖREV HATIRLATMA (GÜNLÜK)
    @Scheduled(fixedRate = 86400000) // 24 saatte bir
    public void sendImportantTaskReminders() {
        List<Task> tasks = taskService.getImportantTasksDueSoon();
        for (Task task : tasks) {
            emailService.sendEmail(
                    "ahmetbursal201@gmail.com",
                    "⏰ Görev Hatırlatıcı: " + task.getDescription(),
                    "Son Tarih: " + task.getDueDate() + "\nAçıklama: " + task.getDescription()
            );

            logger.info("HATIRLATMA GÖNDERİLDİ - Görev ID: {}", task.getId());
        }
    }

    // GÜNLÜK RAPOR (HER GÜN SABAH 9:00'DA)
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDailyReport() {
        try {
            byte[] report = pdfReportService.generateDailyReport();
            emailService.sendEmailWithAttachment(
                    "ahmetbursal201@gmail.com",
                    "📊 Günlük Performans Raporu",
                    "Ekte günlük raporunuz bulunmaktadır.",
                    report,
                    "daily-report.pdf"
            );
            logger.info("GÜNLÜK RAPOR GÖNDERİLDİ");
        } catch (Exception e) {
            logger.error("Günlük rapor gönderilemedi: {}", e.getMessage());
        }
    }

    // HAFTALIK RAPOR (PAZARTESİ SABAH 9:00'DA)
    @Scheduled(cron = "0 0 9 * * MON")
    public void sendWeeklyReport() {
        try {
            byte[] report = pdfReportService.generateWeeklyReport();
            emailService.sendEmailWithAttachment(
                    "ahmetbursal201@gmail.com",
                    "📈 Haftalık Performans Raporu",
                    "Ekte haftalık raporunuz bulunmaktadır.",
                    report,
                    "weekly-report.pdf"
            );
            logger.info("HAFTALIK RAPOR GÖNDERİLDİ");
        } catch (Exception e) {
            logger.error("Haftalık rapor gönderilemedi: {}", e.getMessage());
        }
    }
}