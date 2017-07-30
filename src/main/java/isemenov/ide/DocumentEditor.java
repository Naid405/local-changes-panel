package isemenov.ide;

import isemenov.ide.event.ConcurrentEventManager;
import isemenov.ide.event.EventManager;
import isemenov.ide.event.document.DocumentBeingSavedEvent;
import isemenov.ide.event.document.DocumentChangedEvent;
import isemenov.ide.event.document.DocumentSavedEvent;
import isemenov.ide.ui.action.AsyncChangeNotifyingDocumentListener;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

public final class DocumentEditor {
    private final EditorKit editorKit;
    private final Document document;
    private final AsyncChangeNotifyingDocumentListener documentChangeListener;

    private final EventManager eventManager;

    private volatile boolean documentChanged;

    public DocumentEditor(EditorKit editorKit, Path filePath) {
        Objects.requireNonNull(editorKit);
        Objects.requireNonNull(filePath);

        this.eventManager = new ConcurrentEventManager();

        this.editorKit = editorKit;
        this.document = editorKit.createDefaultDocument();
        this.document.putProperty(Document.StreamDescriptionProperty, filePath);
        this.documentChangeListener = new AsyncChangeNotifyingDocumentListener(e -> {
            if (!this.documentChanged) {
                this.documentChanged = true;
                this.eventManager.fireEventListeners(this,
                                                     new DocumentChangedEvent(
                                                             (Path) document.getProperty(
                                                                     Document.StreamDescriptionProperty),
                                                             this,
                                                             this.document));
            }
        });
    }

    public EditorKit getEditorKit() {
        return editorKit;
    }

    public Document getDocument() {
        return this.document;
    }

    public void readDocument() throws IOException {
        synchronized (document) {
            Path file = (Path) document.getProperty(Document.StreamDescriptionProperty);
            try (Reader reader = Files.newBufferedReader(file)) {
                //To not fire the file change events since technically file is unchanged after it's read
                document.removeDocumentListener(documentChangeListener);
                //Otherwise kit.read attaches to the end of document TODO: try to avoid
                document.remove(0, document.getLength());
                editorKit.read(reader, document, 0);
                document.addDocumentListener(documentChangeListener);

                //Kind of a hack required to reset file state when it is re-read
                this.eventManager.fireEventListeners(this,
                                                     new DocumentSavedEvent(file,
                                                                            this,
                                                                            this.document));
            } catch (BadLocationException e) {
                throw new IOException(e.getMessage(), e);
            }
        }
    }

    public void writeDocument() throws IOException {
        synchronized (document) {
            Path file = (Path) document.getProperty(Document.StreamDescriptionProperty);
            this.eventManager.fireEventListeners(this,
                                                 new DocumentBeingSavedEvent(file,
                                                                             this,
                                                                             this.document));
            try (Writer writer = Files.newBufferedWriter(file)) {
                this.editorKit.write(writer, this.document, 0, document.getLength());
                documentChanged = false;
                this.eventManager.fireEventListeners(this,
                                                     new DocumentSavedEvent(file,
                                                                            this,
                                                                            this.document));
            } catch (BadLocationException e) {
                throw new IOException(e.getMessage(), e);
            }
        }
    }


    public void addDocumentChangedListener(Consumer<DocumentChangedEvent> listener) {
        Objects.requireNonNull(listener);
        eventManager.addEventListener(DocumentChangedEvent.class, listener);
    }

    public void addDocumentBeingSavedListener(Consumer<DocumentBeingSavedEvent> listener) {
        Objects.requireNonNull(listener);
        eventManager.addEventListener(DocumentBeingSavedEvent.class, listener);
    }

    public void addDocumentSavedListener(Consumer<DocumentSavedEvent> listener) {
        Objects.requireNonNull(listener);
        eventManager.addEventListener(DocumentSavedEvent.class, listener);
    }
}
