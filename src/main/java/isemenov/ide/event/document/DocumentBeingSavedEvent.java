package isemenov.ide.event.document;

import isemenov.ide.DocumentEditor;
import isemenov.ide.ProjectFile;

import javax.swing.text.Document;

public class DocumentBeingSavedEvent extends DocumentEditorEvent {
    public DocumentBeingSavedEvent(ProjectFile file,
                                   DocumentEditor documentEditor,
                                   Document document) {
        super(file, documentEditor, document);
    }
}
