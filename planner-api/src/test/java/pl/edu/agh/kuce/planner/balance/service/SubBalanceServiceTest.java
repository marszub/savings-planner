package pl.edu.agh.kuce.planner.balance.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.auth.persistence.UserRepository;
import pl.edu.agh.kuce.planner.balance.dto.BalanceDto;
import pl.edu.agh.kuce.planner.balance.dto.SingleSubBalanceDto;
import pl.edu.agh.kuce.planner.balance.dto.SubBalanceDto;
import pl.edu.agh.kuce.planner.balance.persistence.BalanceRepository;
import pl.edu.agh.kuce.planner.balance.persistence.SubBalanceRepository;

import javax.transaction.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@SpringBootTest
@AutoConfigureMockMvc
public class SubBalanceServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private SubBalanceRepository subBalanceRepository;

    private BalanceService balanceService;
    private SubBalanceService subBalanceService;

    private final BalanceDto balance = new BalanceDto(10000);
    private final SingleSubBalanceDto singleSubBalanceDto = new SingleSubBalanceDto(10000);
    private final List<SingleSubBalanceDto> subBalanceList = List.of(singleSubBalanceDto);
    private final SubBalanceDto subBalanceDto = new SubBalanceDto(subBalanceList);
    private final User user = new User("nick", "mail@mail.mail", "password");

    private final BalanceDto balance2 = new BalanceDto(12345);
    private final SingleSubBalanceDto singleSubBalanceDto2 = new SingleSubBalanceDto(12345);
    private final List<SingleSubBalanceDto> subBalanceList2 = List.of(singleSubBalanceDto2);
    private final SubBalanceDto subBalanceDto2 = new SubBalanceDto(subBalanceList2);
    private final User user2 = new User("nick2", "mail2@mail.mail", "password2");

    @Test
    void contextLoad() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @Transactional
    void testCreateSubBalnanceWhileCreatingBalance() {
        userRepository.save(user);
        userRepository.save(user2);

        balanceService = new BalanceService(balanceRepository, new SubBalanceService(subBalanceRepository));
        subBalanceService = new SubBalanceService(subBalanceRepository);

        assertThatNoException().isThrownBy(
                () -> {
                    balanceService.create(user, balance);
                    balanceService.create(user2, balance2);
                    assertThat(subBalanceService.countSubBalances(user)).isEqualTo(1);
                    assertThat(subBalanceService.countSubBalances(user2)).isEqualTo(1);
                });
    }

    @Test
    @Transactional
    void testListSubBalanceByDto() {
        userRepository.save(user);
        userRepository.save(user2);
        balanceService = new BalanceService(balanceRepository, new SubBalanceService(subBalanceRepository));
        subBalanceService = new SubBalanceService(subBalanceRepository);

        balanceService.create(user, balance);
        balanceService.create(user2, balance2);

        final SubBalanceDto response = subBalanceService.list(user);
        final SubBalanceDto response2 = subBalanceService.list(user2);

        assertThat(response).isEqualTo(subBalanceDto);
        assertThat(response2).isEqualTo(subBalanceDto2);
    }

    @Test
    @Transactional
    void testSingleClearByDto() {
        userRepository.save(user);
        userRepository.save(user2);
        balanceService = new BalanceService(balanceRepository, new SubBalanceService(subBalanceRepository));
        subBalanceService = new SubBalanceService(subBalanceRepository);

        balanceService.create(user, balance);

        final SubBalanceDto response = subBalanceService.list(user);

        assertThat(response).isEqualTo(subBalanceDto);

        subBalanceService.clear(user);

        assertThat(subBalanceService.countSubBalances(user)).isEqualTo(0);
    }

    @Test
    @Transactional
    void testUpdateByDto() {
        userRepository.save(user);
        userRepository.save(user2);
        balanceService = new BalanceService(balanceRepository, new SubBalanceService(subBalanceRepository));
        subBalanceService = new SubBalanceService(subBalanceRepository);

        balanceService.create(user, balance);

        SubBalanceDto response = subBalanceService.list(user);

        assertThat(response).isEqualTo(subBalanceDto);

        subBalanceService.clear(user);
        subBalanceService.create(user, subBalanceDto2);

        response = subBalanceService.list(user);

        assertThat(response).isEqualTo(subBalanceDto2);
    }

    @Test
    @Transactional
    void testSingleCreateAndUpdateByDto() {
        userRepository.save(user);
        userRepository.save(user2);
        balanceService = new BalanceService(balanceRepository, new SubBalanceService(subBalanceRepository));
        subBalanceService = new SubBalanceService(subBalanceRepository);

        balanceService.create(user, balance);

        SubBalanceDto response = subBalanceService.list(user);

        assertThat(response).isEqualTo(subBalanceDto);

        subBalanceService.createSingle(user, singleSubBalanceDto2);

        final Integer indexesCount = subBalanceService.countSubBalances(user);

        assertThat(indexesCount).isEqualTo(2);

        response = subBalanceService.list(user);

        final List<SingleSubBalanceDto> tmpSubBalanceList = List.of(singleSubBalanceDto, singleSubBalanceDto2);
        final SubBalanceDto tmpSubBalanceDto = new SubBalanceDto(tmpSubBalanceList);

        assertThat(response).isEqualTo(tmpSubBalanceDto);

        final SingleSubBalanceDto tmpSingleSubBalance = new SingleSubBalanceDto(0);

        subBalanceService.updateSingle(user, indexesCount - 1, tmpSingleSubBalance);

        response = subBalanceService.list(user);

        assertThat(response).isNotEqualTo(tmpSubBalanceDto);
        assertThat(response.subBalanceDtoList().get(indexesCount - 1)).isNotEqualTo(singleSubBalanceDto2);
        assertThat(response.subBalanceDtoList().get(indexesCount - 1)).isEqualTo(tmpSingleSubBalance);
    }
}
