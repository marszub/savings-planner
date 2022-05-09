package pl.edu.agh.kuce.planner.shared;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(final String string) {
        super(string);
    }
}
