package isemenov.ide.vcs;

public class CannotExecuteVCSOperation extends VCSException {
    public CannotExecuteVCSOperation(String message) {
        super(message);
    }

    public CannotExecuteVCSOperation(Throwable cause) {
        super(cause);
    }
}
