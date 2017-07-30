package isemenov.ide.event;

import java.util.function.Consumer;

public interface EventManager {
    void fireEventListeners(Object source, Event event);

    <C extends Event> void addEventListener(Class<C> eventClass, Consumer<C> listener);
}
