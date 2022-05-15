package pl.edu.agh.kuce.planner.event.dto;

import java.util.List;

public record EventList(
        List<EventData> events
) { }
