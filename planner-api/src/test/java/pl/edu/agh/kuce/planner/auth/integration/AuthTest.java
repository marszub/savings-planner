package pl.edu.agh.kuce.planner.auth.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.kuce.planner.auth.dto.LoginRequestDto;
import pl.edu.agh.kuce.planner.auth.dto.RegistrationRequestDto;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.auth.persistence.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(AuthTestConfig.class)
class AuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void contextLoads() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @Transactional
    void givenValidCredentials_register_returns200() throws Exception {
        final var request = new RegistrationRequestDto("user", "email@example.com", "password");

        mockMvc.perform(post("/api/auth/users").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken", matchesPattern("^.+\\..+\\..+$")));
    }

    @Test
    @Transactional
    void givenValidCredentials_register_savesUser() throws Exception {
        final var request = new RegistrationRequestDto("user", "email@example.com", "password");

        mockMvc.perform(post("/api/auth/users").contentType(MediaType.APPLICATION_JSON).content(toJson(request)));

        assertThat(userRepository.findOneByNickOrEmail("user")).isPresent();
    }

    @Test
    @Transactional
    void givenInvalidRequestBody_register_returns400() throws Exception {
        final var request = new RegistrationRequestDto("us", "email@example.com", "password");

        mockMvc.perform(post("/api/auth/users").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void givenValidNickPassword_login_returns200() throws Exception {
        userRepository.save(fakeUser());

        final var request = new LoginRequestDto("user", "password");

        mockMvc.perform(post("/api/auth/access-token").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken", matchesPattern("^.+\\..+\\..+$")));
    }

    @Test
    @Transactional
    void givenValidEmailPassword_login_returns200() throws Exception {
        userRepository.save(fakeUser());

        final var request = new LoginRequestDto("email@example.com", "password");

        mockMvc.perform(post("/api/auth/access-token").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken", matchesPattern("^.+\\..+\\..+$")));
    }

    @Test
    @Transactional
    void givenInvalidRequestBody_login_returns400() throws Exception {
        userRepository.save(fakeUser());

        final var request = new LoginRequestDto("user", "p");

        mockMvc.perform(post("/api/auth/access-token").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void givenNonExistingLogin_login_returns401() throws Exception {
        userRepository.save(fakeUser());

        final var request = new LoginRequestDto("nonExistingNick", "password");

        mockMvc.perform(post("/api/auth/access-token").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void givenWrongPassword_login_returns401() throws Exception {
        userRepository.save(fakeUser());

        final var request = new LoginRequestDto("user", "wrongPassword");

        mockMvc.perform(post("/api/auth/access-token").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenFakeSecuredApiEndpoint_requestWithoutJwt_returns401() throws Exception {
        mockMvc.perform(get("/api/fake-endpoint/fake-path"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenFakeSecuredApiEndpoint_requestWithJwt_returns404() throws Exception {
        final String validToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9."
                + "eyJzdWIiOiJ1c2VyIiwidXNlcklkIjoxLCJleHAiOjIxNDc0ODM2NDd9."
                + "d5lbk3hpiyvPS1gVsdGwrDzXz_r794CHDUU6sP1lkiU";
        final String headerValue = "Bearer " + validToken;

        mockMvc.perform(get("/api/fake-endpoint/fake-path").header("Authorization", headerValue))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenFakeStaticFileEndpoint_requestWithoutJwt_returns404() throws Exception {
        mockMvc.perform(get("/fake-static/fake-path"))
                .andExpect(status().isNotFound());
    }

    private String toJson(final Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    private User fakeUser() {
        return new User(
                "user",
                "email@example.com",
                "$argon2id$v=19$m=16,t=1,p=1$YWFhYWFhYWE$ISSYATaIOw4blpxBd1KyxTvNzIj/7gBlIxiLeGZ7PRY" // 'password'
        );
    }
}
