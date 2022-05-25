package pl.edu.agh.kuce.planner.event.dto;

import javax.validation.constraints.NotNull;
import java.util.List;

public record EventTimestampList(@NotNull List<EventTimestamp> eventTimestamps) { }
