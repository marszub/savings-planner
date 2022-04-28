package pl.edu.agh.kuce.planner.goal.dto;

import pl.edu.agh.kuce.planner.goal.persistence.Goal;

import javax.validation.constraints.NotNull;

public record GoalData(
        @NotNull
        Integer id,

        @NotNull
        String title,

        @NotNull
        Integer amount,

        @NotNull
        Integer priority
) {
    public GoalData(final Goal goal) {
        this(goal.getId(), goal.getTitle(), goal.getAmount(), goal.getPriority());
    }
}
