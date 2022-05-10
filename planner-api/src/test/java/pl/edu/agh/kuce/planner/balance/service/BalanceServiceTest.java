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
    private UserRepository userRepository;

    private BalanceService balanceService;

    private final BalanceDto balance = new BalanceDto(10000);
    private final User user = new User("nick", "mail@mail.mail", "password");

    private final BalanceDto balance2 = new BalanceDto(12345);
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

        assertThat(response).isEqualTo(new BalanceDto(balance.balance()));
        assertThat(response2).isEqualTo(new BalanceDto(balance2.balance()));
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

        assertThat(response).isEqualTo(new BalanceDto(balance.balance()));
        assertThat(response2).isEqualTo(new BalanceDto(balance2.balance()));

        final BalanceDto request = new BalanceDto(balance.balance() + 123);
        final BalanceDto request2 = new BalanceDto(balance2.balance() + 234);

        balanceService.update(user, request);
        balanceService.update(user2, request2);

        response = balanceService.list(user);
        response2 = balanceService.list(user2);

        assertThat(response).isEqualTo(new BalanceDto(10123));
        assertThat(response2).isEqualTo(new BalanceDto(12579));
    }

    @Test
    @Transactional
    void testRequestEmptyBalance() {
        userRepository.save(user);
        balanceService = new BalanceService(balanceRepository);

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> balanceService.list(user));

        final BalanceDto request = new BalanceDto(balance.balance());

        balanceService.update(user, request);

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> balanceService.list(user));
    }
}
