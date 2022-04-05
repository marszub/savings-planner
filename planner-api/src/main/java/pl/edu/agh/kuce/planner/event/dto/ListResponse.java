package pl.edu.agh.kuce.planner.event.dto;

import pl.edu.agh.kuce.planner.event.persistence.OneTimeEvent;

import java.util.List;

public record ListResponse (
        List<OneTimeEvent> list
){}
