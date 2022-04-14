package pl.edu.agh.kuce.planner.balance.dto;

import pl.edu.agh.kuce.planner.balance.persistence.Balance;

import javax.validation.constraints.NotNull;

public record BalanceDto(
        @NotNull
        Integer balance) {
    public BalanceDto(final Balance balance) {
        this(balance.getBalance());
    }
}
