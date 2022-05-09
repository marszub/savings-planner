package pl.edu.agh.kuce.planner.balance;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.edu.agh.kuce.planner.auth.Current;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.balance.dto.SingleSubBalanceDto;
import pl.edu.agh.kuce.planner.balance.dto.SubBalanceDto;
import pl.edu.agh.kuce.planner.balance.service.SubBalanceService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/subBalances")
public class SubBalanceController {
    private final SubBalanceService subBalanceService;

    public SubBalanceController(final SubBalanceService subBalanceService) {
        this.subBalanceService = subBalanceService;
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void create(@Current final User user, @Valid @RequestBody final SubBalanceDto subBalanceDto) {
        subBalanceService.create(user, subBalanceDto);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public SubBalanceDto list(@Current final User user) {
        return subBalanceService.list(user);
    }

    @DeleteMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Current final User user) {
        subBalanceService.clear(user);
    }

    @PostMapping("/single")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createSingle(@Current final User user,
                             @Valid @RequestBody final SingleSubBalanceDto singleSubBalanceDto) {
        subBalanceService.createSingle(user, singleSubBalanceDto);
    }

    @PutMapping("/single/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSingle(@Current final User user,
                             @Valid @RequestBody final SingleSubBalanceDto singleSubBalanceDto,
                             @PathVariable("id") final Integer subBalanceIndex) {
        subBalanceService.updateSingle(user, subBalanceIndex, singleSubBalanceDto);
    }

    @GetMapping("/count")
    @ResponseStatus(HttpStatus.CREATED)
    public Integer countSubBalances(@Current final User user) {
        return subBalanceService.countSubBalances(user);
    }
}
