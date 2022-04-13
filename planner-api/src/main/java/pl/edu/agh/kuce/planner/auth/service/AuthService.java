package pl.edu.agh.kuce.planner.auth.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.agh.kuce.planner.auth.dto.AuthResponseDto;
import pl.edu.agh.kuce.planner.auth.dto.LoginRequestDto;
import pl.edu.agh.kuce.planner.auth.dto.RegistrationRequestDto;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.auth.persistence.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtUtils;

    public AuthService(
            final UserRepository userRepository,
            final PasswordEncoder passwordEncoder,
            final JwtService jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public AuthResponseDto register(final RegistrationRequestDto request) {
        final var user = userRepository.save(
                new User(request.nick(), request.email(), passwordEncoder.encode(request.password()))
        );

        return new AuthResponseDto(jwtUtils.createAccessToken(user));
    }

    public AuthResponseDto login(final LoginRequestDto request) {
        final var user = userRepository.findOneByNickOrEmail(request.login());
        if (user.isEmpty()) {
            throw new BadCredentialsException("Wrong login");
        }

        if (!passwordEncoder.matches(request.password(), user.get().getPasswordHash())) {
            throw new BadCredentialsException("Wrong password");
        }

        return new AuthResponseDto(jwtUtils.createAccessToken(user.get()));
    }
}
