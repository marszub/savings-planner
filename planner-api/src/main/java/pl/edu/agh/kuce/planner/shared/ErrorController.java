package pl.edu.agh.kuce.planner.shared;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler {

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<TextResponseDto> handleBadCredentials() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new TextResponseDto("Wrong login or password"));
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<TextResponseDto> handleAuthenticationError(final Exception e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new TextResponseDto("User unauthorized"));
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<TextResponseDto> handleDBConflict() {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new TextResponseDto("Violating data integrity"));
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<TextResponseDto> resourceNotFound(final ResourceNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new TextResponseDto(e.getMessage()));
    }
}
