package pl.edu.agh.kuce.planner.auth.integration;

import com.auth0.jwt.interfaces.Clock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.Date;

@TestConfiguration
public class AuthTestConfig {

    @Bean
    @Primary
    public Clock fixedClock() {
        return () -> new Date(1648760741000L); //fixed 31.03.2022
    }
}
