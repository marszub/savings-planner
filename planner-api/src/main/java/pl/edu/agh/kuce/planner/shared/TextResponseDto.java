package pl.edu.agh.kuce.planner.shared;

public class TextResponseDto {

    private String message;

    public TextResponseDto() {}

    public TextResponseDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
