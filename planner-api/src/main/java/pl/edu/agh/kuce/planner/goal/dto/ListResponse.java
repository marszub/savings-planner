package pl.edu.agh.kuce.planner.goal.dto;

import java.util.List;

public record ListResponse(
    List<GoalData> list
) { }
