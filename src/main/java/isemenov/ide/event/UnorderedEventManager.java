package isemenov.ide.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

public class UnorderedEventManager implements EventManager {
    private static final Logger logger = LogManager.getLogger(UnorderedEventManager.class);

    private final ConcurrentMap<Class<? extends Event>, Set<Consumer<Event>>> eventListeners;

    public UnorderedEventManager() {
        eventListeners = new ConcurrentHashMap<>();
    }

    @Override
    public void fireEventListeners(Object source, Event event) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(event);

        logger.debug("Event occured - source: " + source.getClass().toString() +
                             " event: " + event.getClass().toString());
        Set<Consumer<Event>> listeners = eventListeners.get(event.getClass());
        if (listeners == null || listeners.isEmpty())
            return;

        for (Consumer<Event> consumer : listeners) {
            try {
                consumer.accept(event);
            } catch (Exception e) {
                logger.warn(
                        "Error while notifying listeners about " + event.getClass() + " emitted by " + source
                                .getClass(),
                        e);
            }
        }
    }

    @Override
    public CompletableFuture fireEventListenersAsync(Object source, Event event) {
        return CompletableFuture.runAsync(() -> fireEventListeners(source, event));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends Event> void addEventListener(Class<C> eventClass, Consumer<C> listener) {
        Objects.requireNonNull(eventClass);
        Objects.requireNonNull(listener);

        Set<Consumer<Event>> listeners =
                eventListeners.computeIfAbsent(eventClass, (key) -> ConcurrentHashMap.newKeySet());
        listeners.add((Consumer<Event>) listener);
    }
}
