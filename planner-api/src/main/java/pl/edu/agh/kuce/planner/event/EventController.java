package pl.edu.agh.kuce.planner.event;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.kuce.planner.auth.Current;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.event.dto.ListResponse;
import pl.edu.agh.kuce.planner.event.dto.OneTimeEventData;
import pl.edu.agh.kuce.planner.event.service.EventService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/event")
public class EventController {

    private final EventService eventService;

    public EventController(final EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/create")
    public void create(@Valid @RequestBody final OneTimeEventData data, @Current final User user) {
        eventService.create(data, user);
    }

    @PostMapping("/list")
    public ListResponse list(@Current final User user) {
        return eventService.list(user);
    }
}
