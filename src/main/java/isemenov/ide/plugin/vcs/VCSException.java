package isemenov.ide.plugin.vcs;

public class VCSException extends Exception {
    public VCSException() {
        super();
    }

    public VCSException(String message) {
        super(message);
    }

    public VCSException(String message, Throwable cause) {
        super(message, cause);
    }

    public VCSException(Throwable cause) {
        super(cause);
    }

    protected VCSException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
