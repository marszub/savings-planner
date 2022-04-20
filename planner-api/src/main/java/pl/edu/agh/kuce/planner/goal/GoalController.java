package pl.edu.agh.kuce.planner.goal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.kuce.planner.auth.Current;
import pl.edu.agh.kuce.planner.auth.persistence.User;
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

    @PostMapping("/goal")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void create(@Valid @RequestBody final GoalInputData data, @Current final User user) {
        goalService.create(data, user);
    }

    @GetMapping("/goals")
    public ListResponse list(@Current final User user) {
        return goalService.list(user);
    }

    @DeleteMapping("goal/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable("id") final Integer goalId,
                        @Current final User user) throws GoalNotFoundException {
        goalService.destroy(goalId, user);
    }
}
