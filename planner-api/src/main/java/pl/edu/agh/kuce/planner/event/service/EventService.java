package pl.edu.agh.kuce.planner.event.service;

import org.springframework.stereotype.Service;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.event.dto.ListResponse;
import pl.edu.agh.kuce.planner.event.dto.OneTimeEventData;
import pl.edu.agh.kuce.planner.event.dto.OneTimeEventDataInput;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEvent;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEventRepository;
import pl.edu.agh.kuce.planner.shared.ResourceNotFoundException;

import java.util.Optional;

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

    public void update(final OneTimeEventDataInput newData,
                                   final Integer eventId,
                                   final User user) throws ResourceNotFoundException {
        final OneTimeEvent eventToUpdate = oneTimeEventRepository
                .findByIdAndUser(eventId, user).orElseThrow(EventNotFoundException::new);

        eventToUpdate.setTitle(newData.title());
        eventToUpdate.setAmount(newData.amount());
        eventToUpdate.setTimestamp(newData.timestamp());
        oneTimeEventRepository.save(eventToUpdate);
    }

    public void delete(final Integer eventId, final User user) throws ResourceNotFoundException {
        final Optional<OneTimeEvent> event = oneTimeEventRepository.findByIdAndUser(eventId, user);
        if (event.isPresent()) {
            oneTimeEventRepository.deleteEvent(eventId, user);
            return;
        }
        throw new EventNotFoundException();
    }
}
