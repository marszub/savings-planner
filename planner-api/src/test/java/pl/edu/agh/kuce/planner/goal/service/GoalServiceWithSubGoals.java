package pl.edu.agh.kuce.planner.goal.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

    @Test
    void checkIfSubGoalsAddsProperly() {
        final User user = userRepository.save(new User("TEST21", "TEST21", "TEST21"));
        final Goal goal = goalRepository.save(new Goal(user, "TEST1", 21));
        assertThat(subGoalRepository.getSubGoals(goal, user).size()).isEqualTo(0);
        goalService.createSubGoal(goal.getId(), new SubGoalInputData("TitleTest", 100), user);
        goalService.createSubGoal(goal.getId(), new SubGoalInputData("TitleTest2", 100), user);
        assertThat(subGoalRepository.getSubGoals(goal, user).size()).isEqualTo(2);
    }

    @Test
    void checkIfServiceReturnsProperGoalsList() {
        final User user = userRepository.save(new User("TEST22", "TEST22", "TEST22"));
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
    void checkIfSubGoalsDeletesWhenGoalIsDeleted() {
        final User user = userRepository.save(new User("TEST23", "TEST23", "TEST23"));
        final Goal goal = goalRepository.save(new Goal(user, "TEST3", 23));
        final SubGoal subGoal = subGoalRepository.save(new SubGoal(goal, "TEST", 100));
        assertThat(subGoalRepository.getSubGoalById(subGoal.getId(), user).get()).isEqualTo(subGoal);
        goalService.destroy(goal.getId(), user);
        assertThat(subGoalRepository.getSubGoalById(subGoal.getId(), user).isEmpty()).isTrue();
    }

    @Test
    void checkIfDeletingSubGoalWorksProperly() {
        final User user = userRepository.save(new User("TEST24", "TEST24", "TEST24"));
        final Goal goal = goalRepository.save(new Goal(user, "TEST4", 24));
        final SubGoal subGoal = subGoalRepository.save(new SubGoal(goal, "TEST", 100));
        final SubGoal subGoal2 = subGoalRepository.save(new SubGoal(goal, "TEST2", 100));
        assertThat(subGoalRepository.getSubGoals(goal, user).size()).isEqualTo(2);
        goalService.destroySubGoal(subGoal.getId(), goal.getId(), user);
        assertThat(subGoalRepository.getSubGoals(goal, user).size()).isEqualTo(1);
        assertThat(subGoalRepository.getSubGoals(goal, user).get(0)).isEqualTo(subGoal2);
    }

    @Test
    void checkIfGoalAmountCountsCorrectly() {
        final User user = userRepository.save(new User("TEST25", "TEST25", "TEST25"));
        final Goal goal = goalRepository.save(new Goal(user, "TEST4", 25));
        subGoalRepository.save(new SubGoal(goal, "TEST", 222));
        subGoalRepository.save(new SubGoal(goal, "TEST2",  111));
        ListResponse goalList = goalService.list(user);
        assertThat(goalList.goals().get(0).amount()).isEqualTo(333);
        subGoalRepository.save(new SubGoal(goal, "TEST3", 444));
        goalList = goalService.list(user);
        assertThat(goalList.goals().get(0).amount()).isEqualTo(777);
    }

    @Test
    void checkIfProperGoalIsReturnedAfterSubGoalDeletion() {
        final User user = userRepository.save(new User("TEST26", "TEST26", "TEST26"));
        final Goal goal = goalRepository.save(new Goal(user, "TEST4", 26));
        subGoalRepository.save(new SubGoal(goal, "TEST", 222));
        final SubGoal subGoalToBeDeleted = subGoalRepository.save(new SubGoal(goal, "TEST2",  111));
        ListResponse goalList = goalService.list(user);
        assertThat(goalList.goals().get(0).amount()).isEqualTo(333);
        final GoalData data = goalService.destroySubGoal(subGoalToBeDeleted.getId(), goal.getId(), user);
        assertThat(data.amount()).isEqualTo(222);
        goalList = goalService.list(user);
        assertThat(goalList.goals().get(0).amount()).isEqualTo(222);
    }

    @Test
    void checkIfProperGoalIsReturnedAfterSubGoalCreation() {
        final User user = userRepository.save(new User("TEST27", "TEST27", "TEST27"));
        final Goal goal = goalRepository.save(new Goal(user, "TEST4", 27));
        subGoalRepository.save(new SubGoal(goal, "TEST", 222));
        subGoalRepository.save(new SubGoal(goal, "TEST2",  111));
        final SubGoalInputData subGoalToBeCreated = new SubGoalInputData("TEST2", 444);
        ListResponse goalList = goalService.list(user);
        assertThat(goalList.goals().get(0).amount()).isEqualTo(333);
        final GoalData data = goalService.createSubGoal(goal.getId(), subGoalToBeCreated, user);
        assertThat(data.amount()).isEqualTo(777);
        goalList = goalService.list(user);
        assertThat(goalList.goals().get(0).amount()).isEqualTo(777);
    }

    @Test
    void checkIfSubGoalsAreInCompleteOnTheResponseList() {
        final User user = userRepository.save(new User("TEST28", "TEST28", "TEST28"));
        final Goal goal = goalRepository.save(new Goal(user, "TEST4", 28));
        subGoalRepository.save(new SubGoal(goal, "TEST", 322));
        final SubGoalInputData subGoalToBeCreated = new SubGoalInputData("TEST2", 444);
        final GoalData data = goalService.createSubGoal(goal.getId(), subGoalToBeCreated, user);
        assertThat(data.subGoals().get(0).completed()).isEqualTo(Boolean.FALSE);
        assertThat(data.subGoals().get(1).completed()).isEqualTo(Boolean.FALSE);
    }

    @Test
    void checkIfSubGoalsAreCompleteAfterUpdatingThem() {
        final User user = userRepository.save(new User("TEST29", "TEST29", "TEST29"));
        final Goal goal = goalRepository.save(new Goal(user, "TEST4", 29));
        subGoalRepository.save(new SubGoal(goal, "TEST", 222));
        final SubGoal subGoal = subGoalRepository.save(new SubGoal(goal, "TEST2",  111));
        final GoalData data = goalService.completeSubGoal(subGoal.getId(), goal.getId(), user);
        assertThat(data.subGoals().get(0).completed()).isEqualTo(Boolean.FALSE);
        assertThat(data.subGoals().get(1).completed()).isEqualTo(Boolean.TRUE);
    }

    @Test
    void deletingSubGoalsFromGoalThatDoesNotExistsThrows() {
        final User user = userRepository.save(new User("TEST30", "TEST30", "TEST30"));
        assertThatExceptionOfType(GoalNotFoundException.class).isThrownBy(() -> {
            goalService.destroySubGoal(5, 5, user);
        });
    }

    @Test
    void deletingSubGoalsThatDoesNotExistsThrows() {
        final User user = userRepository.save(new User("TEST31", "TEST31", "TEST31"));
        final Goal goal = goalRepository.save(new Goal(user, "TEST4", 31));
        assertThatExceptionOfType(GoalNotFoundException.class).isThrownBy(() -> {
            goalService.destroySubGoal(5, goal.getId(), user);
        });
    }

    @Test
    void completingSubGoalsFromGoalThatDoesNotExistsThrows() {
        final User user = userRepository.save(new User("TEST32", "TEST32", "TEST32"));
        assertThatExceptionOfType(GoalNotFoundException.class).isThrownBy(() -> {
            goalService.completeSubGoal(5, 5, user);
        });
    }

    @Test
    void completingSubGoalsThatDoesNotExistsThrows() {
        final User user = userRepository.save(new User("TEST33", "TEST33", "TEST33"));
        final Goal goal = goalRepository.save(new Goal(user, "TEST4", 33));
        assertThatExceptionOfType(GoalNotFoundException.class).isThrownBy(() -> {
            goalService.completeSubGoal(5, goal.getId(), user);
        });
    }
}
