package pl.edu.agh.kuce.planner.auth.config;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class JwtConfig {

    @Bean
    public Algorithm algorithm(@Value("${jwt.secret}") String secret) {
        return Algorithm.HMAC256(secret);
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
