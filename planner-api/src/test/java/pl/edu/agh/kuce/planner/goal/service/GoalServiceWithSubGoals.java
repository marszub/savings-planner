package pl.edu.agh.kuce.planner.goal.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.auth.persistence.UserRepository;
import pl.edu.agh.kuce.planner.goal.dto.ListResponse;
import pl.edu.agh.kuce.planner.goal.dto.SubGoalData;
import pl.edu.agh.kuce.planner.goal.dto.SubGoalInputData;
import pl.edu.agh.kuce.planner.goal.persistence.Goal;
import pl.edu.agh.kuce.planner.goal.persistence.GoalRepository;
import pl.edu.agh.kuce.planner.goal.persistence.SubGoal;
import pl.edu.agh.kuce.planner.goal.persistence.SubGoalRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class GoalServiceWithSubGoals {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private SubGoalRepository subGoalRepository;

    @Autowired
    final GoalService goalService = new GoalService(goalRepository, subGoalRepository);

    @Test
    void checkIfSubGoalsAddsProperly() {
        final User user = userRepository.save(new User("TEST21", "TEST21", "TEST21"));
        final Goal goal = goalRepository.save(new Goal(user, "TEST1", 1, 21));
        assertThat(subGoalRepository.getSubGoals(goal, user).size()).isEqualTo(0);
        goalService.createSubGoal(goal.getId(), new SubGoalInputData("TitleTest"), user);
        goalService.createSubGoal(goal.getId(), new SubGoalInputData("TitleTest2"), user);
        assertThat(subGoalRepository.getSubGoals(goal, user).size()).isEqualTo(2);
    }

    @Test
    void checkIfServiceReturnsProperGoalsList() {
        final User user = userRepository.save(new User("TEST22", "TEST22", "TEST22"));
        final Goal goal = goalRepository.save(new Goal(user, "TEST22", 1, 22));
        SubGoal subGoal1 = subGoalRepository.save(new SubGoal(goal, "TEST1"));
        SubGoal subGoal2 = subGoalRepository.save(new SubGoal(goal, "TEST2"));
        ListResponse listResponse = goalService.list(user);
        assertThat(listResponse.goals().size()).isEqualTo(1);
        assertThat(listResponse.goals().get(0).subGoals().size()).isEqualTo(2);
        assertThat(listResponse.goals().get(0).subGoals().get(0).id()).isEqualTo(subGoal1.getId());
        assertThat(listResponse.goals().get(0).subGoals().get(0).title()).isEqualTo(subGoal1.getTitle());
        assertThat(listResponse.goals().get(0).subGoals().get(1).id()).isEqualTo(subGoal2.getId());
        assertThat(listResponse.goals().get(0).subGoals().get(1).title()).isEqualTo(subGoal2.getTitle());
    }

    @Test
    void checkIfSubGoalsDeletesWhenGoalIsDeleted() {
        final User user = userRepository.save(new User("TEST23", "TEST23", "TEST23"));
        final Goal goal = goalRepository.save(new Goal(user, "TEST3", 1, 23));
        SubGoal subGoal = subGoalRepository.save(new SubGoal(goal, "TEST"));
        assertThat(subGoalRepository.getSubGoalById(subGoal.getId(), user).get()).isEqualTo(subGoal);
        goalService.destroy(goal.getId(), user);
        assertThat(subGoalRepository.getSubGoalById(subGoal.getId(), user).isEmpty()).isTrue();
    }

    @Test
    void checkIfDeletingSubGoalWorksProperly() {
        final User user = userRepository.save(new User("TEST24", "TEST24", "TEST24"));
        final Goal goal = goalRepository.save(new Goal(user, "TEST4", 1, 24));
        SubGoal subGoal = subGoalRepository.save(new SubGoal(goal, "TEST"));
        SubGoal subGoal2 = subGoalRepository.save(new SubGoal(goal, "TEST2"));
        assertThat(subGoalRepository.getSubGoals(goal, user).size()).isEqualTo(2);
        goalService.destroySubGoal(subGoal.getId(), goal.getId(), user);
        assertThat(subGoalRepository.getSubGoals(goal, user).size()).isEqualTo(1);
        assertThat(subGoalRepository.getSubGoals(goal, user).get(0)).isEqualTo(subGoal2);
    }
}
