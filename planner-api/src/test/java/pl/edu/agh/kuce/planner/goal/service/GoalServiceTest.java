package pl.edu.agh.kuce.planner.goal.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.goal.GoalNotFoundException;
import pl.edu.agh.kuce.planner.goal.dto.GoalData;
import pl.edu.agh.kuce.planner.goal.dto.ListResponse;
import pl.edu.agh.kuce.planner.goal.dto.GoalInputData;
import pl.edu.agh.kuce.planner.goal.persistence.Goal;
import pl.edu.agh.kuce.planner.goal.persistence.GoalRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class GoalServiceTest {

    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    private final String nick1 = "nick1";
    private final String email1 = "nick1@abc.com";
    private final String password1 = "pass1-HASH";
    private final String nick2 = "nick2";
    private final String email2 = "nick2@abc.com";
    private final String password2 = "pass2-HASH";
    private final String title1 = "Title1";
    private final Integer amount1 = 201;
    private final User user1 = new User(nick1, email1, password1);
    private final User user2 = new User(nick2, email2, password2);

    @BeforeEach
    void setUp() {
        user1.setId(1);
        user2.setId(2);
        MockitoAnnotations.openMocks(this);

        when(goalRepository.findByUser(user1))
                .thenReturn(List.of(new Goal(user1, title1, amount1)));
        when(goalRepository.findByUser(user2))
                .thenReturn(List.of());

        goalService = new GoalService(goalRepository);
    }

    @Test
    void create_doesNotThrow() {
        Assertions.assertDoesNotThrow(
                () -> goalService.create(new GoalInputData(title1, amount1), user1));
    }

    @Test
    void list_returnsCorrespondingData() {
        final ListResponse response = goalService.list(user1);
        assertThat(response.list().size()).isEqualTo(1);
        final GoalData foundGoal = response.list().get(0);
        assertThat(foundGoal.title()).isEqualTo(title1);
        assertThat(foundGoal.amount()).isEqualTo(amount1);
    }

    @Test
    void list_notReturnsDataForDifferentUser() {
        final ListResponse response = goalService.list(user2);
        assertThat(response.list().size()).isEqualTo(0);
    }

    @Test
    void delete_NonExistingGoalThrows() {
        Assertions.assertThrows(GoalNotFoundException.class,
                () -> goalService.destroy(10, user1));
    }

}
