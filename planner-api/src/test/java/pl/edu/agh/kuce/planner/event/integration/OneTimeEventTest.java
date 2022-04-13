package pl.edu.agh.kuce.planner.event.integration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.auth.persistence.UserRepository;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEvent;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEventRepository;

import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class OneTimeEventTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OneTimeEventRepository oneTimeEventRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @Transactional
    void single_one_time_event_is_properly_saved_in_database() {
        final User user = new User("TEST", "TEST", "TEST");
        userRepository.save(user);
        final OneTimeEvent testEvent = new OneTimeEvent(user, "test", 110,
                new Timestamp(System.currentTimeMillis()));
        oneTimeEventRepository.save(testEvent);
        final List<OneTimeEvent> result = oneTimeEventRepository.findByUser(user);
        assertThat(result.get(0)).isEqualTo(testEvent);
    }

    @Test
    @Transactional
    void multiple_one_time_events_are_saved_properly_in_database() {
        final User user = new User("TEST", "TEST", "TEST");
        userRepository.save(user);
        final OneTimeEvent testEvent1 = new OneTimeEvent(user, "test1", 10,
                new Timestamp(System.currentTimeMillis()));
        final OneTimeEvent testEvent2 = new OneTimeEvent(user, "test2", 120,
                new Timestamp(System.currentTimeMillis()));
        oneTimeEventRepository.save(testEvent1);
        oneTimeEventRepository.save(testEvent2);
        final List<OneTimeEvent> result = oneTimeEventRepository.findByUser(user);
        assertThat(result.get(0)).isEqualTo(testEvent1);
        assertThat(result.get(1)).isEqualTo(testEvent2);
    }
}
