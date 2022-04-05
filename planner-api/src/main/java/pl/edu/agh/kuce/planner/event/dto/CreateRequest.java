package pl.edu.agh.kuce.planner.event.dto;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

public record CreateRequest(
        @NotBlank
        String token,

        @NotBlank
        String title,

        @NotNull
        Double amount,

        @NotNull
        Timestamp timestamp
) {}
