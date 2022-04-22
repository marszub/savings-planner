package pl.edu.agh.kuce.planner.event.dto;

import pl.edu.agh.kuce.planner.event.persistence.OneTimeEvent;

import javax.validation.constraints.NotNull;

public record OneTimeEventData(
        @NotNull
        Integer id,

        @NotNull
        String title,

        @NotNull
        Integer amount,

        @NotNull
        String timestamp) {
    public OneTimeEventData(final OneTimeEvent oneTimeEvent) {
        this(oneTimeEvent.getId(), oneTimeEvent.getTitle(),
                oneTimeEvent.getAmount(), oneTimeEvent.getTimestamp().toString());
    }
}
