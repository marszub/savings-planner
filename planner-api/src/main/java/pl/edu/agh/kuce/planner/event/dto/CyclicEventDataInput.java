package pl.edu.agh.kuce.planner.event.dto;

import pl.edu.agh.kuce.planner.event.persistence.CyclicEvent;

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
        Integer cycleLength) {

    public CyclicEventDataInput(final CyclicEvent cyclicEvent) {
        this(
                cyclicEvent.getTitle(),
                cyclicEvent.getAmount(),
                cyclicEvent.getBegin(),
                cyclicEvent.getCycleBase(),
                cyclicEvent.getCycleLength()
        );
    }
}
