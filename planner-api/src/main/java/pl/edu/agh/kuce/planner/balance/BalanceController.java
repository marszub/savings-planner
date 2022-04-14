package pl.edu.agh.kuce.planner.balance;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.kuce.planner.auth.Current;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.balance.dto.BalanceDto;
import pl.edu.agh.kuce.planner.balance.service.BalanceService;

import javax.validation.Valid;

@RestController
@RequestMapping("api/balance")
public class BalanceController {
    private final BalanceService balanceService;

    public BalanceController(final BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @PostMapping("/create")
    public void create(@Current final User user, @Valid @RequestBody final Integer balance) {
        balanceService.create(user, balance);
    }

    @PostMapping("/update")
    public void update(@Current final User user, @Valid @RequestBody final Integer balance) {
        balanceService.update(user, balance);
    }

    @PostMapping("/list")
    public BalanceDto list(@Current final User user) {
        return balanceService.list(user);
    }
}
