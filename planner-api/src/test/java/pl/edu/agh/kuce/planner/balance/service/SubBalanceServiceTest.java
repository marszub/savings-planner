package pl.edu.agh.kuce.planner.balance.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.auth.persistence.UserRepository;
import pl.edu.agh.kuce.planner.balance.dto.SubBalanceData;
import pl.edu.agh.kuce.planner.balance.dto.SubBalanceInputData;
import pl.edu.agh.kuce.planner.balance.persistence.BalanceRepository;
import pl.edu.agh.kuce.planner.balance.persistence.SubBalanceRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

@SpringBootTest
@AutoConfigureMockMvc
public class SubBalanceServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private SubBalanceRepository subBalanceRepository;

    @Autowired
    private UserRepository userRepository;

    private BalanceService balanceService;

    private final SubBalanceInputData subBalance11 = new SubBalanceInputData(5000);
    private final SubBalanceInputData subBalance12 = new SubBalanceInputData(5000);
    private final User user = new User("nick", "mail@mail.mail", "password");

    private final SubBalanceInputData subBalance22 = new SubBalanceInputData(12345);
    private final User user2 = new User("nick2", "mail2@mail.mail", "password2");

    @Test
    void contextLoad() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @Transactional
    void testCreateSubBalanceByDto() {
        userRepository.save(user);
        userRepository.save(user2);
        balanceService = new BalanceService(balanceRepository, subBalanceRepository);

        assertThatNoException().isThrownBy(
                () -> {
                    balanceService.createSub(user, subBalance11);
                    balanceService.createSub(user, subBalance12);

                    balanceService.createSub(user2, subBalance22);
                });
    }

    @Test
    @Transactional
    void testListSubBalanceByDto() {
        userRepository.save(user);
        userRepository.save(user2);
        balanceService = new BalanceService(balanceRepository, subBalanceRepository);

        final SubBalanceData response = balanceService.createSub(user, subBalance11);
        final SubBalanceData response2 = balanceService.createSub(user, subBalance12);
        final SubBalanceData response3 = balanceService.createSub(user2, subBalance22);

        final SubBalanceData data = balanceService.listSub(user, response.id());
        final SubBalanceData data2 = balanceService.listSub(user, response2.id());
        final SubBalanceData data3 = balanceService.listSub(user2, response3.id());

        assertThat(data.subBalance()).isEqualTo(subBalance11.subBalance());
        assertThat(data2.subBalance()).isEqualTo(subBalance12.subBalance());
        assertThat(data3.subBalance()).isEqualTo(subBalance22.subBalance());
    }

    @Test
    @Transactional
    void testUpdateSubBalanceByDto() {
        userRepository.save(user);
        userRepository.save(user2);
        balanceService = new BalanceService(balanceRepository, subBalanceRepository);

        final SubBalanceData response = balanceService.createSub(user, subBalance11);
        final SubBalanceData response2 = balanceService.createSub(user, subBalance12);
        final SubBalanceData response3 = balanceService.createSub(user2, subBalance22);

        assertThat(response.subBalance()).isEqualTo(subBalance11.subBalance());
        assertThat(response2.subBalance()).isEqualTo(subBalance12.subBalance());
        assertThat(response3.subBalance()).isEqualTo(subBalance22.subBalance());

        balanceService.updateSub(user, response.id(), subBalance22);
        balanceService.updateSub(user, response2.id(), subBalance22);
        balanceService.updateSub(user2, response3.id(), subBalance11);

        final SubBalanceData data = balanceService.listSub(user, response.id());
        final SubBalanceData data2 = balanceService.listSub(user, response2.id());
        final SubBalanceData data3 = balanceService.listSub(user2, response3.id());

        assertThat(data.subBalance()).isEqualTo(subBalance22.subBalance());
        assertThat(data2.subBalance()).isEqualTo(subBalance22.subBalance());
        assertThat(data3.subBalance()).isEqualTo(subBalance11.subBalance());
    }

    @Test
    @Transactional
    void testRequestEmptySubBalance() {
        userRepository.save(user);
        balanceService = new BalanceService(balanceRepository, subBalanceRepository);

        assertThatExceptionOfType(BalanceNotFoundException.class).isThrownBy(() -> balanceService.listSub(user, 0));
    }

    @Test
    @Transactional
    void testDeleteAllSubBalances() {
        userRepository.save(user);
        userRepository.save(user2);
        balanceService = new BalanceService(balanceRepository, subBalanceRepository);

        final SubBalanceData response = balanceService.createSub(user, subBalance11);
        final SubBalanceData response2 = balanceService.createSub(user, subBalance12);
        final SubBalanceData response3 = balanceService.createSub(user2, subBalance22);

        assertThat(response.subBalance()).isEqualTo(subBalance11.subBalance());
        assertThat(response2.subBalance()).isEqualTo(subBalance12.subBalance());
        assertThat(response3.subBalance()).isEqualTo(subBalance22.subBalance());

        balanceService.deleteSub(user);

        assertThatExceptionOfType(BalanceNotFoundException.class).isThrownBy(() ->
                balanceService.listSub(user, response.id()));
        assertThatExceptionOfType(BalanceNotFoundException.class).isThrownBy(() ->
                balanceService.listSub(user, response2.id()));

        final SubBalanceData data3 = balanceService.listSub(user2, response3.id());
        assertThat(response3.subBalance()).isEqualTo(data3.subBalance());
    }
}
