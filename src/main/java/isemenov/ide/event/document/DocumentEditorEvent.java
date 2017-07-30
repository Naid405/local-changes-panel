package isemenov.ide.event.document;

import isemenov.ide.DocumentEditor;
import isemenov.ide.event.editor.EditorFileEvent;

import javax.swing.text.Document;
import java.nio.file.Path;

public abstract class DocumentEditorEvent extends EditorFileEvent {
    private final Document document;

    public DocumentEditorEvent(Path file, DocumentEditor documentEditor, Document document) {
        super(file, documentEditor);
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }
}
