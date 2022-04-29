package pl.edu.agh.kuce.planner.goal.dto;

import java.util.List;

public record GoalPriorityUpdate(
        List<GoalPriority> newPriorities
) {
}
