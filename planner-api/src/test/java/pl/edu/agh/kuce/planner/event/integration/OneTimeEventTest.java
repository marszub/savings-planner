package pl.edu.agh.kuce.planner.event.integration;
import org.assertj.core.api.Fail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.auth.persistence.UserRepository;
import pl.edu.agh.kuce.planner.event.dto.OneTimeEventDataInput;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEvent;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEventRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class OneTimeEventTest {
    @Autowired
    private OneTimeEventRepository oneTimeEventRepository;

    @Autowired
    private UserRepository userRepository;

    private User user = new User("nick", "123@321.223", "password");
    private final OneTimeEventDataInput eventData1 =
            new OneTimeEventDataInput("Title1", 201, Instant.now().getEpochSecond());
    private final OneTimeEventDataInput eventData2 =
            new OneTimeEventDataInput("Title2", 301, Instant.now().getEpochSecond());
    private OneTimeEvent event1;
    private OneTimeEvent event2;

    @Test
    @Transactional
    void single_one_time_event_is_properly_saved_in_database() {
        user = userRepository.save(user);
        event1 = new OneTimeEvent(eventData1, user);
        oneTimeEventRepository.save(event1);
        final List<OneTimeEvent> result = oneTimeEventRepository.findByUser(user);
        assertThat(result.get(0)).isEqualTo(event1);
    }

    @Test
    void multiple_one_time_events_are_saved_properly_in_database() {
        user = userRepository.save(user);
        event1 = new OneTimeEvent(eventData1, user);
        event2 = new OneTimeEvent(eventData2, user);
        oneTimeEventRepository.save(event1);
        oneTimeEventRepository.save(event2);
        final List<OneTimeEvent> result = oneTimeEventRepository.findByUser(user);
        assertThat(result.contains(event1)).isTrue();
        assertThat(result.contains(event2)).isTrue();
    }

    @Test
    @Transactional
    void findByIdAndUser_existingEvent() {
        user = userRepository.save(user);
        event1 = new OneTimeEvent(eventData1, user);
        event2 = new OneTimeEvent(eventData2, user);
        oneTimeEventRepository.save(event1);
        oneTimeEventRepository.save(event2);
        final List<OneTimeEvent> result = oneTimeEventRepository.findByUser(user);
        final OneTimeEvent foundEvent2 = result.stream().filter(e -> e == event2).findFirst().orElseGet(() -> {
            Fail.fail("");
            return new OneTimeEvent();
        });
        final OneTimeEvent foundByIdAndUser = oneTimeEventRepository
                .findByIdAndUser(foundEvent2.getId(), user).orElseGet(() -> {
                    Fail.fail("");
                    return new OneTimeEvent();
                });
        assertThat(foundByIdAndUser.getTitle()).isEqualTo(event2.getTitle());
    }

    @Test
    @Transactional
    void findByIdAndUser_notExistingEvent() {
        user = userRepository.save(user);
        final Optional<OneTimeEvent> foundByIdAndUser = oneTimeEventRepository.findByIdAndUser(1, user);
        assertThat(foundByIdAndUser.isEmpty()).isTrue();
    }
}
