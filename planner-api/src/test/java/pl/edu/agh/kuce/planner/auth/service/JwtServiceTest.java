package pl.edu.agh.kuce.planner.auth.service;

import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.agh.kuce.planner.auth.persistence.User;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                () -> new Date(1648760741000L), //fixed 31.03.2022
                Algorithm.HMAC256("fakeSecret"),
                60000
        );
    }

    @Test
    void givenValidJwt_verifyToken_userIsPresent() {
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9."
                + "eyJzdWIiOiJ1c2VyMSIsImV4cCI6MjE0NzQ4MzY0NywidXNlcklkIjoxfQ."
                + "0VtPh11XONzdozSYi5Xp1AA8vMSzhfbEuLXen_dR_II";

        var user = jwtService.verifyToken(jwt);

        assertThat(user).isPresent();
        assertThat(user.get().getId()).isEqualTo(1);
        assertThat(user.get().getNick()).isEqualTo("user1");
    }

    @Test
    void givenInvalidJwt_verifyToken_userIsEmpty() {
        String jwt = "sadasfgasfasfafasf."
                + "fasfasfsaafsfsafasfas."
                + "asfldsaklmadsklmdsklaafs";

        var user = jwtService.verifyToken(jwt);

        assertThat(user).isEmpty();
    }

    @Test
    void givenExpiredJwt_verifyToken_userIsEmpty() {
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9."
                + "eyJzdWIiOiJ1c2VyMSIsInVzZXJJZCI6MSwiZXhwIjoxNjQ4NzYwNzQwfQ."
                + "IhOVEyDGHSqrt7mcpdvsW-7KC-QNgcbkhR4figQ2IZU";

        var user = jwtService.verifyToken(jwt);

        assertThat(user).isEmpty();
    }

    @Test
    void givenNick_createAccessToken_returnsToken() {
        String expectedJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9."
                + "eyJzdWIiOiJ1c2VyMSIsImV4cCI6MTY0ODc2MDgwMSwidXNlcklkIjoxfQ."
                + "frgIQ_4mnd5SiGYYZdmkWosVxqIN5gRJEGh6diDuNnY";

        var user = new User("user1", "", "");
        user.setId(1);

        String token = jwtService.createAccessToken(user);

        assertThat(token).isEqualTo(expectedJwt);
    }
}
