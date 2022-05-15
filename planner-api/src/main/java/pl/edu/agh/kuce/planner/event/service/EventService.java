package pl.edu.agh.kuce.planner.event.service;

import org.springframework.stereotype.Service;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.event.dto.EventData;
import pl.edu.agh.kuce.planner.event.dto.EventDataInput;
import pl.edu.agh.kuce.planner.event.dto.EventList;
import pl.edu.agh.kuce.planner.event.dto.EventTimestamp;
import pl.edu.agh.kuce.planner.event.dto.EventTimestampList;
import pl.edu.agh.kuce.planner.event.dto.TimestampListRequest;
import pl.edu.agh.kuce.planner.event.persistence.CyclicEvent;
import pl.edu.agh.kuce.planner.event.persistence.CyclicEventRepository;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEvent;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEventRepository;
import pl.edu.agh.kuce.planner.shared.InvalidDataException;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {
    private final OneTimeEventRepository oneTimeEventRepository;
    private final CyclicEventRepository cyclicEventRepository;

    public EventService(final OneTimeEventRepository oneTimeEventRepository,
                        final CyclicEventRepository cyclicEventRepository) {
        this.oneTimeEventRepository = oneTimeEventRepository;
        this.cyclicEventRepository = cyclicEventRepository;
    }

    public EventData create(final EventDataInput eventData, final User user) {
        if (!eventData.isValid()) {
            throw new InvalidDataException("Create event request with wrong data");
        }
        if (eventData.isCyclic()) {
            final CyclicEvent event = cyclicEventRepository.save(new CyclicEvent(eventData, user));
            return new EventData(event);
        }
        final OneTimeEvent event = oneTimeEventRepository.save(new OneTimeEvent(eventData, user));
        return new EventData(event);
    }

    public EventList list(final User user) {
        final List<EventData> events =
                oneTimeEventRepository.findByUser(user).stream().map(EventData::new).collect(Collectors.toList());
        events.addAll(cyclicEventRepository.findByUser(user).stream().map(EventData::new).toList());
        return new EventList(events);
    }

    public EventTimestampList getFollowingEventTimestamps(final TimestampListRequest request, final User user) {
        final List<EventTimestamp> eventTimestamps = getFollowingOneTimeEventTimestamps(request, user);
        eventTimestamps.addAll(getFollowingCyclicEventTimestamps(request, user));
        return new EventTimestampList(pickFirstNDistinct(eventTimestamps, request.eventsNum()));
    }

    public void update(final EventData eventData, final Integer eventId, final User user) {
        if (eventData.isCyclic()) {
            updateCyclic(eventData, eventId, user);
        } else {
            updateOneTime(eventData, eventId, user);
        }
    }

    @Transactional
    public void delete(final Integer eventId, final User user) {
        final Optional<OneTimeEvent> oneTimeEvent = oneTimeEventRepository.findByIdAndUser(eventId, user);
        final Optional<CyclicEvent> cyclicEvent = cyclicEventRepository.findByIdAndUser(eventId, user);
        if (cyclicEvent.isEmpty() && oneTimeEvent.isEmpty()) {
            throw new EventNotFoundException();
        }
        if (oneTimeEvent.isPresent()) {
            oneTimeEventRepository.deleteEvent(eventId, user);
        }
        if (cyclicEvent.isPresent()) {
            cyclicEventRepository.deleteEvent(eventId, user);
        }
    }

    private List<EventTimestamp> pickFirstNDistinct(final List<EventTimestamp> original, final Integer n) {
        final List<EventTimestamp> followingNEvents = new LinkedList<>();
        Integer distinctEventsToTake = n;
        Long lastTimestamp = null;

        for (EventTimestamp event
                : original.stream().sorted(Comparator.comparingLong(EventTimestamp::timestamp)).toList()) {
            if (!Objects.equals(lastTimestamp, event.timestamp())) {
                lastTimestamp = event.timestamp();
                distinctEventsToTake -= 1;
            }
            if (distinctEventsToTake < 0) {
                break;
            }
            followingNEvents.add(event);
        }
        return followingNEvents;
    }

    private List<EventTimestamp> getFollowingOneTimeEventTimestamps(final TimestampListRequest request,
                                                                    final User user) {
        final List<EventTimestamp> followingEvents = oneTimeEventRepository
                .findByUser(user).stream()
                .filter(event -> event.getTimestamp() > request.start())
                .map(EventTimestamp::new)
                .toList();
        return pickFirstNDistinct(followingEvents, request.eventsNum());
    }

    private List<EventTimestamp> getFollowingCyclicEventTimestamps(final TimestampListRequest request,
                                                                   final User user) {

        final List<CyclicEvent> cyclicEvents = cyclicEventRepository.findByUser(user);
        List<EventTimestamp> timestamps = new LinkedList<>();
        for (CyclicEvent event : cyclicEvents) {
            timestamps.addAll(event.getFollowingN(request.start(), request.eventsNum()));
            timestamps = pickFirstNDistinct(timestamps, request.eventsNum());
        }
        return timestamps;
    }

    private void updateCyclic(final EventData eventData, final Integer eventId, final User user) {
        final CyclicEvent eventToUpdate = cyclicEventRepository
                .findByIdAndUser(eventId, user).orElseThrow(EventNotFoundException::new);

        eventToUpdate.setTitle(eventData.title());
        eventToUpdate.setAmount(eventData.amount());
        eventToUpdate.setBegin(eventData.begin());
        eventToUpdate.setCycleBase(eventData.cycleBase());
        eventToUpdate.setCycleLength(eventData.cycleLength());
        cyclicEventRepository.save(eventToUpdate);
    }

    private void updateOneTime(final EventData eventData, final Integer eventId, final User user) {
        final OneTimeEvent eventToUpdate = oneTimeEventRepository
                .findByIdAndUser(eventId, user).orElseThrow(EventNotFoundException::new);

        eventToUpdate.setTitle(eventData.title());
        eventToUpdate.setAmount(eventData.amount());
        eventToUpdate.setTimestamp(eventData.timestamp());
        oneTimeEventRepository.save(eventToUpdate);
    }
}
