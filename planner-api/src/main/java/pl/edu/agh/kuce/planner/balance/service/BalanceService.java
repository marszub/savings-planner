package pl.edu.agh.kuce.planner.balance.service;

import org.springframework.stereotype.Service;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.balance.persistence.Balance;
import pl.edu.agh.kuce.planner.balance.persistence.BalanceRepository;

import java.util.List;

@Service
public class BalanceService {
    private final BalanceRepository balanceRepository;

    public BalanceService(final BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    public void create(final User user, final Integer balance) {
        balanceRepository.save(new Balance(user, balance));
    }

    public void update(final User user, final Integer newBalance) {
        balanceRepository.updateBalance(user, newBalance);
    }

    public List<Balance> list(final User user) {
        return balanceRepository.findByUser(user);
    }
}
