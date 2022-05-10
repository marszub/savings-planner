package pl.edu.agh.kuce.planner.goal.dto;

import pl.edu.agh.kuce.planner.goal.persistence.SubGoal;

import javax.validation.constraints.NotNull;

public record SubGoalData(
        @NotNull
        Integer id,

        @NotNull
        String title
) {
    public SubGoalData(final SubGoal subGoal) {
        this(subGoal.getId(), subGoal.getTitle());
    }
}
