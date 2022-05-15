package pl.edu.agh.kuce.planner.balance.dto;

import pl.edu.agh.kuce.planner.balance.persistence.SubBalance;

import javax.validation.constraints.NotNull;

public record SubBalanceData(
        @NotNull
        Integer id,

        @NotNull
        Integer subBalance) {
    public SubBalanceData(final SubBalance subBalance) {
        this(subBalance.getId(), subBalance.getSubBalance());
    }
}
