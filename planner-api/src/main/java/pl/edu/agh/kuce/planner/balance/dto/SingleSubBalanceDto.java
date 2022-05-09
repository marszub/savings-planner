package pl.edu.agh.kuce.planner.balance.dto;

import pl.edu.agh.kuce.planner.balance.persistence.SubBalance;

import javax.validation.constraints.NotNull;

public record SingleSubBalanceDto(
        @NotNull
        Integer subBalance) {
    public SingleSubBalanceDto(final SubBalance subBalance) {
        this(subBalance.getSubBalance());
    }
}
