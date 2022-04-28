package pl.edu.agh.kuce.planner.goal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.kuce.planner.auth.Current;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.goal.dto.GoalData;
import pl.edu.agh.kuce.planner.goal.dto.GoalPriorityUpdate;
import pl.edu.agh.kuce.planner.goal.dto.ListResponse;
import pl.edu.agh.kuce.planner.goal.dto.GoalInputData;
import pl.edu.agh.kuce.planner.goal.service.GoalService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class GoalController {
    private final GoalService goalService;

    public GoalController(final GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping("/goals")
    @ResponseStatus(HttpStatus.CREATED)
    public GoalData create(@Valid @RequestBody final GoalInputData data, @Current final User user) {
        return goalService.create(data, user);
    }

    @GetMapping("/goals")
    public ListResponse list(@Current final User user) {
        return goalService.list(user);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePriority(@Valid @RequestBody final GoalPriorityUpdate dto, @Current final User user) {
        goalService.updatePriority(dto, user);
    }

    @DeleteMapping("goals/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable("id") final Integer goalId,
                        @Current final User user) {
        goalService.destroy(goalId, user);
    }
}
