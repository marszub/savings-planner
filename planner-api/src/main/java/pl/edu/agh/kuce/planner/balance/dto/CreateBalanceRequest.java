package pl.edu.agh.kuce.planner.balance.dto;

import javax.validation.constraints.NotNull;

public record CreateBalanceRequest (
    @NotNull
    Integer balance
) {}
