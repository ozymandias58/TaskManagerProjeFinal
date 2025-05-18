package com.example.taskmanagerlast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${weather.api.url}")
    private String weatherApiUrl;
    @Value("${weather.api.key}")
    private String weatherApiKey;
    @Value("${weather.api.city}")
    private String weatherApiCity;

    @GetMapping
    public String getAllTasks(
            @RequestParam(value = "month", required = false)
            @DateTimeFormat(pattern = "yyyy-MM") YearMonth targetMonth,
            Model model) {

        YearMonth currentYearMonth = targetMonth != null ? targetMonth : YearMonth.now();
        LocalDate startDate = currentYearMonth.atDay(1);
        LocalDate endDate = currentYearMonth.atEndOfMonth();
        int daysInMonth = currentYearMonth.lengthOfMonth();
        int firstDayOfMonth = startDate.getDayOfWeek().getValue();

        Map<Integer, List<Task>> calendarTasks = new HashMap<>();
        taskRepository.findByDueDateBetween(startDate, endDate)
                .forEach(task -> {
                    int day = task.getDueDate().getDayOfMonth();
                    calendarTasks.computeIfAbsent(day, k -> new ArrayList<>()).add(task);
                });

        model.addAttribute("currentYearMonth", currentYearMonth);
        model.addAttribute("daysInMonth", daysInMonth);
        model.addAttribute("firstDayOfMonth", firstDayOfMonth);
        model.addAttribute("calendarTasks", calendarTasks);
        model.addAttribute("prevMonth", currentYearMonth.minusMonths(1));
        model.addAttribute("nextMonth", currentYearMonth.plusMonths(1));
        model.addAttribute("weekDays", getLocalizedWeekDays());
        model.addAttribute("activeTasks", taskService.getActiveTasks());
        model.addAttribute("completedTasks", taskService.getCompletedTasks());
        model.addAttribute("task", new Task());

        try {
            String weatherUrl = String.format("%s?q=%s&appid=%s&units=metric&lang=tr",
                    weatherApiUrl, weatherApiCity, weatherApiKey);
            WeatherResponse weatherResponse = restTemplate.getForObject(weatherUrl, WeatherResponse.class);
            model.addAttribute("weather", weatherResponse);
        } catch (Exception e) {
            model.addAttribute("weatherError", "Hava durumu bilgisi alınamadı");
        }

        return "task-list";
    }

    private List<String> getLocalizedWeekDays() {
        List<String> days = new ArrayList<>();
        Locale trLocale = new Locale("tr", "TR");

        for(int i = 1; i <= 7; i++) {
            String dayName = DayOfWeek.of(i)
                    .getDisplayName(TextStyle.SHORT, trLocale)
                    .replace("Pzt", "Paz")
                    .replace("Cum", "Cmt");
            days.add(dayName);
        }
        return days;
    }

    @PostMapping("/new")
    public String saveTask(@ModelAttribute Task task) {
        taskService.saveTask(task);
        return "redirect:/tasks";
    }

    @PostMapping("/toggle/{id}")
    public String toggleTaskCompletion(@PathVariable Long id) {
        taskService.toggleTaskCompletion(id);
        return "redirect:/tasks";
    }
}