package pl.edu.agh.kuce.planner.balance.dto;

import pl.edu.agh.kuce.planner.balance.persistence.Balance;

import javax.validation.constraints.NotNull;

public record BalanceData(
        @NotNull
        Integer balance) {
    public BalanceData(final Balance balance) {
        this(balance.getBalance());
    }
}
