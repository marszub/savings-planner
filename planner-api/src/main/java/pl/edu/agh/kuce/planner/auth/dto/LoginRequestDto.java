package pl.edu.agh.kuce.planner.auth.dto;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record LoginRequestDto(

    @NotBlank
    String login,

    @NotNull
    @Length(min = 7, max = 100)
    String password
) {}
