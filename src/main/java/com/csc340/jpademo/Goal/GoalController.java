package com.csc340.jpademo.Goal;

import com.csc340.jpademo.Task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
@RequestMapping("/goals")
public class GoalController {

    @Autowired
    private GoalService goalService;

    private static final Logger logger = LoggerFactory.getLogger(GoalController.class);

    @GetMapping("/all")
    public String getAllGoals(Model model) {
        logger.info("Entering getAllGoals method");
        List<Goal> goals = goalService.getAllGoals();
        logger.info("Retrieved {} goals", goals.size());
        for (Goal goal : goals) {
            logger.info("Goal ID: {}, Title: {}, Number of Tasks: {}", goal.getGoalId(), goal.getTitle(), goal.getTasks().size());
        }
        model.addAttribute("goals", goals);
        return "goal-list";
    }

    @GetMapping("/tasks/{id}")
    public String getTasksForGoal(@PathVariable("id") int id, Model model) {
        logger.info("Entering getTasksForGoal method with goalId: {}", id);
        Goal goal = goalService.getGoalById(id);
        if (goal == null) {
            logger.error("Goal not found with ID: {}", id);
            return "redirect:/goals/all";
        }
        List<Task> tasks = goal.getTasks();
        logger.info("Retrieved {} tasks for goal ID: {}", tasks.size(), id);
        model.addAttribute("tasks", tasks);
        model.addAttribute("goal", goal);
        return "task-list";
    }

    @GetMapping("/new")
    public String createGoalForm(Model model) {
        logger.info("Entering createGoalForm method");
        model.addAttribute("goal", new Goal());
        return "goal-create";
    }

    @PostMapping("/save")
    public String saveGoal(@ModelAttribute("goal") Goal goal) {
        logger.info("Entering saveGoal method");
        logger.info("Saving new goal with title: {}", goal.getTitle());
        goalService.saveGoal(goal);
        return "redirect:/goals/all";
    }

    @GetMapping("/edit/{id}")
    public String editGoalForm(@PathVariable("id") int id, Model model) {
        logger.info("Entering editGoalForm method with goalId: {}", id);
        Goal goal = goalService.getGoalById(id);
        if (goal == null) {
            logger.error("Goal not found with ID: {}", id);
            return "redirect:/goals/all";
        }
        logger.info("Goal found with ID: {}", id);
        model.addAttribute("goal", goal);
        return "goal-update";
    }

    @PostMapping("/update/{id}")
    public String updateGoal(@PathVariable("id") int id, @ModelAttribute("goal") Goal updatedGoal) {
        logger.info("Entering updateGoal method with goalId: {}", id);
        Goal goal = goalService.getGoalById(id);
        if (goal == null) {
            logger.error("Goal not found with ID: {}", id);
            return "redirect:/goals/all";
        }
        logger.info("Updating goal with ID: {}", id);
        goal.setTitle(updatedGoal.getTitle());
        goal.setDetails(updatedGoal.getDetails());
        goal.setTargetDate(updatedGoal.getTargetDate());
        goal.setStatus(updatedGoal.getStatus());
        goal.setTasks(updatedGoal.getTasks());
        goalService.saveGoal(goal);
        return "redirect:/goals/all";
    }

    @GetMapping("/delete/{id}")
    public String deleteGoal(@PathVariable("id") int id) {
        logger.info("Entering deleteGoal method with goalId: {}", id);
        goalService.deleteGoalById(id);
        return "redirect:/goals/all";
    }
}
