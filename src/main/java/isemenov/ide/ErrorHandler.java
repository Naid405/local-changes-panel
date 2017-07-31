package isemenov.ide;

import isemenov.ide.event.ConcurrentEventManager;
import isemenov.ide.event.EventManager;
import isemenov.ide.event.ide.error.ErrorLevel;
import isemenov.ide.event.ide.error.ErrorOccuredEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class ErrorHandler {
    private static final Logger logger = LogManager.getLogger(ErrorHandler.class);

    private final EventManager eventManager;

    public ErrorHandler() {
        this.eventManager = new ConcurrentEventManager();
    }

    public void error(Exception e) {
        logger.error(e.getMessage(), e);
        eventManager.fireEventListeners(this, new ErrorOccuredEvent(ErrorLevel.ERROR, e));
    }

    public void warn(Exception e) {
        logger.warn(e.getMessage());
        eventManager.fireEventListeners(this, new ErrorOccuredEvent(ErrorLevel.WARN, e));
    }

    public void addErrorListener(Consumer<ErrorOccuredEvent> listener) {
        eventManager.addEventListener(ErrorOccuredEvent.class, listener);
    }
}
