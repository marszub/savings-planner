package pl.edu.agh.kuce.planner.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.edu.agh.kuce.planner.auth.service.JwtService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String AUTH_HEADER_NAME = "Authorization";
    public static final String AUTH_HEADER_PREFIX = "Bearer ";

    private final JwtService jwtUtils;

    public JwtAuthFilter(final JwtService jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(AUTH_HEADER_NAME);
        if (authHeader != null && authHeader.startsWith(AUTH_HEADER_PREFIX)) {
            verifyAuthHeader(authHeader);
        }
        filterChain.doFilter(request, response);
    }

    private void verifyAuthHeader(final String authHeader) {
        final String token = authHeader.replaceFirst(AUTH_HEADER_PREFIX, "");
        final var user = jwtUtils.verifyToken(token);
        if (user.isEmpty()) {
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.get(), null, List.of())
        );
    }
}
