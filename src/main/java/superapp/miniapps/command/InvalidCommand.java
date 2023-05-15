package superapp.miniapps.command;

public class InvalidCommand {

    private String errorMessage;

    public InvalidCommand(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public InvalidCommand setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    @Override
    public String toString() {
        return "InvalidCommand{" +
                "errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
