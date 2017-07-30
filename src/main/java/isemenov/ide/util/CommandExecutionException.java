package isemenov.ide.util;

public class CommandExecutionException extends Exception {
    public CommandExecutionException(String command, String error) {
        super("Error occured while executing command " + command + ": " + error);
    }

    public CommandExecutionException(String command, Exception cause) {
        super("Error occured while executing command " + command, cause);
    }
}
