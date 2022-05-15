package pl.edu.agh.kuce.planner.event.dto;

import javax.validation.constraints.NotNull;

public record TimestampListRequest(@NotNull Long start, @NotNull Integer eventsNum) {
}
