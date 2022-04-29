package pl.edu.agh.kuce.planner.goal.dto;

import pl.edu.agh.kuce.planner.goal.persistence.Goal;

import javax.validation.constraints.NotNull;

public record GoalInputData(
        @NotNull
        String title,

        @NotNull
        Integer amount,

        @NotNull
        Integer priority
) {
    public GoalInputData(final Goal goal) {
        this(goal.getTitle(), goal.getAmount(), goal.getPriority());
    }
}

