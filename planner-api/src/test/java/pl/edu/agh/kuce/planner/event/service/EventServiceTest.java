package pl.edu.agh.kuce.planner.event.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.auth.persistence.UserRepository;
import pl.edu.agh.kuce.planner.event.dto.ListResponse;
import pl.edu.agh.kuce.planner.event.dto.EventData;
import pl.edu.agh.kuce.planner.event.dto.OneTimeEventDataInput;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEventRepository;

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

    private final String nick1 = "nick1";
    private final String email1 = "nick1@abc.com";
    private final String password1 = "pass1-HASH";
    private final String nick2 = "nick2";
    private final String email2 = "nick2@abc.com";
    private final String password2 = "pass2-HASH";
    private final String title1 = "Title1";
    private final String title2 = "Title2";
    private final Integer amount1 = 201;
    private final Integer amount2 = 301;
    private final Long timestampSpr1 = 1650989137L;
    private final Long timestampSpr2 = 1650980000L;
    private User user1 = new User(nick1, email1, password1);
    private User user2 = new User(nick2, email2, password2);

    private final OneTimeEventDataInput dataInput1 = new OneTimeEventDataInput(title1, amount1, timestampSpr1);
    private final OneTimeEventDataInput dataInput2 = new OneTimeEventDataInput(title2, amount2, timestampSpr2);
    @BeforeEach
    void setUp() {
        eventService = new EventService(oneTimeEventRepository);
    }

    @Test
    @Transactional
    void createOneTimeEvent_doesNotThrow() {
        user1 = userRepository.save(user1);
        assertThatNoException().isThrownBy(() ->
                eventService.create(new OneTimeEventDataInput(title1, amount1, timestampSpr1), user1));
    }

    @Test
    @Transactional
    void list_returnsCorrespondingData() {
        user1 = userRepository.save(user1);
        eventService.create(dataInput1, user1);
        final ListResponse response = eventService.list(user1);
        assertThat(response.events().size()).isEqualTo(1);
        final EventData foundEvent = response.events().get(0);
        assertThat(foundEvent.title()).isEqualTo(title1);
        assertThat(foundEvent.amount()).isEqualTo(amount1);
        assertThat(foundEvent.timestamp()).isEqualTo(timestampSpr1);
    }

    @Test
    @Transactional
    void list_notReturnsDataForDifferentUser() {
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
        final ListResponse response = eventService.list(user2);
        assertThat(response.events().size()).isEqualTo(0);
    }

    @Test
    @Transactional
    void updateOneTimeEvent() {
        user1 = userRepository.save(user1);
        final EventData eventData = eventService.create(dataInput1, user1);
        eventService.update(dataInput2, eventData.id(), user1);
        final EventData updated = eventService.list(user1).events().get(0);
        assertThat(updated.id()).isEqualTo(eventData.id());
        assertThat(updated.amount()).isEqualTo(amount2);
        assertThat(updated.title()).isEqualTo(title2);
        assertThat(updated.timestamp()).isEqualTo(timestampSpr2);
    }

    @Test
    @Transactional
    void createAndDeleteOneTimeEvent() {
        user1 = userRepository.save(user1);
        final EventData eventData = eventService.create(dataInput1, user1);
        eventService.delete(eventData.id(), user1);
        assertThat(eventService.list(user1).events().size()).isEqualTo(0);
    }
}
