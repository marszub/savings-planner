package pl.edu.agh.kuce.planner.balance.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.balance.dto.BalanceData;
import pl.edu.agh.kuce.planner.balance.dto.SubBalanceData;
import pl.edu.agh.kuce.planner.balance.dto.SubBalanceInputData;
import pl.edu.agh.kuce.planner.balance.persistence.Balance;
import pl.edu.agh.kuce.planner.balance.persistence.BalanceRepository;
import pl.edu.agh.kuce.planner.balance.persistence.SubBalance;
import pl.edu.agh.kuce.planner.balance.persistence.SubBalanceRepository;
import pl.edu.agh.kuce.planner.shared.ResourceNotFoundException;

import java.util.Optional;

@Service
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private final SubBalanceRepository subBalanceRepository;

    public BalanceService(final BalanceRepository balanceRepository, final SubBalanceRepository subBalanceRepository) {
        this.balanceRepository = balanceRepository;
        this.subBalanceRepository = subBalanceRepository;
    }

    public void create(final User user, final BalanceData balanceData) {
        balanceRepository.save(new Balance(user, balanceData.balance()));
    }

    @Transactional
    public void update(final User user, final BalanceData balanceData) {
        balanceRepository.updateBalance(user, balanceData.balance());
    }

    public BalanceData list(final User user) {
        return new BalanceData(balanceRepository.findByUser(user));
    }


    //SUBBALANCES

    public SubBalanceData createSub(final User user, final SubBalanceInputData subBalanceInputData) {
        final SubBalance subBalance = subBalanceRepository.save(new SubBalance(user, subBalanceInputData.subBalance()));
        return new SubBalanceData(subBalance);
    }

    @Transactional
    public void updateSub(final User user,
                          final Integer id,
                          final SubBalanceInputData subBalanceInputData) throws ResourceNotFoundException {
        subBalanceRepository.updateSubBalanceByUserAndId(user, id, subBalanceInputData.subBalance());
    }

    @Transactional
    public void deleteSub(final User user) {
        subBalanceRepository.deleteByUser(user);
    }

    public SubBalanceData listSub(final User user, final Integer id) {
        final Optional<SubBalance> subBalance = subBalanceRepository.findByIdAndUser(id, user);
        if (subBalance.isPresent()) {
            return new SubBalanceData(subBalance.get());
        }
        throw new BalanceNotFoundException();
    }
}
