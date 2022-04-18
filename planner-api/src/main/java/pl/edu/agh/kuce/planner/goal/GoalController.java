package pl.edu.agh.kuce.planner.goal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
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
    public void create(@Valid @RequestBody final GoalInputData data, @Current final User user) {
        goalService.create(data, user);
    }

    @GetMapping("/goals")
    public ListResponse list(@Current final User user) {
        return goalService.list(user);
    }

    @DeleteMapping("goal/{id}")
    public void destroy(@PathVariable("id") final Integer goalId,
                        @Current final User user) throws GoalNotFoundException {
        goalService.destroy(goalId, user);
    }
}
