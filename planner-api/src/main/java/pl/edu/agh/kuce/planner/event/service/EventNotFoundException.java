package pl.edu.agh.kuce.planner.event.service;

import pl.edu.agh.kuce.planner.shared.ResourceNotFoundException;

public class EventNotFoundException extends ResourceNotFoundException {
    public EventNotFoundException() {
        super("This event does not exist");
    }
}
