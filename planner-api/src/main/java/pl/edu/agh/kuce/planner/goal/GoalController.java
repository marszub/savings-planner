package pl.edu.agh.kuce.planner.goal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.kuce.planner.auth.Current;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.goal.dto.GoalData;
import pl.edu.agh.kuce.planner.goal.dto.SubGoalData;
import pl.edu.agh.kuce.planner.goal.dto.GoalInputData;
import pl.edu.agh.kuce.planner.goal.dto.SubGoalInputData;
import pl.edu.agh.kuce.planner.goal.dto.GoalPriorityUpdate;
import pl.edu.agh.kuce.planner.goal.dto.ListResponse;
import pl.edu.agh.kuce.planner.goal.service.GoalService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/goals")
public class GoalController {
    private final GoalService goalService;

    public GoalController(final GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalData create(@Valid @RequestBody final GoalInputData data, @Current final User user) {
        return goalService.create(data, user);
    }

    @PostMapping("/{id}/sub-goals")
    @ResponseStatus(HttpStatus.CREATED)
    public SubGoalData create(@PathVariable("id") final Integer goalId,
                           @Valid @RequestBody final SubGoalInputData data,
                           @Current final User user) {
        return goalService.createSubGoal(goalId, data, user);
    }

    @GetMapping
    public ListResponse list(@Current final User user) {
        return goalService.list(user);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePriority(@Valid @RequestBody final GoalPriorityUpdate dto, @Current final User user) {
        goalService.updatePriority(dto, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable("id") final Integer goalId,
                        @Current final User user) {
        goalService.destroy(goalId, user);
    }

    @DeleteMapping("/{id}/sub-goals/{subGoalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable("id") final Integer goalId,
                        @Current final User user,
                        @PathVariable("subGoalId") final Integer subGoalId) {
        goalService.destroySubGoal(subGoalId, goalId, user);
    }
}
