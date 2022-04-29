package pl.edu.agh.kuce.planner.goal.integration;
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
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @Transactional
    void singleGoalIsProperlySavedInDatabase() {
        final User user = new User("TEST", "TEST", "TEST");
        userRepository.save(user);
        final Goal testGoal = new Goal(user, "test", 11, 1);
        goalRepository.save(testGoal);

        final List<Goal> result = goalRepository.findByUserOrderByPriorityDesc(user);

        assertThat(result.get(0)).isEqualTo(testGoal);
    }

    @Test
    @Transactional
    void multipleGoalsAreSavedProperlyInDatabase() {
        final User user = new User("TEST", "TEST", "TEST");
        userRepository.save(user);
        final List<Goal> testGoals = List.of(
                new Goal(user, "test", 11, 1),
                new Goal(user, "test", 112, 5),
                new Goal(user, "test", 112, 2)
        );
        goalRepository.saveAll(testGoals);

        final List<Goal> result = goalRepository.findByUserOrderByPriorityDesc(user);

        assertThat(result).containsAll(testGoals);
    }

    @Test
    @Transactional
    void multipleGoalsAreReturnedInOrder() {
        final User user = new User("TEST", "TEST", "TEST");
        userRepository.save(user);
        final List<Goal> testGoals = List.of(
                new Goal(user, "test", 11, 1),
                new Goal(user, "test", 112, 5),
                new Goal(user, "test", 112, 2)
        );
        goalRepository.saveAll(testGoals);

        final List<Goal> result = goalRepository.findByUserOrderByPriorityDesc(user);

        assertThat(result).containsExactly(testGoals.get(1), testGoals.get(2), testGoals.get(0));
    }

    @Test
    @Transactional
    void goalIsProperlyDeletedFromDatabase() {
        final User user = new User("TEST", "TEST", "TEST");
        userRepository.save(user);
        final Goal testGoal = new Goal(user, "test", 11, 1);
        goalRepository.save(testGoal);

        assertThat(goalRepository.getGoalById(testGoal.getId(), user)).isPresent();
        goalRepository.deleteGoal(testGoal.getId(), user);
        assertThat(goalRepository.getGoalById(testGoal.getId(), user)).isEmpty();
    }
}
