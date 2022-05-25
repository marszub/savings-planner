package pl.edu.agh.kuce.planner.shared;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(final String string) {
        super(string);
    }
}
