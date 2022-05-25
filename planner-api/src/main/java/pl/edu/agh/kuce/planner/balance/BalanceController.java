package pl.edu.agh.kuce.planner.balance;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.kuce.planner.auth.Current;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.balance.dto.BalanceData;
import pl.edu.agh.kuce.planner.balance.dto.SubBalanceData;
import pl.edu.agh.kuce.planner.balance.dto.SubBalanceInputData;
import pl.edu.agh.kuce.planner.balance.service.BalanceService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/balance")
public class BalanceController {
    private final BalanceService balanceService;

    public BalanceController(final BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @PutMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Current final User user, @Valid @RequestBody final BalanceData balanceData) {
        balanceService.update(user, balanceData);
    }

    @GetMapping("")
    public BalanceData list(@Current final User user) {
        return balanceService.list(user);
    }

    @PostMapping("/sub")
    @ResponseStatus(HttpStatus.CREATED)
    public SubBalanceData createSub(@Current final User user,
                                    @Valid @RequestBody final SubBalanceInputData subBalanceInputData) {
        return balanceService.createSub(user, subBalanceInputData);
    }

    @DeleteMapping("/sub")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllSub(@Current final User user) {
        balanceService.deleteSub(user);
    }

    @GetMapping("/sub/{id}")
    public SubBalanceData listSub(@Current final User user, @PathVariable("id") final Integer subBalanceId) {
        return balanceService.listSub(user, subBalanceId);
    }

    @PutMapping("/sub/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSub(@Current final User user,
                          @PathVariable("id") final Integer subBalanceId,
                          @Valid @RequestBody final SubBalanceInputData subBalanceInputData) {
        balanceService.updateSub(user, subBalanceId, subBalanceInputData);
    }
}
