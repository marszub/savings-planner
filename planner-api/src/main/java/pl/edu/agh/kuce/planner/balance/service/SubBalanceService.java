package pl.edu.agh.kuce.planner.balance.service;

import org.springframework.stereotype.Service;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.balance.dto.SingleSubBalanceDto;
import pl.edu.agh.kuce.planner.balance.dto.SubBalanceDto;
import pl.edu.agh.kuce.planner.balance.persistence.SubBalance;
import pl.edu.agh.kuce.planner.balance.persistence.SubBalanceRepository;

import java.util.List;

@Service
public class SubBalanceService {
    private final SubBalanceRepository subBalanceRepository;

    public SubBalanceService(final SubBalanceRepository subBalanceRepository) {
        this.subBalanceRepository = subBalanceRepository;
    }

    public void create(final User user, final SubBalanceDto subBalanceDto) {
        for (SingleSubBalanceDto singleSubBalanceDto : subBalanceDto.subBalanceDtoList()) {
            subBalanceRepository.save(new SubBalance(user, singleSubBalanceDto.subBalance()));
        }
    }

    public SubBalanceDto list(final User user) {
        return new SubBalanceDto(
                subBalanceRepository
                        .findByUser(user)
                        .stream()
                        .map(SingleSubBalanceDto::new)
                        .toList());
    }

    public void createSingle(final User user, final SingleSubBalanceDto singleSubBalanceDto) {
        subBalanceRepository.save(new SubBalance(user, singleSubBalanceDto.subBalance()));
    }

    public void clear(final User user) {
        subBalanceRepository.deleteByUser(user);
    }

    public Integer countSubBalances(final User user) {
        return subBalanceRepository.findIdsOfSubBalancesByUser(user).size();
    }

    public void updateSingle(
            final User user,
            final Integer index,
            final SingleSubBalanceDto singleSubBalanceDto) {
        final List<Integer> subBalancesIndexes = subBalanceRepository.findIdsOfSubBalancesByUser(user);
        if (index >= 0 && index < subBalancesIndexes.size()) {
            subBalanceRepository.updateSubBalanceById(
                    user,
                    subBalancesIndexes.get(index),
                    singleSubBalanceDto.subBalance());
        }
    }
}
