package pl.edu.agh.kuce.planner.balance.service;

import pl.edu.agh.kuce.planner.shared.ResourceNotFoundException;

public class BalanceNotFoundException extends ResourceNotFoundException {

    public BalanceNotFoundException() {
        super("Balance with that id does not exist");
    }

    public BalanceNotFoundException(final String string) {
        super(string);
    }
}
