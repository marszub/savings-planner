package pl.edu.agh.kuce.planner.event.dto;

import pl.edu.agh.kuce.planner.event.persistence.CyclicEvent;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEvent;

import javax.validation.constraints.NotNull;

public record EventData(
        @NotNull
        Integer id,

        @NotNull
        String title,

        @NotNull
        Integer amount,

        @NotNull
        Boolean isCyclic,

        Long timestamp,

        Long begin,
        Integer cycleBase,
        Integer cycleLength,
        Integer cycleCount) {
    public EventData(final CyclicEvent cyclicEvent) {
        this(
                cyclicEvent.getId(),
                cyclicEvent.getTitle(),
                cyclicEvent.getAmount(),
                Boolean.TRUE,
                null,
                cyclicEvent.getBegin(),
                cyclicEvent.getCycleBase(),
                cyclicEvent.getCycleLength(),
                cyclicEvent.getCycleCount());
    }

    public EventData(final OneTimeEvent oneTimeEvent) {
        this(
                oneTimeEvent.getId(),
                oneTimeEvent.getTitle(),
                oneTimeEvent.getAmount(),
                Boolean.FALSE,
                oneTimeEvent.getTimestamp(),
                null,
                null,
                null,
                null);
    }

    public boolean isValid() {
        if (isCyclic()) {
            return begin != null && cycleBase != null && cycleLength != null;
        }
        return timestamp != null;
    }
}
