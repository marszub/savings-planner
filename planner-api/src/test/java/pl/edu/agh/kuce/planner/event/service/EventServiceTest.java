package pl.edu.agh.kuce.planner.event.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.event.dto.ListResponse;
import pl.edu.agh.kuce.planner.event.dto.OneTimeEventData;
import pl.edu.agh.kuce.planner.event.dto.OneTimeEventDataInput;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEvent;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEventRepository;

import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class EventServiceTest {

    private EventService eventService;

    @Mock
    private OneTimeEventRepository oneTimeEventRepository;

    private final String nick1 = "nick1";
    private final String email1 = "nick1@abc.com";
    private final String password1 = "pass1-HASH";
    private final String nick2 = "nick2";
    private final String email2 = "nick2@abc.com";
    private final String password2 = "pass2-HASH";
    private final String title1 = "Title1";
    private final Integer amount1 = 201;
    private final String timestampStr1 = "2022-01-21 01:01:01.0";
    private final Timestamp timestamp1 = Timestamp.valueOf(timestampStr1);
    private final User user1 = new User(nick1, email1, password1);
    private final User user2 = new User(nick2, email2, password2);
    @BeforeEach
    void setUp() {
        user1.setId(1);
        user2.setId(2);
        MockitoAnnotations.openMocks(this);

        final OneTimeEvent event = new OneTimeEvent(user1, title1, amount1, timestamp1);
        event.setId(1);
        when(oneTimeEventRepository.findByUser(user1))
                .thenReturn(List.of(new OneTimeEvent(user1, title1, amount1, timestamp1)));
        when(oneTimeEventRepository.findByUser(user2))
                .thenReturn(List.of());
        when(oneTimeEventRepository.save(any())).thenReturn(event);

        eventService = new EventService(oneTimeEventRepository);
    }

    @Test
    void create_doesNotThrow() {
        Assertions.assertDoesNotThrow(
                () -> eventService.create(new OneTimeEventDataInput(title1, amount1, timestampStr1), user1));
    }

    @Test
    void list_returnsCorrespondingData() {
        final ListResponse response = eventService.list(user1);
        assertThat(response.list().size()).isEqualTo(1);
        final OneTimeEventData foundEvent = response.list().get(0);
        assertThat(foundEvent.title()).isEqualTo(title1);
        assertThat(foundEvent.amount()).isEqualTo(amount1);
        assertThat(foundEvent.timestamp()).isEqualTo(timestampStr1);
    }

    @Test
    void list_notReturnsDataForDifferentUser() {
        final ListResponse response = eventService.list(user2);
        assertThat(response.list().size()).isEqualTo(0);
    }
}
