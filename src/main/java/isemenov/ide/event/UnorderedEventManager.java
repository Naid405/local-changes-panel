package isemenov.ide.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

public class UnorderedEventManager implements EventManager {
    private static final Logger logger = LogManager.getLogger(UnorderedEventManager.class);

    protected final ConcurrentMap<Class<? extends Event>, Set<Consumer<Event>>> eventListeners;

    public UnorderedEventManager() {
        eventListeners = new ConcurrentHashMap<>();
    }

    @Override
    public void fireEventListeners(Object source, Event event) {
        Set<Consumer<Event>> listeners = eventListeners.get(event.getClass());
        if (listeners.isEmpty())
            return;

        for (Consumer<Event> consumer : listeners) {
            try {
                consumer.accept(event);
            } catch (Exception e) {
                logger.warn(
                        "Error while notifying listeners about " + event.getClass() + " emitted by " + source.getClass(),
                        e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends Event> void addEventListener(Class<C> eventClass, Consumer<C> listener) {
        Set<Consumer<Event>> listeners =
                eventListeners.computeIfAbsent(eventClass, (key) -> ConcurrentHashMap.newKeySet());
        listeners.add((Consumer<Event>) listener);
    }
}
