package pl.edu.agh.kuce.planner.event.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.auth.persistence.UserRepository;
import pl.edu.agh.kuce.planner.event.dto.CyclicEventDataInput;
import pl.edu.agh.kuce.planner.event.dto.EventData;
import pl.edu.agh.kuce.planner.event.dto.EventList;
import pl.edu.agh.kuce.planner.event.dto.EventTimestampList;
import pl.edu.agh.kuce.planner.event.dto.OneTimeEventDataInput;
import pl.edu.agh.kuce.planner.event.dto.TimestampListRequest;
import pl.edu.agh.kuce.planner.event.persistence.CyclicEventRepository;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEventRepository;

import java.util.Objects;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@SpringBootTest
@AutoConfigureMockMvc
class EventServiceTest {

    private EventService eventService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OneTimeEventRepository oneTimeEventRepository;

    @Autowired
    private CyclicEventRepository cyclicEventRepository;

    private final String nick1 = "nick1";
    private final String email1 = "nick1@abc.com";
    private final String password1 = "pass1-HASH";
    private final String nick2 = "nick2";
    private final String email2 = "nick2@abc.com";
    private final String password2 = "pass2-HASH";
    private final String title1 = "Title1";
    private final String title2 = "Title2";
    private final String title3 = "Title3";
    private final String title4 = "Title4";
    private final Integer amount1 = 201;
    private final Integer amount2 = 301;
    private final Integer amount3 = 401;
    private final Integer amount4 = 501;
    private final Long timestampSpr1 = 1650989137L;
    private final Long timestampSpr2 = 1650980000L;
    private final Long timestampSpr3 = 1650980032L;
    private final Long timestampSpr4 = 1650980564L;
    private final Integer cycleBase1 = DAY_OF_MONTH;
    private final Integer cycleBase2 = DAY_OF_WEEK;
    private final Integer cycleLength1 = 3;
    private final Integer cycleLength2 = 1;
    private User user1 = new User(nick1, email1, password1);
    private User user2 = new User(nick2, email2, password2);

    private final OneTimeEventDataInput oneTimeEventDataInput1 =
            new OneTimeEventDataInput(title1, amount1, timestampSpr1);
    private final OneTimeEventDataInput oneTimeEventDataInput2 =
            new OneTimeEventDataInput(title2, amount2, timestampSpr2);
    private final OneTimeEventDataInput oneTimeEventDataInput3 =
            new OneTimeEventDataInput(title3, amount3, timestampSpr3);
    private final OneTimeEventDataInput oneTimeEventDataInput4 =
            new OneTimeEventDataInput(title4, amount4, timestampSpr4);

    private final CyclicEventDataInput cyclicEventDataInput1 =
            new CyclicEventDataInput(title1, amount1, timestampSpr1, cycleBase1, cycleLength1);

    @BeforeEach
    void setUp() {
        eventService = new EventService(oneTimeEventRepository, cyclicEventRepository);
    }

    @Test
    @Transactional
    void createOneTimeEvent_doesNotThrow() {
        user1 = userRepository.save(user1);
        assertThatNoException().isThrownBy(() -> eventService.create(oneTimeEventDataInput1, user1));
    }

    @Test
    @Transactional
    void createCyclicEvent_doesNotThrow() {
        user1 = userRepository.save(user1);
        assertThatNoException().isThrownBy(() -> eventService.create(cyclicEventDataInput1, user1));
    }

    @Test
    @Transactional
    void list_returnsCorrespondingOneTimeEventData() {
        user1 = userRepository.save(user1);
        eventService.create(oneTimeEventDataInput1, user1);
        final EventList response = eventService.list(user1);
        assertThat(response.events().size()).isEqualTo(1);
        final EventData foundEvent = response.events().get(0);
        assertThat(foundEvent.title()).isEqualTo(title1);
        assertThat(foundEvent.amount()).isEqualTo(amount1);
        assertThat(foundEvent.isCyclic()).isFalse();
        assertThat(foundEvent.timestamp()).isEqualTo(timestampSpr1);
    }

    @Test
    @Transactional
    void list_returnsCorrespondingCyclicEventData() {
        user1 = userRepository.save(user1);
        eventService.create(cyclicEventDataInput1, user1);
        final EventList response = eventService.list(user1);
        assertThat(response.events().size()).isEqualTo(1);
        final EventData foundEvent = response.events().get(0);
        assertThat(foundEvent.title()).isEqualTo(title1);
        assertThat(foundEvent.amount()).isEqualTo(amount1);
        assertThat(foundEvent.isCyclic()).isTrue();
        assertThat(foundEvent.begin()).isEqualTo(timestampSpr1);
    }

    @Test
    @Transactional
    void list_notReturnsDataForDifferentUser() {
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
        final EventList response = eventService.list(user2);
        assertThat(response.events().size()).isEqualTo(0);
    }

    @Test
    @Transactional
    void updateOneTimeEvent() {
        user1 = userRepository.save(user1);
        final EventData eventData = eventService.create(oneTimeEventDataInput1, user1);
        eventService.update(oneTimeEventDataInput2, eventData.id(), user1);
        final EventData updated = eventService.list(user1).events().get(0);
        assertThat(updated.id()).isEqualTo(eventData.id());
        assertThat(updated.amount()).isEqualTo(amount2);
        assertThat(updated.title()).isEqualTo(title2);
        assertThat(updated.isCyclic()).isFalse();
        assertThat(updated.timestamp()).isEqualTo(timestampSpr2);
    }

