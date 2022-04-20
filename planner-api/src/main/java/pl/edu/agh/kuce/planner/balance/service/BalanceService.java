package pl.edu.agh.kuce.planner.balance.service;

import org.springframework.stereotype.Service;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.balance.dto.BalanceDto;
import pl.edu.agh.kuce.planner.balance.persistence.Balance;
import pl.edu.agh.kuce.planner.balance.persistence.BalanceRepository;

@Service
public class BalanceService {
    private final BalanceRepository balanceRepository;

    public BalanceService(final BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    public void create(final User user, final Integer balance) {
        balanceRepository.save(new Balance(user, balance));
    }

    public void update(final User user, final BalanceDto newBalanceDto) {
        balanceRepository.updateBalance(user, newBalanceDto.balance());
    }

    public BalanceDto list(final User user) {
        return new BalanceDto(balanceRepository.findByUser(user));
    }
}
