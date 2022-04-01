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
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    void givenValidCredentials_register_returnsJwt() throws Exception {
        var request = new RegistrationRequestDto("user", "email@example.com", "password");

        String expectedJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9." +
                             "eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjQ4NzYwODAxfQ." +
                             "jDa3CL4InUwyCu_bYHJ4-JfVs5g7t19h3iCjyT6fr1w";

        mockMvc.perform(post("/auth/users").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken", is(expectedJwt)));
    }

    @Test
    @Transactional
    void givenValidCredentials_register_savesUser() throws Exception {
        var request = new RegistrationRequestDto("user", "email@example.com", "password");

        mockMvc.perform(post("/auth/users").contentType(MediaType.APPLICATION_JSON).content(toJson(request)));

        assertThat(userRepository.findOneByNickOrEmail("user")).isPresent();
    }

    @Test
    @Transactional
    void givenInvalidRequestBody_register_returns400() throws Exception {
        var request = new RegistrationRequestDto("us", "email@example.com", "password");

        mockMvc.perform(post("/auth/users").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void givenValidNickPassword_login_returnsJwt() throws Exception {
        userRepository.save(fakeUser());

        var request = new LoginRequestDto("user", "password");

        String expectedJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9." +
                "eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjQ4NzYwODAxfQ." +
                "jDa3CL4InUwyCu_bYHJ4-JfVs5g7t19h3iCjyT6fr1w";

        mockMvc.perform(post("/auth/access-token").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken", is(expectedJwt)));
    }

    @Test
    @Transactional
    void givenValidEmailPassword_login_returnsJwt() throws Exception {
        userRepository.save(fakeUser());

        var request = new LoginRequestDto("email@example.com", "password");

        String expectedJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9." +
                "eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjQ4NzYwODAxfQ." +
                "jDa3CL4InUwyCu_bYHJ4-JfVs5g7t19h3iCjyT6fr1w";

        mockMvc.perform(post("/auth/access-token").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken", is(expectedJwt)));
    }

    @Test
    @Transactional
    void givenInvalidRequestBody_login_returns400() throws Exception {
        userRepository.save(fakeUser());

        var request = new LoginRequestDto("user", "p");

        mockMvc.perform(post("/auth/access-token").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void givenNonExistingLogin_login_returns401() throws Exception {
        userRepository.save(fakeUser());

        var request = new LoginRequestDto("nonExistingNick", "password");

        mockMvc.perform(post("/auth/access-token").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void givenWrongPassword_login_returns401() throws Exception {
        userRepository.save(fakeUser());

        var request = new LoginRequestDto("user", "wrongPassword");

        mockMvc.perform(post("/auth/access-token").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenFakeSecuredEndpoint_requestWithoutJwt_returns401() throws Exception {
        mockMvc.perform(get("/fake-endpoint/fake-path"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenFakeSecuredEndpoint_requestWithJwt_returns404() throws Exception {
        String validToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9." +
                "eyJzdWIiOiJ1c2VyIiwiZXhwIjoyMTQ3NDgzNjQ3fQ." +
                "ZDr5cWMLgEzxAS6kJ6nbMOP0B4R1iY1QeH0CpnWulag";
        String headerValue = "Bearer " + validToken;

        mockMvc.perform(get("/fake-endpoint/fake-path").header("Authorization", headerValue))
                .andExpect(status().isNotFound());
    }

    private String toJson(Object object) throws JsonProcessingException {
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
