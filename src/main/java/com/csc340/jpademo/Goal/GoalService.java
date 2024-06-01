package com.csc340.jpademo.Goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalService {

    @Autowired
    private GoalRepository goalRepository;

    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    public Goal getGoalById(int id) {
        return goalRepository.findById(id).orElse(null);
    }

    public void saveGoal(Goal goal) {
        goalRepository.save(goal);
    }

    public void deleteGoalById(int id) {
        goalRepository.deleteById(id);
    }
}
