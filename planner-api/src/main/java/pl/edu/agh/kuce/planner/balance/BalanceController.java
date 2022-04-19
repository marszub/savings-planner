package pl.edu.agh.kuce.planner.balance;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    // This mapping is not used because balance is created automatically while creating user profile
    // but this could be usefull while adding subbalances
    //     @PostMapping("")
    //     public void create(@Current final User user, @Valid @RequestBody final Integer balance) {
    //         balanceService.create(user, balance);
    //     }

    @PutMapping("")
    public void update(@Current final User user, @Valid @RequestBody final BalanceDto balanceDto) {
        balanceService.update(user, balanceDto);
    }

    @GetMapping("")
    public BalanceDto list(@Current final User user) {
        return balanceService.list(user);
    }
}
