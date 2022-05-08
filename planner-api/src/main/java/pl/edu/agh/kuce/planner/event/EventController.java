package pl.edu.agh.kuce.planner.event;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.kuce.planner.auth.Current;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.event.dto.ListResponse;
import pl.edu.agh.kuce.planner.event.dto.OneTimeEventData;
import pl.edu.agh.kuce.planner.event.dto.OneTimeEventDataInput;
import pl.edu.agh.kuce.planner.event.service.EventService;
import pl.edu.agh.kuce.planner.shared.ResourceNotFoundException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(final EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public OneTimeEventData create(@Valid @RequestBody final OneTimeEventDataInput data, @Current final User user) {
        return eventService.create(data, user);
    }

    @GetMapping("")
    public ListResponse list(@Current final User user) {
        return eventService.list(user);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable("id") final Integer eventId,
                                   @Valid @RequestBody final OneTimeEventDataInput data,
                                   @Current final User user) throws ResourceNotFoundException {
        eventService.update(data, eventId, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") final Integer eventId,
                       @Current final User user) throws ResourceNotFoundException {
        eventService.delete(eventId, user);
    }
}
