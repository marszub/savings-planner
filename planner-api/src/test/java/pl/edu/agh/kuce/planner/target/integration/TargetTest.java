package pl.edu.agh.kuce.planner.target.integration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.auth.persistence.UserRepository;
import pl.edu.agh.kuce.planner.target.persistence.Target;
import pl.edu.agh.kuce.planner.target.persistence.TargetRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class TargetTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TargetRepository targetRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @Transactional
    void single_one_time_event_is_properly_saved_in_database() {
        User user = new User("TEST", "TEST", "TEST");
        userRepository.save(user);
        Target testTarget = new Target(user, "test", 11);
        targetRepository.save(testTarget);
        List<Target> result = targetRepository.findByUser(user);
        assertThat(result.get(0)).isEqualTo(testTarget);
    }

    @Test
    @Transactional
    void multiple_one_time_events_are_saved_properly_in_database() {
        User user = new User("TEST", "TEST", "TEST");
        userRepository.save(user);
        Target testTarget1 = new Target(user, "test", 11);
        Target testTarget2 = new Target(user, "test", 112);
        targetRepository.save(testTarget1);
        targetRepository.save(testTarget2);
        List<Target> result = targetRepository.findByUser(user);
        assertThat(result.get(0)).isEqualTo(testTarget1);
        assertThat(result.get(1)).isEqualTo(testTarget2);
    }
}
