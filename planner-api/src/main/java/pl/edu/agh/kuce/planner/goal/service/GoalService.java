package pl.edu.agh.kuce.planner.goal.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.goal.GoalNotFoundException;
import pl.edu.agh.kuce.planner.goal.dto.GoalData;
import pl.edu.agh.kuce.planner.goal.dto.GoalInputData;
import pl.edu.agh.kuce.planner.goal.dto.GoalPriority;
import pl.edu.agh.kuce.planner.goal.dto.GoalPriorityUpdate;
import pl.edu.agh.kuce.planner.goal.dto.ListResponse;
import pl.edu.agh.kuce.planner.goal.persistence.Goal;
import pl.edu.agh.kuce.planner.goal.persistence.GoalRepository;

import java.util.Optional;
import java.util.stream.Collectors;

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
                        .findByUserOrderByPriorityDesc(user)
                        .stream()
                        .map(GoalData::new)
                        .toList()
        );
    }

    @Transactional
    public void updatePriority(final GoalPriorityUpdate dto, final User user) {
        final var savedGoals = goalRepository.findByUserOrderByPriorityDesc(user).stream()
                .collect(Collectors.toMap(Goal::getId, goal -> goal));

        final boolean goalNotPresent = dto.newPriorities().stream()
                .anyMatch(goal -> !savedGoals.containsKey(goal.id()));

        if (goalNotPresent) {
            throw new GoalNotFoundException("Goal with that id does not exist");
        }

        final var newPriorities = dto.newPriorities().stream()
                .collect(Collectors.toMap(GoalPriority::id, GoalPriority::newPriority));

        goalRepository.saveAll(
                savedGoals.values().stream()
                        .filter(goal -> newPriorities.containsKey(goal.getId()))
                        .peek(goal -> goal.setPriority(newPriorities.get(goal.getId())))
                        .toList()
        );
    }

    @Transactional
    public void destroy(final Integer id, final User user) {
        final Optional<Goal> goal = goalRepository.getGoalById(id, user);
        if (goal.isPresent()) {
            goalRepository.deleteGoal(id, user);
            return;
        }
        throw new GoalNotFoundException("Goal with that id does not exist");
    }
}
