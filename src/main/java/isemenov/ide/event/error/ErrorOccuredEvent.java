package isemenov.ide.event.error;

import isemenov.ide.event.IDEEvent;

public class ErrorOccuredEvent extends IDEEvent {
    private final ErrorLevel errorLevel;
    private final Exception exception;

    public ErrorOccuredEvent(ErrorLevel errorLevel, Exception exception) {
        this.errorLevel = errorLevel;
        this.exception = exception;
    }

    public ErrorLevel getErrorLevel() {
        return errorLevel;
    }

    public Exception getException() {
        return exception;
    }
}
