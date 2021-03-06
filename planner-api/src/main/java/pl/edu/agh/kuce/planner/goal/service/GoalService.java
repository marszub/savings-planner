package pl.edu.agh.kuce.planner.goal.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.goal.dto.ListResponse;
import pl.edu.agh.kuce.planner.goal.dto.GoalData;
import pl.edu.agh.kuce.planner.goal.dto.GoalInputData;
import pl.edu.agh.kuce.planner.goal.dto.SubGoalData;
import pl.edu.agh.kuce.planner.goal.dto.SubGoalInputData;
import pl.edu.agh.kuce.planner.goal.dto.GoalPriority;
import pl.edu.agh.kuce.planner.goal.dto.GoalPriorityUpdate;
import pl.edu.agh.kuce.planner.goal.persistence.Goal;
import pl.edu.agh.kuce.planner.goal.persistence.GoalRepository;
import pl.edu.agh.kuce.planner.goal.persistence.SubGoal;
import pl.edu.agh.kuce.planner.goal.persistence.SubGoalRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GoalService {
    private final GoalRepository goalRepository;

    private final SubGoalRepository subGoalRepository;

    private final String goalNotFoundText = "Goal with that id does not exist";

    public GoalService(final GoalRepository goalRepository, final SubGoalRepository subGoalRepository) {
        this.subGoalRepository = subGoalRepository;
        this.goalRepository = goalRepository;
    }

    public GoalData create(final GoalInputData data, final User user) {
        final Goal goal = goalRepository.save(new Goal(user, data));
        return createGoalData(goal, user);
    }

    public GoalData createSubGoal(final Integer goalId, final SubGoalInputData data, final User user) {
        final Optional<Goal> goal = goalRepository.getGoalById(goalId, user);
        if (goal.isPresent()) {
            subGoalRepository.save(new SubGoal(goal.get(), data));
            return createGoalData(goal.get(), user);
        }
        throw new GoalNotFoundException(goalNotFoundText);
    }

    public ListResponse list(final User user) {
        return new ListResponse(
                goalRepository
                        .findByUserOrderByPriorityDesc(user)
                        .stream()
                        .map(goal -> new GoalData(goal, transformSubGoals(subGoalRepository.getSubGoals(goal, user))))
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
            throw new GoalNotFoundException();
        }

        final var newPriorities = dto.newPriorities().stream()
                .collect(Collectors.toMap(GoalPriority::id, GoalPriority::newPriority));

        /*
        This trick allows to swap values of columns with unique constraints. I have no idea how to make it work another
        way.
         */
        goalRepository.saveAll(
                savedGoals.values().stream()
                        .filter(goal -> newPriorities.containsKey(goal.getId()))
                        .peek(goal -> goal.setPriority(Integer.MIN_VALUE + goal.getId()))
                        .toList()
        );
        goalRepository.flush();

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
            subGoalRepository.getSubGoals(goal.get(), user).forEach(subGoal -> {
                subGoalRepository.deleteSubGoal(subGoal.getId(), goal.get());
            });
            goalRepository.deleteGoal(id, user);
            return;
        }

        throw new GoalNotFoundException(goalNotFoundText);
    }

    @Transactional
    public GoalData destroySubGoal(final Integer subGoalId, final Integer goalId, final User user) {
        final Goal goal = checkIfGoalAndSubGoalExists(goalId, subGoalId, user);
        subGoalRepository.deleteSubGoal(subGoalId, goal);
        return createGoalData(goal, user);
    }

    @Transactional
    public GoalData completeSubGoal(final Integer subGoalId, final Integer goalId, final User user) {
        final Goal goal = checkIfGoalAndSubGoalExists(goalId, subGoalId, user);
        subGoalRepository.completeSubGoal(subGoalId, goal);
        return createGoalData(goal, user);
    }

    private GoalData createGoalData(final Goal goal, final User user) {
        return new GoalData(goal, transformSubGoals(subGoalRepository.getSubGoals(goal, user)));
    }

    private Goal checkIfGoalAndSubGoalExists(final Integer goalId, final Integer subGoalId, final User user) {
        final Optional<Goal> goal = goalRepository.getGoalById(goalId, user);
        if (goal.isEmpty()) {
            throw new GoalNotFoundException(goalNotFoundText);
        }
        final Optional<SubGoal> subGoal = subGoalRepository.getSubGoalById(subGoalId, user);
        if (subGoal.isEmpty()) {
            throw new GoalNotFoundException(goalNotFoundText);
        }
        return goal.get();
    }

    private List<SubGoalData> transformSubGoals(final List<SubGoal> subGoalList) {
        return subGoalList.stream()
                .map(SubGoalData::new)
                .toList();
    }
}
