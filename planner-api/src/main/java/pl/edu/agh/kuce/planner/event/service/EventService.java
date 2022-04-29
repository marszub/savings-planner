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

    public OneTimeEventData update(final OneTimeEventDataInput newData,
                                   final Integer eventId,
                                   final User user) throws ResourceNotFoundException {
        final Optional<OneTimeEvent> foundEvent = oneTimeEventRepository.getEventById(eventId, user);
        if (foundEvent.isEmpty()) {
            throw new EventNotFoundException();
        }
        final OneTimeEvent previous = foundEvent.get();

        if (!previous.getTitle().equals(newData.title())) {
            oneTimeEventRepository.updateTitle(newData.title(), eventId, user);
        }
        if (!previous.getAmount().equals(newData.amount())) {
            oneTimeEventRepository.updateAmount(newData.amount(), eventId, user);
        }
        if (!previous.getTimestamp().equals(newData.timestamp())) {
            oneTimeEventRepository.updateTimestamp(newData.timestamp(), eventId, user);
        }

        final Optional<OneTimeEvent> updatedEvent = oneTimeEventRepository.getEventById(eventId, user);
        if (updatedEvent.isEmpty()) {
            throw new EventNotFoundException();
        }
        return new OneTimeEventData(updatedEvent.get());
    }

    public void delete(final Integer eventId, final User user) throws ResourceNotFoundException {
        final Optional<OneTimeEvent> event = oneTimeEventRepository.getEventById(eventId, user);
        if (event.isPresent()) {
            oneTimeEventRepository.deleteEvent(eventId, user);
            return;
        }
        throw new EventNotFoundException();
    }
}
