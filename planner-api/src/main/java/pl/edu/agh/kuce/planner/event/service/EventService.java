package pl.edu.agh.kuce.planner.event.service;

import org.springframework.stereotype.Service;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.event.dto.CyclicEventDataInput;
import pl.edu.agh.kuce.planner.event.dto.EventData;
import pl.edu.agh.kuce.planner.event.dto.EventList;
import pl.edu.agh.kuce.planner.event.dto.OneTimeEventDataInput;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEvent;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEventRepository;
import pl.edu.agh.kuce.planner.shared.ResourceNotFoundException;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EventService {
    private final OneTimeEventRepository oneTimeEventRepository;

    public EventService(final OneTimeEventRepository oneTimeEventRepository) {
        this.oneTimeEventRepository = oneTimeEventRepository;
    }

    public EventData create(final OneTimeEventDataInput request, final User user) {
        final OneTimeEvent event = oneTimeEventRepository.save(new OneTimeEvent(request, user));
        return new EventData(event);
    }

    public EventData create(final CyclicEventDataInput request, final User user) {
        final CyclicEvent event = cyclicEventRepository.save(new CyclicEvent(request, user));
        return new EventData(event);
    }

    public EventList list(final User user) {
        final List<EventData> events = oneTimeEventRepository.findByUser(user).stream().map(EventData::new).toList();
        events.addAll(cyclicEventRepository.findByUser(user).stream().map(EventData::new).toList());
        return new EventList(events);
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

    @Transactional
    public void delete(final Integer eventId, final User user) throws ResourceNotFoundException {
        final Optional<OneTimeEvent> event = oneTimeEventRepository.findByIdAndUser(eventId, user);
        if (event.isPresent()) {
            oneTimeEventRepository.deleteEvent(eventId, user);
            return;
        }
        throw new EventNotFoundException();
    }
}
