package isemenov.ide.ui.component;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.function.Consumer;

public class OneTimeNotifyingDocumentEditsObserver implements DocumentListener {
    private final Consumer<Boolean> listener;
    //true - will emit one "edited" notification and switch to false
    //false - will emit one "not edited" notification and switch to true
    private boolean notificationFlag;

    public OneTimeNotifyingDocumentEditsObserver(Consumer<Boolean> listener) {
        this.listener = listener;
        this.notificationFlag = true;
    }

    public synchronized void reset() {
        if (!notificationFlag) {
            listener.accept(false);
        }
        notificationFlag = true;
    }

    @Override
    public synchronized void insertUpdate(DocumentEvent e) {
        if (notificationFlag)
            listener.accept(true);
        notificationFlag = false;
    }

    @Override
    public synchronized void removeUpdate(DocumentEvent e) {
        if (notificationFlag)
            listener.accept(true);
        notificationFlag = false;
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }
}
