package pl.edu.agh.kuce.planner.event;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.kuce.planner.auth.dto.AuthResponseDto;
import pl.edu.agh.kuce.planner.auth.dto.LoginRequestDto;
import pl.edu.agh.kuce.planner.auth.dto.RegistrationRequestDto;
import pl.edu.agh.kuce.planner.auth.service.AuthService;
import pl.edu.agh.kuce.planner.event.dto.CreateRequest;
import pl.edu.agh.kuce.planner.event.dto.CreateResponse;
import pl.edu.agh.kuce.planner.event.dto.ListRequest;
import pl.edu.agh.kuce.planner.event.dto.ListResponse;
import pl.edu.agh.kuce.planner.event.service.EventService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/event")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/create")
    public CreateResponse create(@Valid @RequestBody CreateRequest request) {
        return eventService.create(request);
    }

    @PostMapping("/list")
    public ListResponse list(@Valid @RequestBody ListRequest request) {
        return eventService.list(request);
    }
}
