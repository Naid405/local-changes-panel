package isemenov.ide.event.document;

import isemenov.ide.DocumentEditor;
import isemenov.ide.ProjectFile;

import javax.swing.text.Document;

public class DocumentChangedEvent extends DocumentEditorEvent {
    public DocumentChangedEvent(ProjectFile file,
                                DocumentEditor documentEditor,
                                Document document) {
        super(file, documentEditor, document);
    }
}
