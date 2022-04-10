package pl.edu.agh.kuce.planner.balance.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.agh.kuce.planner.balance.persistence.Balance;
import pl.edu.agh.kuce.planner.balance.persistence.BalanceRepository;

import javax.transaction.Transactional;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class BalanceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BalanceRepository balanceRepository;

    @Test
    void contextLoad() { assertThat(mockMvc).isNotNull(); }

    @Test
    @Transactional
    void singleAccountBalanceIsProperlySavedInDatabase() {
        Balance testBalance = new Balance(1, 10000);
        balanceRepository.save(testBalance);
        Collection<Balance> result = balanceRepository.findByUserId(1);
        assertThat(result.toArray()[0]).isEqualTo(testBalance);
    }

    @Test
    @Transactional
    void multipleAccountBalanceIsProperlySavedInDatabase() {
        Balance dummyBalance1 = new Balance(1, 10000);
        Balance dummyBalance2 = new Balance(2, 20000);
        Balance dummyBalance3 = new Balance(3, 30000);

        balanceRepository.save(dummyBalance1);
        balanceRepository.save(dummyBalance2);
        balanceRepository.save(dummyBalance3);

        Collection<Balance> result1 = balanceRepository.findByUserId(1);
        assertThat(result1.toArray()[0]).isEqualTo(dummyBalance1);
        Collection<Balance> result2 = balanceRepository.findByUserId(2);
        assertThat(result2.toArray()[0]).isEqualTo(dummyBalance2);
        Collection<Balance> result3 = balanceRepository.findByUserId(3);
        assertThat(result3.toArray()[0]).isEqualTo(dummyBalance3);
    }

    @Test
    @Transactional
    void singleAccountBalanceOverwritenInDatabase() {
        Balance dummyBalance = new Balance(1, 10000);
        Balance dummyBalance2 = new Balance(1, 10002);
        balanceRepository.save(dummyBalance);
        balanceRepository.save(dummyBalance2);
        Collection<Balance> result = balanceRepository.findByUserId(1);
        assertThat(result.toArray()[0]).isEqualTo(dummyBalance2);
        assertThat(result.toArray()[0]).isNotEqualTo(dummyBalance);
    }

    @Test
    @Transactional
    void emptyQueryThenAddAndCheck(){
        Balance dummyBalance = new Balance(1, 10000);
        assertThat(balanceRepository.findByUserId(1).isEmpty()).isEqualTo(true);
        balanceRepository.save(dummyBalance);
        Collection<Balance> result = balanceRepository.findByUserId(1);
        assertThat(result.toArray()[0]).isEqualTo(dummyBalance);
    }
}
