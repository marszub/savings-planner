package pl.edu.agh.kuce.planner.goal.dto;

import pl.edu.agh.kuce.planner.goal.persistence.Goal;
import pl.edu.agh.kuce.planner.goal.persistence.SubGoal;

import javax.validation.constraints.NotNull;
import java.util.List;

public record GoalData(
        @NotNull
        Integer id,

        @NotNull
        String title,

        @NotNull
        Integer amount,

        @NotNull
        Integer priority,

        @NotNull
        List<SubGoalData> subGoals
) {
    public GoalData(final Goal goal, final List<SubGoalData> subGoals) {
        this(goal.getId(), goal.getTitle(), goal.getAmount(), goal.getPriority(), subGoals);
    }
}
