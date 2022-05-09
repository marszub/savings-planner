package pl.edu.agh.kuce.planner.goal.dto;

import javax.validation.constraints.NotNull;

public record SubGoalInputData(
        @NotNull
        String title
) { }


