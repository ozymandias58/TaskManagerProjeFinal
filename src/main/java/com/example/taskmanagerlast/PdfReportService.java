package com.example.taskmanagerlast;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PdfReportService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TemplateEngine templateEngine;

    public byte[] generateDailyReport() throws IOException {
        LocalDate today = LocalDate.now();
        List<Task> dailyTasks = taskService.getTasksCompletedBetween(today, today);
        return generateReport("Günlük Performans Raporu", dailyTasks, today.minusDays(1), today);
    }

    public byte[] generateWeeklyReport() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(7);
        List<Task> weeklyTasks = taskService.getTasksCompletedBetween(start, end);
        return generateReport("Haftalık Performans Raporu", weeklyTasks, start, end);
    }

    private byte[] generateReport(String title, List<Task> tasks, LocalDate start, LocalDate end) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Başlık
            document.add(new Paragraph(title)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20));

            // Rapor aralığı
            document.add(new Paragraph("Rapor Aralığı: " +
                    start.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " +
                    end.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                    .setMarginBottom(20));

            // İstatistikler
            Table table = new Table(2);
            table.addHeaderCell("Metrik");
            table.addHeaderCell("Değer");
            table.addCell("Tamamlanan Görev Sayısı");
            table.addCell(String.valueOf(tasks.size()));
            table.addCell("Toplam Önemli Görev");
            table.addCell(String.valueOf(taskService.getImportantTaskCount()));
            document.add(table);

            // Görev listesi
            document.add(new Paragraph("Görev Detayları:")
                    .setMarginTop(20));
            Table taskTable = new Table(4);
            taskTable.addHeaderCell("Açıklama");
            taskTable.addHeaderCell("Tamamlama Tarihi");
            taskTable.addHeaderCell("Önemli");
            taskTable.addHeaderCell("Süre (Gün)");

            for (Task task : tasks) {
                taskTable.addCell(task.getDescription());
                taskTable.addCell(task.getCompletionDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                taskTable.addCell(task.getImportant() == 1 ? "Evet" : "Hayır");
                long days = ChronoUnit.DAYS.between(task.getDueDate(), task.getCompletionDate());
                taskTable.addCell(String.valueOf(days));
            }
            document.add(taskTable);

            document.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("PDF oluşturma hatası: " + e.getMessage());
        }
    }
}