    @Test
    @Transactional
    void createAndDeleteOneTimeEvent() {
        user1 = userRepository.save(user1);
        final EventData eventData = eventService.create(oneTimeEventDataInput1, user1);
        eventService.delete(eventData.id(), user1);
        assertThat(eventService.list(user1).events().size()).isEqualTo(0);
    }

    @Test
    @Transactional
    void getFollowingOneTimeEventTimestamps_differentDates() {
        user1 = userRepository.save(user1);
        eventService.create(oneTimeEventDataInput1, user1);
        eventService.create(oneTimeEventDataInput2, user1);
        eventService.create(oneTimeEventDataInput3, user1);
        eventService.create(oneTimeEventDataInput4, user1);
        assertThat(oneTimeEventRepository.findByUser(user1).size()).isEqualTo(4);
        final EventTimestampList result =
                eventService.getFollowingEventTimestamps(new TimestampListRequest(timestampSpr2, 2), user1);
        assertThat(result.eventTimestamps().size()).isEqualTo(2);
        assertThat(result.eventTimestamps().get(0).timestamp()).isEqualTo(timestampSpr3);
        assertThat(result.eventTimestamps().get(1).timestamp()).isEqualTo(timestampSpr4);
    }

    @Test
    @Transactional
    void getFollowingOneTimeEventTimestamps_differentDatesNotEnoughEvents() {
        user1 = userRepository.save(user1);
        eventService.create(oneTimeEventDataInput2, user1);
        eventService.create(oneTimeEventDataInput3, user1);
        final EventTimestampList result =
                eventService.getFollowingEventTimestamps(new TimestampListRequest(timestampSpr2, 2), user1);
        assertThat(result.eventTimestamps().size()).isEqualTo(1);
        assertThat(result.eventTimestamps().get(0).timestamp()).isEqualTo(timestampSpr3);
    }

    @Test
    @Transactional
    void getFollowingOneTimeEventTimestamps_duplicates() {
        user1 = userRepository.save(user1);
        final OneTimeEventDataInput oneTimeEventDataInput3duplicate =
                new OneTimeEventDataInput("Title3Duplicate", 2000, timestampSpr3);
        eventService.create(oneTimeEventDataInput1, user1);
        eventService.create(oneTimeEventDataInput2, user1);
        eventService.create(oneTimeEventDataInput3, user1);
        eventService.create(oneTimeEventDataInput3duplicate, user1);
        eventService.create(oneTimeEventDataInput4, user1);
        final EventTimestampList result =
                eventService.getFollowingEventTimestamps(new TimestampListRequest(timestampSpr2, 2), user1);
        assertThat(result.eventTimestamps().size()).isEqualTo(3);
        assertThat(result.eventTimestamps().get(0).timestamp()).isEqualTo(timestampSpr3);
        assertThat(result.eventTimestamps().get(1).timestamp()).isEqualTo(timestampSpr3);
        assertThat(result.eventTimestamps())
                .anyMatch(eventTimestamp -> Objects.equals(eventTimestamp.title(), "Title3Duplicate"));
        assertThat(result.eventTimestamps())
                .anyMatch(eventTimestamp -> Objects.equals(eventTimestamp.title(), "Title3"));
        assertThat(result.eventTimestamps().get(2).timestamp()).isEqualTo(timestampSpr4);
    }

    @Test
    @Transactional
    void getFollowingOneTimeEventTimestamps_duplicatesAtEnd() {
        user1 = userRepository.save(user1);
        final OneTimeEventDataInput oneTimeEventDataInput4duplicate =
                new OneTimeEventDataInput("Title4Duplicate", 2000, timestampSpr4);
        eventService.create(oneTimeEventDataInput1, user1);
        eventService.create(oneTimeEventDataInput2, user1);
        eventService.create(oneTimeEventDataInput3, user1);
        eventService.create(oneTimeEventDataInput4duplicate, user1);
        eventService.create(oneTimeEventDataInput4, user1);
        final EventTimestampList result =
                eventService.getFollowingEventTimestamps(new TimestampListRequest(timestampSpr2, 2), user1);
        assertThat(result.eventTimestamps().size()).isEqualTo(3);
        assertThat(result.eventTimestamps().get(0).timestamp()).isEqualTo(timestampSpr3);
        assertThat(result.eventTimestamps().get(1).timestamp()).isEqualTo(timestampSpr4);
        assertThat(result.eventTimestamps().get(2).timestamp()).isEqualTo(timestampSpr4);
        assertThat(result.eventTimestamps())
                .anyMatch(eventTimestamp -> Objects.equals(eventTimestamp.title(), "Title3"));
        assertThat(result.eventTimestamps())
                .anyMatch(eventTimestamp -> Objects.equals(eventTimestamp.title(), "Title4Duplicate"));
        assertThat(result.eventTimestamps())
                .anyMatch(eventTimestamp -> Objects.equals(eventTimestamp.title(), "Title4"));
    }
}
