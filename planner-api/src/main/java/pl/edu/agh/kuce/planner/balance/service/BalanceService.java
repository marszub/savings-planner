package pl.edu.agh.kuce.planner.balance.service;

import org.springframework.stereotype.Service;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.balance.dto.BalanceDto;
import pl.edu.agh.kuce.planner.balance.dto.SingleSubBalanceDto;
import pl.edu.agh.kuce.planner.balance.persistence.Balance;
import pl.edu.agh.kuce.planner.balance.persistence.BalanceRepository;

@Service
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private final SubBalanceService subBalanceService;

    public BalanceService(final BalanceRepository balanceRepository, final SubBalanceService subBalanceService) {
        this.balanceRepository = balanceRepository;
        this.subBalanceService = subBalanceService;
    }

    public void create(final User user, final BalanceDto balanceDto) {
        balanceRepository.save(new Balance(user, balanceDto.balance()));
        subBalanceService.createSingle(user, new SingleSubBalanceDto(balanceDto.balance()));
    }

    public void update(final User user, final BalanceDto newBalanceDto) {
        balanceRepository.updateBalance(user, newBalanceDto.balance());
    }

    public BalanceDto list(final User user) {
        return new BalanceDto(balanceRepository.findByUser(user));
    }
}
