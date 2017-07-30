package isemenov.ide.ui.action;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.function.Consumer;

public class AsyncChangeNotifyingDocumentListener implements DocumentListener {
    private final Consumer<DocumentEvent> listener;

    public AsyncChangeNotifyingDocumentListener(Consumer<DocumentEvent> listener) {
        this.listener = listener;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                listener.accept(e);
                return null;
            }
        }.execute();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                listener.accept(e);
                return null;
            }
        }.execute();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }
}
