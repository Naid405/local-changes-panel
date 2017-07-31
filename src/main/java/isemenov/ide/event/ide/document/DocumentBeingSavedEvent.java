package isemenov.ide.event.ide.document;

import isemenov.ide.DocumentEditor;

import javax.swing.text.Document;
import java.nio.file.Path;

public class DocumentBeingSavedEvent extends DocumentEditorEvent {
    public DocumentBeingSavedEvent(Path file,
                                   DocumentEditor documentEditor,
                                   Document document) {
        super(file, documentEditor, document);
    }
}
