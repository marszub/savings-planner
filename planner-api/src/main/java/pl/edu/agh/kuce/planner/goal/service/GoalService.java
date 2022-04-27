package pl.edu.agh.kuce.planner.goal.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.goal.GoalNotFoundException;
import pl.edu.agh.kuce.planner.goal.dto.ListResponse;
import pl.edu.agh.kuce.planner.goal.dto.GoalData;
import pl.edu.agh.kuce.planner.goal.dto.GoalInputData;
import pl.edu.agh.kuce.planner.goal.persistence.Goal;
import pl.edu.agh.kuce.planner.goal.persistence.GoalRepository;

import java.util.Optional;

@Service
public class GoalService {
    private final GoalRepository goalRepository;

    public GoalService(final GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public GoalData create(final GoalInputData data, final User user) {
        final Goal goal = goalRepository.save(new Goal(user, data));
        return new GoalData(goal);
    }

    public ListResponse list(final User user) {
        return new ListResponse(
                goalRepository
                        .findByUser(user)
                        .stream()
                        .map(GoalData::new).toList());
    }

    @Transactional
    public void destroy(final Integer id, final User user) throws GoalNotFoundException {
        final Optional<Goal> goal = goalRepository.getGoalById(id, user);
        if (goal.isPresent()) {
            goalRepository.deleteGoal(id, user);
            return;
        }
        throw new GoalNotFoundException("Goal with that id does not exist");
    }
}
