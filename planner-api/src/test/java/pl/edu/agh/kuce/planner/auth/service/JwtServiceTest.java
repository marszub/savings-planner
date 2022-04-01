package pl.edu.agh.kuce.planner.auth.service;

import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                Algorithm.HMAC256("fakeSecret"),
                Clock.fixed(Instant.ofEpochSecond(1648760741), ZoneId.of("Europe/Warsaw")), //fixed 31.03.2022
                60000
        );
    }

    @Test
    void givenValidJwt_tryRetrieveNick_nickIsPresent() {
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJzdWIiOiJ1c2VyMSIsImV4cCI6MjE0NzQ4MzY0N30." +
                "L-OzhC0g5TVQr0tcg75S1pGn57vr1MjumtHT_atU3Ag";

        var nick = jwtService.tryRetrieveNick(jwt);

        assertThat(nick).isPresent();
        assertThat(nick.get()).isEqualTo("user1");
    }

    @Test
    void givenInvalidJwt_tryRetrieveNick_nickIsEmpty() {
        String jwt = "sadasfgasfasfafasf." +
                "fasfasfsaafsfsafasfas." +
                "asfldsaklmadsklmdsklaafs";

        var nick = jwtService.tryRetrieveNick(jwt);

        assertThat(nick).isEmpty();
    }

    @Test
    void givenExpiredJwt_tryRetrieveNick_nickIsEmpty() {
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJzdWIiOiJ1c2VyMSIsImV4cCI6MX0." +
                "UHL3UhDJ49UfA1edalNCbtiRScdhU8psgzMrSs7gR-4";

        var nick = jwtService.tryRetrieveNick(jwt);

        assertThat(nick).isEmpty();
    }

    @Test
    void givenNick_createAccessToken_returnsToken() {
        String expectedJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9." +
                "eyJzdWIiOiJ1c2VyMSIsImV4cCI6MTY0ODc2MDgwMX0." +
                "rf5LmtR4iMmkjWbWZqbqG8uKrlnDuWgaRsIxaSiyEAw";

        String token = jwtService.createAccessToken("user1");

        assertThat(token).isEqualTo(expectedJwt);
    }
}
