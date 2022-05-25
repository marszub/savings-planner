package pl.edu.agh.kuce.planner.goal.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.auth.persistence.UserRepository;
import pl.edu.agh.kuce.planner.goal.dto.GoalData;
import pl.edu.agh.kuce.planner.goal.dto.ListResponse;
import pl.edu.agh.kuce.planner.goal.dto.SubGoalInputData;
import pl.edu.agh.kuce.planner.goal.persistence.Goal;
import pl.edu.agh.kuce.planner.goal.persistence.GoalRepository;
import pl.edu.agh.kuce.planner.goal.persistence.SubGoal;
import pl.edu.agh.kuce.planner.goal.persistence.SubGoalRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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
    private final GoalService goalService = new GoalService(goalRepository, subGoalRepository);

    private final User userData = new User("TEST", "TEST", "TEST");
    private final SubGoalInputData subGoalInputData1 = new SubGoalInputData("TitleTest", 100);
    private final SubGoalInputData subGoalInputData2 = new SubGoalInputData("TitleTest2", 200);

    @Test
    @Transactional
    void checkIfSubGoalsAddsProperly() {
        final User user = userRepository.save(userData);
        final Goal goal = goalRepository.save(new Goal(user, "TEST1", 21));
        assertThat(subGoalRepository.getSubGoals(goal, user).size()).isEqualTo(0);
        goalService.createSubGoal(goal.getId(), subGoalInputData1, user);
        goalService.createSubGoal(goal.getId(), subGoalInputData2, user);
        assertThat(subGoalRepository.getSubGoals(goal, user).size()).isEqualTo(2);
    }

    @Test
    @Transactional
    void checkIfServiceReturnsProperGoalsList() {
        final User user = userRepository.save(userData);
        final Goal goal = goalRepository.save(new Goal(user, "TEST22", 22));
        final SubGoal subGoal1 = subGoalRepository.save(new SubGoal(goal, "TEST1", 200));
        final SubGoal subGoal2 = subGoalRepository.save(new SubGoal(goal, "TEST2", 100));
        final ListResponse listResponse = goalService.list(user);
        assertThat(listResponse.goals().size()).isEqualTo(1);
        assertThat(listResponse.goals().get(0).subGoals().size()).isEqualTo(2);
        assertThat(listResponse.goals().get(0).subGoals().get(0).id()).isEqualTo(subGoal1.getId());
        assertThat(listResponse.goals().get(0).amount()).isEqualTo(300);
        assertThat(listResponse.goals().get(0).subGoals().get(0).title()).isEqualTo(subGoal1.getTitle());
        assertThat(listResponse.goals().get(0).subGoals().get(1).id()).isEqualTo(subGoal2.getId());
        assertThat(listResponse.goals().get(0).subGoals().get(1).title()).isEqualTo(subGoal2.getTitle());
    }

    @Test
    @Transactional
    void checkIfSubGoalsDeletesWhenGoalIsDeleted() {
        final User user = userRepository.save(userData);
        final Goal goal = goalRepository.save(new Goal(user, "TEST3", 23));
        final SubGoal subGoal = subGoalRepository.save(new SubGoal(goal, "TEST", 100));
        assertThat(subGoalRepository.getSubGoalById(subGoal.getId(), user).get()).isEqualTo(subGoal);
        goalService.destroy(goal.getId(), user);
        assertThat(subGoalRepository.getSubGoalById(subGoal.getId(), user).isEmpty()).isTrue();
    }

    @Test
    @Transactional
    void checkIfDeletingSubGoalWorksProperly() {
        final User user = userRepository.save(userData);
        final Goal goal = goalRepository.save(new Goal(user, "TEST4", 24));
        final SubGoal subGoal = subGoalRepository.save(new SubGoal(goal, "TEST", 100));
        final SubGoal subGoal2 = subGoalRepository.save(new SubGoal(goal, "TEST2", 100));
        assertThat(subGoalRepository.getSubGoals(goal, user).size()).isEqualTo(2);
        goalService.destroySubGoal(subGoal.getId(), goal.getId(), user);
        assertThat(subGoalRepository.getSubGoals(goal, user).size()).isEqualTo(1);
        assertThat(subGoalRepository.getSubGoals(goal, user).get(0)).isEqualTo(subGoal2);
    }

    @Test
    @Transactional
    void checkIfGoalAmountCountsCorrectly() {
        final User user = userRepository.save(userData);
        final Goal goal = goalRepository.save(new Goal(user, "TEST4", 25));
        goalService.createSubGoal(goal.getId(), subGoalInputData1, user);
        goalService.createSubGoal(goal.getId(), subGoalInputData2, user);
        ListResponse goalList = goalService.list(user);
        assertThat(goalList.goals().get(0).amount()).isEqualTo(300);
        goalService.createSubGoal(goal.getId(), subGoalInputData2, user);
        goalList = goalService.list(user);
        assertThat(goalList.goals().get(0).amount()).isEqualTo(500);
    }

    @Test
    @Transactional
    void checkIfProperGoalIsReturnedAfterSubGoalDeletion() {
        final User user = userRepository.save(userData);
        final Goal goal = goalRepository.save(new Goal(user, "TEST4", 26));
        goalService.createSubGoal(goal.getId(), subGoalInputData1, user);
        final SubGoal subGoalToBeDeleted = subGoalRepository.save(new SubGoal(goal, "TEST2",  111));
        ListResponse goalList = goalService.list(user);
        assertThat(goalList.goals().get(0).amount()).isEqualTo(211);
        final GoalData data = goalService.destroySubGoal(subGoalToBeDeleted.getId(), goal.getId(), user);
        assertThat(data.amount()).isEqualTo(100);
        goalList = goalService.list(user);
        assertThat(goalList.goals().get(0).amount()).isEqualTo(100);
    }

    @Test
    @Transactional
    void checkIfProperGoalIsReturnedAfterSubGoalCreation() {
        final User user = userRepository.save(userData);
        final Goal goal = goalRepository.save(new Goal(user, "TEST4", 27));
        goalService.createSubGoal(goal.getId(), subGoalInputData1, user);
        goalService.createSubGoal(goal.getId(), subGoalInputData2, user);
        ListResponse goalList = goalService.list(user);
        assertThat(goalList.goals().get(0).amount()).isEqualTo(300);
        final GoalData data = goalService.createSubGoal(goal.getId(), subGoalInputData2, user);
        assertThat(data.amount()).isEqualTo(500);
        goalList = goalService.list(user);
        assertThat(goalList.goals().get(0).amount()).isEqualTo(500);
    }

    @Test
    @Transactional
    void checkIfSubGoalsAreInCompleteOnTheResponseList() {
        final User user = userRepository.save(userData);
        final Goal goal = goalRepository.save(new Goal(user, "TEST4", 28));
        goalService.createSubGoal(goal.getId(), subGoalInputData1, user);
        final GoalData data = goalService.createSubGoal(goal.getId(), subGoalInputData2, user);
        assertThat(data.subGoals().get(0).completed()).isEqualTo(Boolean.FALSE);
        assertThat(data.subGoals().get(1).completed()).isEqualTo(Boolean.FALSE);
    }

    @Test
    @Transactional
    void checkIfSubGoalsAreCompleteAfterUpdatingThem() {
        final User user = userRepository.save(userData);
        final Goal goal = goalRepository.save(new Goal(user, "TEST4", 29));
        goalService.createSubGoal(goal.getId(), subGoalInputData1, user);
        final SubGoal subGoal = subGoalRepository.save(new SubGoal(goal, "TEST2",  111));
        final GoalData data = goalService.completeSubGoal(subGoal.getId(), goal.getId(), user);
        assertThat(data.subGoals().get(0).completed()).isEqualTo(Boolean.FALSE);
        assertThat(data.subGoals().get(1).completed()).isEqualTo(Boolean.TRUE);
    }

    @Test
    @Transactional
    void deletingSubGoalsFromGoalThatDoesNotExistsThrows() {
        final User user = userRepository.save(userData);
        assertThatExceptionOfType(GoalNotFoundException.class).isThrownBy(() -> {
            goalService.destroySubGoal(5, 5, user);
        });
    }

    @Test
    @Transactional
    void deletingSubGoalsThatDoesNotExistsThrows() {
        final User user = userRepository.save(userData);
        final Goal goal = goalRepository.save(new Goal(user, "TEST4", 31));
        assertThatExceptionOfType(GoalNotFoundException.class).isThrownBy(() -> {
            goalService.destroySubGoal(5, goal.getId(), user);
        });
    }

    @Test
    @Transactional
    void completingSubGoalsFromGoalThatDoesNotExistsThrows() {
        final User user = userRepository.save(userData);
        assertThatExceptionOfType(GoalNotFoundException.class).isThrownBy(() -> {
            goalService.completeSubGoal(5, 5, user);
        });
    }

    @Test
    @Transactional
    void completingSubGoalsThatDoesNotExistsThrows() {
        final User user = userRepository.save(userData);
        final Goal goal = goalRepository.save(new Goal(user, "TEST4", 31));
        assertThatExceptionOfType(GoalNotFoundException.class).isThrownBy(() -> {
            goalService.completeSubGoal(5, goal.getId(), user);
        });
    }
}
