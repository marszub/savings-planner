package pl.edu.agh.kuce.planner.event.service;

import org.springframework.stereotype.Service;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.event.dto.ListResponse;
import pl.edu.agh.kuce.planner.event.dto.OneTimeEventData;
import pl.edu.agh.kuce.planner.event.dto.OneTimeEventDataInput;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEvent;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEventRepository;

@Service
public class EventService {
    private final OneTimeEventRepository oneTimeEventRepository;

    public EventService(final OneTimeEventRepository oneTimeEventRepository) {
        this.oneTimeEventRepository = oneTimeEventRepository;
    }

    public OneTimeEventData create(final OneTimeEventDataInput request, final User user) {
        final OneTimeEvent event = oneTimeEventRepository.save(new OneTimeEvent(request, user));
        return new OneTimeEventData(event);
    }

    public ListResponse list(final User user) {
        return new ListResponse(
                oneTimeEventRepository
                        .findByUser(user)
                        .stream()
                        .map(OneTimeEventData::new).toList());
    }
}
