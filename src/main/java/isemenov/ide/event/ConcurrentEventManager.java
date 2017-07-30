package isemenov.ide.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

public class ConcurrentEventManager extends UnorderedEventManager {
    private static final Logger logger = LogManager.getLogger(ConcurrentEventManager.class);

    @Override
    public void fireEventListeners(Object source, Event event) {
        Set<Consumer<Event>> listeners = super.eventListeners.get(event.getClass());
        if (listeners.isEmpty())
            return;

        List<CompletableFuture> futures = new ArrayList<>();
        for (Consumer<Event> consumer : listeners)
            futures.add(CompletableFuture.runAsync(() -> consumer.accept(event)));

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
        } catch (CompletionException e) {
            logger.warn(
                    "Error while notifying listeners about " + event.getClass() + " emitted by " + source.getClass(),
                    e);
        }
    }
}
