package pl.edu.agh.kuce.planner.balance.service;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.balance.persistence.BalanceRepository;

public class BalanceServiceTest {

    @Mock
    private final BalanceRepository balanceRepository;

    private final BalanceService balanceService;

    private final String nick = "nick";
    private final String mail = "mail@mail.mail";
    private final String password = "password";
    private final Integer mainBalance = 10000;
    private final User user = new User(nick, mail, password);

    private final String nick2 = "nick2";
    private final String mail2 = "mail2@mail.mail";
    private final String password2 = "password2";
    private final Integer mainBalance2 = 12345;
    private final User user2 = new User(nick2, mail2, password2);

    @BeforeEach
    void setUp() {

    }


}
