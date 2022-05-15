package pl.edu.agh.kuce.planner.balance.dto;

import pl.edu.agh.kuce.planner.balance.persistence.SubBalance;

import javax.validation.constraints.NotNull;

public record SubBalanceInputData(
        @NotNull
        Integer subBalance) {
    public SubBalanceInputData(final SubBalance subBalance) {
        this(subBalance.getSubBalance());
    }
}
