package pl.edu.agh.kuce.planner.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final Clock clock;
    private final long accessTokenDurationMillis;

    public JwtService(Algorithm algorithm, Clock clock,
                      @Value("${jwt.access-token-duration-millis}") long accessTokenDurationMillis) {
        this.algorithm = algorithm;
        this.clock = clock;
        this.accessTokenDurationMillis = accessTokenDurationMillis;
    }

    public String createAccessToken(String nick) {
        return createToken(nick, accessTokenDurationMillis);
    }

    public Optional<String> tryRetrieveNick(String token) {
        try {
            return Optional.ofNullable(JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject());
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }

    private String createToken(String nick, long duration) {
        return JWT.create()
                .withSubject(nick)
                .withExpiresAt(getDateFromNow(duration))
                .sign(algorithm);
    }

    private Date getDateFromNow(long durationMillis) {
        return new Date(clock.millis() + durationMillis);
    }
}
