package pl.edu.agh.kuce.planner.event.dto;

import javax.validation.constraints.NotNull;

public record EventDataInput(
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
        Long cycleEnd) {

    public boolean isValid() {
        if (isCyclic()) {
            return begin != null && cycleBase != null && cycleLength != null && cycleEnd != null;
        }
        return timestamp != null;
    }
}
