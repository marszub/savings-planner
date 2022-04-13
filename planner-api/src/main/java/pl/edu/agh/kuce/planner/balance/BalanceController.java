package pl.edu.agh.kuce.planner.balance;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.kuce.planner.balance.service.BalanceService;

@RestController
@RequestMapping("api/balance")
public class BalanceController {
    private final BalanceService balanceService;

    public BalanceController(final BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @PostMapping("/create")
    public void create() {

    }

    @PostMapping("/update")
    public void update() {

    }

    @PostMapping("/list")
    public void list() {

    }
}
