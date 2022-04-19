package pl.edu.agh.kuce.planner.auth.service;

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
import pl.edu.agh.kuce.planner.balance.service.BalanceService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private BalanceService balanceService;

    private User fakeUser;

    @BeforeEach
    void setUp() {
        fakeUser = new User("user1", "email1@example.com", "hashedPassword1");

        MockitoAnnotations.openMocks(this);

        when(userRepository.save(any())).thenReturn(fakeUser);
        when(userRepository.findOneByNickOrEmail(fakeUser.getNick())).thenReturn(Optional.of(fakeUser));
        when(userRepository.findOneByNickOrEmail(fakeUser.getEmail())).thenReturn(Optional.of(fakeUser));

        when(passwordEncoder.encode("password1")).thenReturn(fakeUser.getPasswordHash());
        when(passwordEncoder.matches("password1", "hashedPassword1")).thenReturn(true);

        when(jwtService.createAccessToken(fakeUser)).thenReturn("token1");

        authService = new AuthService(userRepository, passwordEncoder, jwtService, balanceService);
    }

    @Test
    void givenRequestDto_register_savesUser() {
        authService.register(
                new RegistrationRequestDto(fakeUser.getNick(), fakeUser.getEmail(), "password1")
        );

        verify(userRepository).save(Mockito.argThat(u ->
                u.getNick().equals(fakeUser.getNick())
                && u.getEmail().equals(fakeUser.getEmail())
                && u.getPasswordHash().equals(fakeUser.getPasswordHash())
        ));
    }

    @Test
    void givenRequestDto_register_returnsJwt() {
        final var response = authService.register(
                new RegistrationRequestDto(fakeUser.getNick(), fakeUser.getEmail(), "password1")
        );

        assertThat(response.accessToken()).isEqualTo("token1");
    }

    @Test
    void givenExistingNickAndValidPassword_login_returnsJwt() {
        final var response = authService.login(
                new LoginRequestDto(fakeUser.getNick(), "password1")
        );

        assertThat(response.accessToken()).isEqualTo("token1");
    }

    @Test
    void givenExistingEmailAndValidPassword_login_returnsJwt() {
        final var response = authService.login(
                new LoginRequestDto(fakeUser.getEmail(), "password1")
        );

        assertThat(response.accessToken()).isEqualTo("token1");
    }

    @Test
    void givenNonExistingLogin_login_throwsException() {
        assertThatExceptionOfType(BadCredentialsException.class)
                .isThrownBy(() -> authService.login(new LoginRequestDto("nonExistingLogin", "password1")));
    }

    @Test
    void givenWrongPassword_login_throwsException() {
        assertThatExceptionOfType(BadCredentialsException.class)
                .isThrownBy(() -> authService.login(new LoginRequestDto(fakeUser.getNick(), "wrongPassword")));
    }
}
