package pl.edu.agh.kuce.planner.event.integration;
import org.assertj.core.api.Fail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.auth.persistence.UserRepository;
import pl.edu.agh.kuce.planner.event.dto.CyclicEventDataInput;
import pl.edu.agh.kuce.planner.event.persistence.CyclicEvent;
import pl.edu.agh.kuce.planner.event.persistence.CyclicEventRepository;

import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class CyclicEventTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CyclicEventRepository cyclicEventRepository;

    @Autowired
    private UserRepository userRepository;

    private final Long data1 = Instant.parse("2022-03-23T00:00:01.00Z").getEpochSecond();
    private final Long data2 = Instant.parse("2022-05-11T00:00:01.00Z").getEpochSecond();

    private User user = new User("nick", "123@321.223", "password");
    private final CyclicEventDataInput eventData1 =
            new CyclicEventDataInput("Title1", 210, data1, Calendar.WEEK_OF_YEAR, 1);
    private final CyclicEventDataInput eventData2 = new CyclicEventDataInput("Title2", 310, data2, Calendar.MONTH, 2);
    private CyclicEvent event1;
    private CyclicEvent event2;

    @Test
    void cyclicEventFromInterval() {
        event1 = new CyclicEvent(eventData1, user);
        assertThat(event1.getFromInterval(data1, data2).get(0).timestamp()).isEqualTo(data1);
    }

    @Test
    @Transactional
    void singleEventIsSaved() {
        user = userRepository.save(user);
        event1 = new CyclicEvent(eventData1, user);
        cyclicEventRepository.save(event1);
        final List<CyclicEvent> result = cyclicEventRepository.findByUser(user);
        assertThat(result.get(0)).isEqualTo(event1);
    }

    @Test
    void singleEventsAreSaved() {
        user = userRepository.save(user);
        event1 = new CyclicEvent(eventData1, user);
        event2 = new CyclicEvent(eventData2, user);
        cyclicEventRepository.save(event1);
        cyclicEventRepository.save(event2);
        final List<CyclicEvent> result = cyclicEventRepository.findByUser(user);
        assertThat(result.contains(event1)).isTrue();
        assertThat(result.contains(event2)).isTrue();
    }

    @Test
    @Transactional
    void findByIdAndUser_existingEvent() {
        user = userRepository.save(user);
        event1 = new CyclicEvent(eventData1, user);
        event2 = new CyclicEvent(eventData2, user);
        cyclicEventRepository.save(event1);
        cyclicEventRepository.save(event2);
        final List<CyclicEvent> result = cyclicEventRepository.findByUser(user);
        final CyclicEvent foundEvent2 = result.stream().filter(e -> e == event2).findFirst().orElseGet(() -> {
            Fail.fail("");
            return new CyclicEvent();
        });
        final CyclicEvent foundByIdAndUser = cyclicEventRepository
                .findByIdAndUser(foundEvent2.getId(), user).orElseGet(() -> {
                    Fail.fail("");
                    return new CyclicEvent();
                });
        assertThat(foundByIdAndUser.getTitle()).isEqualTo(event2.getTitle());
    }

    @Test
    @Transactional
    void findByIdAndUser_notExistingEvent() {
        user = userRepository.save(user);
        final Optional<CyclicEvent> foundByIdAndUser = cyclicEventRepository.findByIdAndUser(1, user);
        assertThat(foundByIdAndUser.isEmpty()).isTrue();
    }
}
