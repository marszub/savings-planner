package pl.edu.agh.kuce.planner.goal.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public record GoalPriority(
        @NotNull
        Integer id,

        @NotNull
        @PositiveOrZero
        Integer newPriority
) { }
