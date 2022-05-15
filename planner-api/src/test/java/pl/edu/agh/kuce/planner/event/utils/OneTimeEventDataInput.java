package pl.edu.agh.kuce.planner.event.utils;

import pl.edu.agh.kuce.planner.event.dto.EventDataInput;

import javax.validation.constraints.NotNull;

public record OneTimeEventDataInput(
        @NotNull
        String title,

        @NotNull
        Integer amount,

        @NotNull
        Long timestamp) {

    public EventDataInput getEventDataInput() {
        return new EventDataInput(
                title,
                amount,
                Boolean.FALSE,
                timestamp,
                null,
                null,
                null);
    }
}
