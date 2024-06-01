package com.csc340.jpademo.Task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.csc340.jpademo.Goal.Goal;
import com.csc340.jpademo.Goal.GoalService;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private GoalService goalService;

    @GetMapping("/{goalId}")
    public String getAllTasksForGoal(@PathVariable int goalId, Model model) {
        Goal goal = goalService.getGoalById(goalId);
        List<Task> tasks = taskService.getAllTasks().stream()
                .filter(task -> task.getGoal() != null && task.getGoal().getGoalId() == goalId)
                .collect(Collectors.toList());
        model.addAttribute("tasks", tasks);
        model.addAttribute("goal", goal);
        return "task-list";
    }

    @GetMapping("/detail/{id}")
    public String getTaskById(@PathVariable int id, Model model) {
        Task task = taskService.getTaskById(id);
        model.addAttribute("task", task);
        return "task-detail";
    }

    @GetMapping("/new/{goalId}")
    public String createTaskForm(@PathVariable int goalId, Model model) {
        Task task = new Task();
        Goal goal = goalService.getGoalById(goalId);
        task.setGoal(goal);
        model.addAttribute("task", task);
        model.addAttribute("goal", goal);
        return "task-create";
    }

    @PostMapping("/save")
    public String saveTask(@RequestParam int goalId, @ModelAttribute Task task) {
        logger.info("Entering saveTask method");
        logger.info("Received goalId: {}", goalId);
        logger.info("Received task details: Title = {}, Details = {}, Status = {}", task.getTitle(), task.getDetails(), task.getStatus());


        Goal goal = goalService.getGoalById(goalId);
        if (goal == null) {
            logger.error("Goal not found for goalId: {}", goalId);
        }

        task.setGoal(goal);
        logger.info("Setting goal in task: {}", goal);

        // Save the task
        taskService.saveTask(task);
        logger.info("Task saved with ID: {}", task.getTaskId());

        return "redirect:/tasks/" + goalId;
    }

    @GetMapping("/edit/{id}")
    public String editTaskForm(@PathVariable int id, Model model) {
        logger.info("Entering editTaskForm method");
        logger.info("Received taskId: {}", id);

        Task task = taskService.getTaskById(id);
        if (task != null && task.getGoal() != null) {
            logger.info("Task found with ID: {}", task.getTaskId());
            model.addAttribute("task", task);
            model.addAttribute("goal", task.getGoal());
            return "task-update";
        }
        logger.error("Task not found with ID: {}", id);
        return "redirect:/tasks";
    }

    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable int id, @RequestParam int goalId, @ModelAttribute Task task) {
        logger.info("Entering updateTask method");
        logger.info("Received taskId: {}", id);
        logger.info("Received goalId: {}", goalId);
        logger.info("Received task details: Title = {}, Details = {}, Status = {}", task.getTitle(), task.getDetails(), task.getStatus()); // Log task details


        Task existingTask = taskService.getTaskById(id);
        if (existingTask != null) {
            logger.info("Found existing task with ID: {}", existingTask.getTaskId());


            Goal goal = goalService.getGoalById(goalId);
            if (goal == null) {
                logger.error("Goal not found for goalId: {}", goalId);
                return "redirect:/tasks";
            }


            task.setTaskId(id);
            task.setGoal(goal);
            logger.info("Setting goal in task: {}", goal);

            // Save the task
            taskService.saveTask(task);
            logger.info("Task updated with ID: {}", task.getTaskId());

            // Redirect to task list
            return "redirect:/tasks/" + goal.getGoalId();
        }
        logger.error("Task not found with ID: {}", id);
        return "redirect:/tasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable int id) {
        logger.info("Entering deleteTask method");
        logger.info("Received taskId: {}", id);

        Task task = taskService.getTaskById(id);
        if (task != null && task.getGoal() != null) {
            logger.info("Task found with ID: {}", task.getTaskId());
            int goalId = task.getGoal().getGoalId();
            taskService.deleteTask(id);
            logger.info("Task deleted with ID: {}", id);
            return "redirect:/tasks/" + goalId;
        }
        logger.error("Task not found with ID: {}", id);
        return "redirect:/tasks";
    }
}
