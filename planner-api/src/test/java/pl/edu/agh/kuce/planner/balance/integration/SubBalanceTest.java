package pl.edu.agh.kuce.planner.balance.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.auth.persistence.UserRepository;
import pl.edu.agh.kuce.planner.balance.persistence.Balance;
import pl.edu.agh.kuce.planner.balance.persistence.BalanceRepository;
import pl.edu.agh.kuce.planner.balance.persistence.SubBalance;
import pl.edu.agh.kuce.planner.balance.persistence.SubBalanceRepository;

import javax.transaction.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class SubBalanceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private SubBalanceRepository subBalanceRepository;

    @Autowired
    private UserRepository userRepository;

    private final User user = new User("name", "email", "password");
    private final Balance balance = new Balance(user, 10000);

    @Test
    void contextLoad() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @Transactional
    void singleAccountSubBalanceIsProperlySavedInDatabase() {
        userRepository.save(user);
        balanceRepository.save(balance);

        final SubBalance subBalance = new SubBalance(user, balance.getBalance());
        subBalanceRepository.save(subBalance);

        final List<SubBalance> result = subBalanceRepository.findByUser(user);
        assertThat(result.get(0)).isEqualTo(new SubBalance(user, balance.getBalance()));
    }

    @Test
    @Transactional
    void multiSubBalanceForSingleBalanceSavedInDataBase() {
        userRepository.save(user);
        balanceRepository.save(balance);

        final SubBalance subBalance = new SubBalance(user, balance.getBalance());
        subBalanceRepository.save(subBalance);

        final SubBalance subBalance2 = new SubBalance(user, balance.getBalance() + 1);
        subBalanceRepository.save(subBalance2);

        final SubBalance subBalance3 = new SubBalance(user, balance.getBalance() + 2);
        subBalanceRepository.save(subBalance3);

        final List<SubBalance> result = subBalanceRepository.findByUser(user);
        assertThat(result.get(0)).isEqualTo(subBalance);
        assertThat(result.get(1)).isEqualTo(subBalance2);
        assertThat(result.get(2)).isEqualTo(subBalance3);
    }

    @Test
    @Transactional
    void singleAccountSubBalanceOverwritenInDatabase() {
        userRepository.save(user);
        balanceRepository.save(balance);

        final SubBalance subBalance = new SubBalance(user, balance.getBalance());
        subBalanceRepository.save(subBalance);

        final SubBalance subBalance2 = new SubBalance(user, balance.getBalance() + 1);
        subBalanceRepository.save(subBalance2);

        final List<SubBalance> result = subBalanceRepository.findByUser(user);
        assertThat(result.get(0)).isEqualTo(subBalance);
        assertThat(result.get(1)).isEqualTo(subBalance2);

        final List<Integer> ids = subBalanceRepository.findIdsOfSubBalancesByUser(user);
        System.out.println(ids);

        final SubBalance subBalance3 = new SubBalance(user, balance.getBalance() * 2);
        subBalanceRepository.updateSubBalanceById(user, ids.get(1), subBalance3.getSubBalance());

        final List<SubBalance> result2 = subBalanceRepository.findByUser(user);
        assertThat(result2.get(0)).isEqualTo(subBalance);
        assertThat(result2.get(1)).isEqualTo(subBalance3);
    }

    @Test
    @Transactional
    void clearUserSubBalancesInDatabase() {
        userRepository.save(user);
        balanceRepository.save(balance);

        final User user2 = new User("name2", "email2", "password2");
        final Balance balance2 = new Balance(user, 20002);

        userRepository.save(user2);
        balanceRepository.save(balance2);

        final SubBalance subBalance = new SubBalance(user, balance.getBalance());
        subBalanceRepository.save(subBalance);

        final SubBalance subBalance2 = new SubBalance(user, balance.getBalance() + 1);
        subBalanceRepository.save(subBalance2);

        final SubBalance subBalance3 = new SubBalance(user2, balance2.getBalance());
        subBalanceRepository.save(subBalance3);

        List<SubBalance> result = subBalanceRepository.findByUser(user);
        assertThat(result.size()).isEqualTo(2);

        List<SubBalance> result2 = subBalanceRepository.findByUser(user2);
        assertThat(result2.size()).isEqualTo(1);

        subBalanceRepository.deleteByUser(user);

        result = subBalanceRepository.findByUser(user);
        assertThat(result.size()).isEqualTo(0);

        result2 = subBalanceRepository.findByUser(user2);
        assertThat(result2.size()).isEqualTo(1);
    }
}
