package pl.edu.agh.kuce.planner.balance.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.auth.persistence.UserRepository;
import pl.edu.agh.kuce.planner.balance.dto.BalanceData;
import pl.edu.agh.kuce.planner.balance.persistence.BalanceRepository;
import pl.edu.agh.kuce.planner.balance.persistence.SubBalanceRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

@SpringBootTest
@AutoConfigureMockMvc
public class BalanceServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private SubBalanceRepository subBalanceRepository;

    @Autowired
    private UserRepository userRepository;

    private BalanceService balanceService;

    private final BalanceData balance = new BalanceData(10000);
    private final User user = new User("nick", "mail@mail.mail", "password");

    private final BalanceData balance2 = new BalanceData(12345);
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

        balanceService = new BalanceService(balanceRepository, subBalanceRepository);
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
        balanceService = new BalanceService(balanceRepository, subBalanceRepository);

        balanceService.create(user, balance);
        balanceService.create(user2, balance2);

        final BalanceData response = balanceService.list(user);
        final BalanceData response2 = balanceService.list(user2);

        assertThat(response).isEqualTo(new BalanceData(balance.balance()));
        assertThat(response2).isEqualTo(new BalanceData(balance2.balance()));
    }

    @Test
    @Transactional
    void testUpdateByDto() {
        userRepository.save(user);
        userRepository.save(user2);
        balanceService = new BalanceService(balanceRepository, subBalanceRepository);

        balanceService.create(user, balance);
        balanceService.create(user2, balance2);

        BalanceData response = balanceService.list(user);
        BalanceData response2 = balanceService.list(user2);

        assertThat(response).isEqualTo(new BalanceData(balance.balance()));
        assertThat(response2).isEqualTo(new BalanceData(balance2.balance()));

        final BalanceData request = new BalanceData(balance.balance() + 123);
        final BalanceData request2 = new BalanceData(balance2.balance() + 234);

        balanceService.update(user, request);
        balanceService.update(user2, request2);

        response = balanceService.list(user);
        response2 = balanceService.list(user2);

        assertThat(response).isEqualTo(new BalanceData(10123));
        assertThat(response2).isEqualTo(new BalanceData(12579));
    }

    @Test
    @Transactional
    void testRequestEmptyBalance() {
        userRepository.save(user);
        balanceService = new BalanceService(balanceRepository, subBalanceRepository);

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> balanceService.list(user));

        final BalanceData request = new BalanceData(balance.balance());

        balanceService.update(user, request);

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> balanceService.list(user));
    }
}
