package pl.edu.agh.kuce.planner.event;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.kuce.planner.auth.Current;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.event.dto.EventData;
import pl.edu.agh.kuce.planner.event.dto.EventDataInput;
import pl.edu.agh.kuce.planner.event.dto.EventList;
import pl.edu.agh.kuce.planner.event.dto.EventTimestampList;
import pl.edu.agh.kuce.planner.event.dto.TimestampListRequest;
import pl.edu.agh.kuce.planner.event.service.EventService;

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
    public EventData create(@Valid @RequestBody final EventDataInput eventData, @Current final User user) {
        return eventService.create(eventData, user);
    }

    @GetMapping("")
    public EventList list(@Current final User user) {
        return eventService.list(user);
    }

    @GetMapping("following-n")
    public EventTimestampList followingN(@Valid @RequestBody final TimestampListRequest request,
                                         @Current final User user) {
        return eventService.getFollowingEventTimestamps(request, user);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable("id") final Integer eventId,
                                   @Valid @RequestBody final EventDataInput eventData,
                                   @Current final User user) {
        eventService.update(eventData, eventId, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") final Integer eventId,
                       @Current final User user) {
        eventService.delete(eventId, user);
    }
}
