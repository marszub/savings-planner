package pl.edu.agh.kuce.planner.event.utils;

import pl.edu.agh.kuce.planner.event.dto.EventDataInput;

import javax.validation.constraints.NotNull;

public record CyclicEventDataInput(
        @NotNull
        String title,

        @NotNull
        Integer amount,

        @NotNull
        Long begin,

        @NotNull
        Integer cycleBase,

        @NotNull
        Integer cycleLength,

        @NotNull
        Integer cycleCount) {

    public EventDataInput getEventDataInput() {
        return new EventDataInput(
                title,
                amount,
                Boolean.TRUE,
                null,
                begin,
                cycleBase,
                cycleLength,
                cycleCount);
    }
}
