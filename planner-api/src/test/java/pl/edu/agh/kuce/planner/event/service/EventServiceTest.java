package pl.edu.agh.kuce.planner.event.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.agh.kuce.planner.auth.dto.LoginRequestDto;
import pl.edu.agh.kuce.planner.auth.dto.RegistrationRequestDto;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.auth.persistence.UserRepository;
import pl.edu.agh.kuce.planner.auth.service.JwtService;
import pl.edu.agh.kuce.planner.event.dto.CreateRequest;
import pl.edu.agh.kuce.planner.event.dto.CreateResponse;
import pl.edu.agh.kuce.planner.event.dto.ListRequest;
import pl.edu.agh.kuce.planner.event.dto.ListResponse;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEvent;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEventRepository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventServiceTest {

    private EventService eventService;

    @Mock
    private OneTimeEventRepository oneTimeEventRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    private final String token1 = "token1";
    private final String nick1 = "nick1";
    private final String title1 = "Title1";
    private final Double amount1 = 1.2;
    private final Timestamp timestamp1 = Timestamp.valueOf("2022-1-1 01:01:01");
    private final User user1 = new User(nick1, nick1, nick1);
    @BeforeEach
    void setUp() {
        user1.setId(1);
        MockitoAnnotations.openMocks(this);

        when(jwtService.tryRetrieveNick(token1)).thenReturn(Optional.of(nick1));
        when(userRepository.findOneByNickOrEmail(nick1)).thenReturn(Optional.of(user1));
        when(oneTimeEventRepository.findByUserId(user1.getId())).thenReturn(List.of(new OneTimeEvent()));

        eventService = new EventService(oneTimeEventRepository, jwtService, userRepository);
    }

    @Test
    void create_returnsSuccessMessage() {
        CreateResponse response = eventService.create(new CreateRequest(token1, title1, amount1, timestamp1));

        assertThat(response.message()).isEqualTo("Ok");
    }

    @Test
    void create_returnsErrorMessage() {
        String token2 = "token2";
        CreateResponse response = eventService.create(new CreateRequest(token2, title1, amount1, timestamp1));

        assertThat(response.message()).isEqualTo("Authentication error");
    }

    @Test
    void create_writeReadEvent() {
        eventService.create(new CreateRequest(token1, title1, amount1, timestamp1));
        ListResponse response = eventService.list(new ListRequest(token1));
        assertThat(response.list().size()).isEqualTo(1);
    }
}
