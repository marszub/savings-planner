package pl.edu.agh.kuce.planner.balance.dto;

import java.util.List;

public record SubBalanceDto(
    List<SingleSubBalanceDto> subBalanceDtoList
) { }
