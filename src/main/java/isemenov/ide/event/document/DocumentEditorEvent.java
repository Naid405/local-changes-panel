package isemenov.ide.event.document;

import isemenov.ide.DocumentEditor;
import isemenov.ide.ProjectFile;
import isemenov.ide.event.editor.EditorFileEvent;

import javax.swing.text.Document;

public abstract class DocumentEditorEvent extends EditorFileEvent {
    private final Document document;

    public DocumentEditorEvent(ProjectFile file, DocumentEditor documentEditor, Document document) {
        super(file, documentEditor);
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }
}
