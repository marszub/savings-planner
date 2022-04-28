package pl.edu.agh.kuce.planner.goal;

public class GoalNotFoundException extends RuntimeException {
    public GoalNotFoundException(final String string) {
        super(string);
    }
}
