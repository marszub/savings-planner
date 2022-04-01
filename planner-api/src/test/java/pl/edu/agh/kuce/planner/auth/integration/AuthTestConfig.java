package pl.edu.agh.kuce.planner.auth.integration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@TestConfiguration
public class AuthTestConfig {

    @Bean
    @Primary
    public Clock fixedClock() {
        return Clock.fixed(Instant.ofEpochSecond(1648760741), ZoneId.of("Europe/Warsaw")); //fixed 31.03.2022
    }
}
