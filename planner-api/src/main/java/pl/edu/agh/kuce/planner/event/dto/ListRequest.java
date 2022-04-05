package pl.edu.agh.kuce.planner.event.dto;

import javax.validation.constraints.NotBlank;

public record ListRequest (
        @NotBlank String token
){ }
