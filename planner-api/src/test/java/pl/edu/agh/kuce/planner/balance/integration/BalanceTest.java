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

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class BalanceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private UserRepository userRepository;

    private final User user = new User("name", "email", "password");

    @Test
    void contextLoad() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @Transactional
    void singleAccountBalanceIsProperlySavedInDatabase() {
        userRepository.save(user);
        final Balance testBalance = new Balance(user, 10000);
        balanceRepository.save(testBalance);
        final Balance result = balanceRepository.findByUser(user);
        assertThat(result).isEqualTo(testBalance);
    }

    @Test
    @Transactional
    void multipleAccountBalanceIsProperlySavedInDatabase() {
        final User user2 = new User("nick2", "email2", "password2");
        final User user3 = new User("nick3", "email3", "password3");

        userRepository.save(user);
        userRepository.save(user2);
        userRepository.save(user3);

        final Balance dummyBalance1 = new Balance(user, 10000);
        final Balance dummyBalance2 = new Balance(user2, 20000);
        final Balance dummyBalance3 = new Balance(user3, 30000);

        balanceRepository.save(dummyBalance1);
        balanceRepository.save(dummyBalance2);
        balanceRepository.save(dummyBalance3);

        final Balance result1 = balanceRepository.findByUser(user);
        assertThat(result1).isEqualTo(dummyBalance1);
        final Balance result2 = balanceRepository.findByUser(user2);
        assertThat(result2).isEqualTo(dummyBalance2);
        final Balance result3 = balanceRepository.findByUser(user3);
        assertThat(result3).isEqualTo(dummyBalance3);
    }

    @Test
    @Transactional
    void singleAccountBalanceOverwritenInDatabase() {
        userRepository.save(user);

        final Balance dummyBalance = new Balance(user, 10000);
        final Balance dummyBalance2 = new Balance(user, 10002);

        balanceRepository.save(dummyBalance);

        Balance result = balanceRepository.findByUser(user);

        result.setBalance(dummyBalance2.getBalance());

        balanceRepository.save(result);

        result = balanceRepository.findByUser(user);

        assertThat(result).isEqualTo(dummyBalance2);
    }

    @Test
    @Transactional
    void singleAccountBalanceChange() {
        userRepository.save(user);

        final Balance dummyBalance = new Balance(user, 10000);

        balanceRepository.save(dummyBalance);
        Balance result = balanceRepository.findByUser(user);
        assertThat(result).isEqualTo(dummyBalance);

        balanceRepository.updateBalance(user, 20000);
        result = balanceRepository.findByUser(user);
        assertThat(result).isEqualTo(new Balance(user, 20000));
    }

    @Test
    @Transactional
    void emptyQueryThenAddAndCheck() {
        userRepository.save(user);

        final Balance dummyBalance = new Balance(user, 10000);
        assertThat(balanceRepository.findByUser(user)).isEqualTo(null);

        balanceRepository.save(dummyBalance);
        final Balance result = balanceRepository.findByUser(user);
        assertThat(result).isEqualTo(new Balance(user, 10000));
    }
}
