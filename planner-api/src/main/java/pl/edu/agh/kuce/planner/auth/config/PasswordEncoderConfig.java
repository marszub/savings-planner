package pl.edu.agh.kuce.planner.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder(
            @Value("argon2.salt-length") int saltLength,
            @Value("argon2.hash-length") int hashLength,
            @Value("argon2.parallelism") int parallelism,
            @Value("argon2.memory") int memory,
            @Value("argon2.iterations") int iterations
    ) {
        return new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iterations);
    }
}
