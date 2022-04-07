package pl.edu.agh.kuce.planner.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifier.BaseVerification;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Clock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.edu.agh.kuce.planner.auth.persistence.User;

import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    private static final String USER_ID_CLAIM = "userId";

    private final JWTVerifier jwtVerifier;
    private final Algorithm algorithm;
    private final Clock clock;
    private final long accessTokenDurationMillis;

    public JwtService(Clock clock, Algorithm algorithm,
                      @Value("${jwt.access-token-duration-millis}") long accessTokenDurationMillis) {
        jwtVerifier = ((BaseVerification) JWT.require(algorithm)).build(clock);
        this.algorithm = algorithm;
        this.clock = clock;
        this.accessTokenDurationMillis = accessTokenDurationMillis;
    }

    public String createAccessToken(User user) {
        return createToken(user, accessTokenDurationMillis);
    }

    public Optional<User> verifyToken(String token) {
        try {
            var decodedToken = jwtVerifier.verify(token);

            var user = new User();
            user.setId(decodedToken.getClaim(USER_ID_CLAIM).asInt());
            user.setNick(decodedToken.getSubject());

            if (user.getId() == null || user.getNick() == null) {
                return Optional.empty();
            }

            return Optional.of(user);
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }

    private String createToken(User user, long duration) {
        return JWT.create()
                .withSubject(user.getNick())
                .withClaim(USER_ID_CLAIM, user.getId())
                .withExpiresAt(getDateFromNow(duration))
                .sign(algorithm);
    }

    private Date getDateFromNow(long durationMillis) {
        return new Date(clock.getToday().getTime() + durationMillis);
    }
}
