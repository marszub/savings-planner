package pl.edu.agh.kuce.planner.target.integration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.auth.persistence.UserRepository;
import pl.edu.agh.kuce.planner.goal.persistence.Goal;
import pl.edu.agh.kuce.planner.goal.persistence.GoalRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class GoalTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GoalRepository targetRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @Transactional
    void single_one_time_event_is_properly_saved_in_database() {
        final User user = new User("TEST", "TEST", "TEST");
        userRepository.save(user);
        final Goal testGoal = new Goal(user, "test", 11);
        targetRepository.save(testGoal);
        final List<Goal> result = targetRepository.findByUser(user);
        assertThat(result.get(0)).isEqualTo(testGoal);
    }

    @Test
    @Transactional
    void multiple_one_time_events_are_saved_properly_in_database() {
        final User user = new User("TEST", "TEST", "TEST");
        userRepository.save(user);
        final Goal testGoal1 = new Goal(user, "test", 11);
        final Goal testGoal2 = new Goal(user, "test", 112);
        targetRepository.save(testGoal1);
        targetRepository.save(testGoal2);
        final List<Goal> result = targetRepository.findByUser(user);
        assertThat(result.get(0)).isEqualTo(testGoal1);
        assertThat(result.get(1)).isEqualTo(testGoal2);
    }
}
