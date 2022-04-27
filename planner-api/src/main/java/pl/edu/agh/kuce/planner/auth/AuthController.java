package pl.edu.agh.kuce.planner.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.kuce.planner.auth.dto.AuthResponseDto;
import pl.edu.agh.kuce.planner.auth.dto.LoginRequestDto;
import pl.edu.agh.kuce.planner.auth.dto.RegistrationRequestDto;
import pl.edu.agh.kuce.planner.auth.service.AuthService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDto register(@Valid @RequestBody final RegistrationRequestDto request) {
        return authService.register(request);
    }

    @PostMapping("/access-token")
    public AuthResponseDto login(@Valid @RequestBody final LoginRequestDto request) {
        return authService.login(request);
    }
}
