package pl.edu.agh.kuce.planner.balance.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.auth.persistence.UserRepository;
import pl.edu.agh.kuce.planner.balance.dto.BalanceDto;
import pl.edu.agh.kuce.planner.balance.persistence.BalanceRepository;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@AutoConfigureMockMvc
public class BalanceServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private UserRepository userRepository;

    private BalanceService balanceService;

    private final Integer balance = 10000;
    private final User user = new User("nick", "mail@mail.mail", "password");

    private final Integer balance2 = 12345;
    private final User user2 = new User("nick2", "mail2@mail.mail", "password2");

    @Test
    void contextLoad() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @Transactional
    void testCreateBalanceByDto() {
        userRepository.save(user);
        userRepository.save(user2);

        balanceService = new BalanceService(balanceRepository);
        assertThatNoException().isThrownBy(
                () -> {
                    balanceService.create(user, balance);
                    balanceService.create(user2, balance2);
                });
    }

    @Test
    @Transactional
    void testListBalanceByDto() {
        userRepository.save(user);
        userRepository.save(user2);
        balanceService = new BalanceService(balanceRepository);

        balanceService.create(user, balance);
        balanceService.create(user2, balance2);

        final BalanceDto response = balanceService.list(user);
        final BalanceDto response2 = balanceService.list(user2);

        assertThat(response.balance()).isEqualTo(balance);
        assertThat(response2.balance()).isEqualTo(balance2);
    }

    @Test
    @Transactional
    void testUpdateByDto() {
        userRepository.save(user);
        userRepository.save(user2);
        balanceService = new BalanceService(balanceRepository);

        balanceService.create(user, balance);
        balanceService.create(user2, balance2);

        BalanceDto response = balanceService.list(user);
        BalanceDto response2 = balanceService.list(user2);

        assertThat(response.balance()).isEqualTo(balance);
        assertThat(response2.balance()).isEqualTo(balance2);

        balanceService.update(user, balance + 123);
        balanceService.update(user2, balance2 + 234);

        response = balanceService.list(user);
        response2 = balanceService.list(user2);

        assertThat(response.balance()).isEqualTo(10123);
        assertThat(response2.balance()).isEqualTo(12579);
    }

    @Test
    @Transactional
    void testRequestEmptyBalance() {
        userRepository.save(user);
        balanceService = new BalanceService(balanceRepository);

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> balanceService.list(user));

        balanceService.update(user, balance);

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> balanceService.list(user));
    }
}
