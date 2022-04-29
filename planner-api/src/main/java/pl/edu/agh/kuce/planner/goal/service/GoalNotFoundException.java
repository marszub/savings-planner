package pl.edu.agh.kuce.planner.goal.service;

import pl.edu.agh.kuce.planner.shared.ResourceNotFoundException;

public class GoalNotFoundException extends ResourceNotFoundException {

    public GoalNotFoundException() {
        super("Goal with that id does not exist");
    }
}
