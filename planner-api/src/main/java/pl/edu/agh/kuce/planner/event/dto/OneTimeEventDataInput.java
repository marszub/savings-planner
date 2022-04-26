package pl.edu.agh.kuce.planner.event.dto;

import pl.edu.agh.kuce.planner.event.persistence.OneTimeEvent;

import javax.validation.constraints.NotNull;

public record OneTimeEventDataInput(
        @NotNull
        String title,

        @NotNull
        Integer amount,

        @NotNull
        Long timestamp) {
    public OneTimeEventDataInput(final OneTimeEvent oneTimeEvent) {
        this(oneTimeEvent.getTitle(), oneTimeEvent.getAmount(), oneTimeEvent.getTimestamp());
    }
}