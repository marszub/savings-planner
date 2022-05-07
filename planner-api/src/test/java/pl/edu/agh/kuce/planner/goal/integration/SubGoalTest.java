package pl.edu.agh.kuce.planner.goal.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.auth.persistence.UserRepository;
import pl.edu.agh.kuce.planner.goal.persistence.Goal;
import pl.edu.agh.kuce.planner.goal.persistence.GoalRepository;
import pl.edu.agh.kuce.planner.goal.persistence.SubGoal;
import pl.edu.agh.kuce.planner.goal.persistence.SubGoalRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class SubGoalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private SubGoalRepository subGoalRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void singleSubGoalIsProperlySavedInDatabaseCheckedByGoal() {
        final User user = userRepository.save(new User("TEST1", "TEST1", "TEST1"));
        final Goal goal = goalRepository.save(new Goal(user, "TEST1", 1, 1));
        SubGoal subGoal = subGoalRepository.save(new SubGoal(goal, "TEST"));
        List<SubGoal> result = subGoalRepository.getSubGoals(goal, user);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getTitle()).isEqualTo(subGoal.getTitle());
        assertThat(result.get(0).getGoal()).isEqualTo(subGoal.getGoal());
        assertThat(result.get(0).getId()).isEqualTo(subGoal.getId());
    }

    @Test
    void singleSubGoalIsProperlySavedInDatabaseCheckedById() {
        final User user = userRepository.save(new User("TEST2", "TEST2", "TEST2"));
        final Goal goal = goalRepository.save(new Goal(user, "TEST", 1, 2));
        SubGoal subGoal = subGoalRepository.save(new SubGoal(goal, "TEST"));
        Optional<SubGoal> result = subGoalRepository.getSubGoalById(subGoal.getId(), user);
        assertThat(result.get().getTitle()).isEqualTo(subGoal.getTitle());
        assertThat(result.get().getGoal()).isEqualTo(subGoal.getGoal());
        assertThat(result.get().getId()).isEqualTo(subGoal.getId());
    }

    @Test
    void getMultipleSubGoals() {
        final User user = userRepository.save(new User("TEST3", "TEST3", "TEST3"));
        final Goal goal = goalRepository.save(new Goal(user, "TEST3", 1, 3));
        SubGoal subGoal1 = subGoalRepository.save(new SubGoal(goal, "TEST"));
        SubGoal subGoal2 = subGoalRepository.save(new SubGoal(goal, "TEST2"));
        List<SubGoal> result = subGoalRepository.getSubGoals(goal, user);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getTitle()).isEqualTo(subGoal1.getTitle());
        assertThat(result.get(0).getGoal()).isEqualTo(subGoal1.getGoal());
        assertThat(result.get(0).getId()).isEqualTo(subGoal1.getId());
        assertThat(result.get(1).getTitle()).isEqualTo(subGoal2.getTitle());
        assertThat(result.get(1).getGoal()).isEqualTo(subGoal2.getGoal());
        assertThat(result.get(1).getId()).isEqualTo(subGoal2.getId());
    }

    @Test
    @Transactional
    void deleteSubGoal() {
        final User user = userRepository.save(new User("TEST4", "TEST4", "TEST4"));
        final Goal goal = goalRepository.save(new Goal(user, "TEST1", 1, 4));
        SubGoal subGoal = subGoalRepository.save(new SubGoal(goal, "TEST"));
        subGoalRepository.deleteSubGoal(subGoal.getId(), goal);
        List<SubGoal> result = subGoalRepository.getSubGoals(goal, user);
        assertThat(result.size()).isEqualTo(0);
    }
}
