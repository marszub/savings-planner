package pl.edu.agh.kuce.planner.goal.dto;

import pl.edu.agh.kuce.planner.goal.persistence.Goal;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public record GoalPriorityUpdate(
        List<GoalPriority> newPriorities
) {
}
