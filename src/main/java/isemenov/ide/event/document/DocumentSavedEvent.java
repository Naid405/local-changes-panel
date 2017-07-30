package isemenov.ide.event.document;

import isemenov.ide.DocumentEditor;
import isemenov.ide.ProjectFile;

import javax.swing.text.Document;

public class DocumentSavedEvent extends DocumentEditorEvent {
    public DocumentSavedEvent(ProjectFile file,
                              DocumentEditor documentEditor,
                              Document document) {
        super(file, documentEditor, document);
    }
}
